package v620.cc001.cloud01.app01.mservice.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import v620.cc001.base.common.dto.career.FileConstants;
import v620.cc001.base.common.dto.career.FileDeleteResult;
import v620.cc001.base.common.dto.career.FileReferenceDto;
import v620.cc001.base.common.dto.career.FileTextExtractionResult;
import v620.cc001.base.common.dto.career.FileUploadRequest;
import v620.cc001.base.common.dto.career.FileUploadResult;
import v620.cc001.base.common.dto.career.CareerRouteContext;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialDeleteResult;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialDto;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialUploadRequest;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.StudyCenterStorage;

/** Uploads, extracts and persists user-owned evidence for study route planning. */
public class StudyPlanningMaterialApplicationService {
    public static final int MAX_FILE_BYTES = 5 * 1024 * 1024;
    public static final int MAX_STORED_TEXT_CHARS = 20000;

    private final StudyCenterStorage storage;
    private final FileUploadPreviewApplicationService fileService;

    public StudyPlanningMaterialApplicationService() {
        this(CyanCruiseStorageFactory.studyCenterStorage(), new FileUploadPreviewApplicationService());
    }

    public StudyPlanningMaterialApplicationService(StudyCenterStorage storage,
                                                    FileUploadPreviewApplicationService fileService) {
        this.storage = storage;
        this.fileService = fileService;
    }

    public StudyPlanningMaterialDto upload(String userId, StudyPlanningMaterialUploadRequest request) {
        String safeUserId = requireText(userId, "用户身份");
        if (request == null || request.getFile() == null) {
            throw new IllegalArgumentException("请选择要上传的升学规划资料。");
        }
        String direction = requireDirection(request.getDirection());
        if (!CareerRouteContext.POSTGRADUATE.equals(direction)
                && !CareerRouteContext.RECOMMENDATION.equals(direction)) {
            throw new IllegalArgumentException("当前仅支持上传考研或保研规划资料。");
        }
        FileUploadRequest file = request.getFile();
        validateFile(file);
        file.setFolder("study-planning/" + safeUserId + "/" + direction.toLowerCase(Locale.ROOT));

        FileUploadResult uploaded = fileService.upload(file);
        if (uploaded == null || !FileConstants.STATUS_OK.equals(uploaded.getStatus())
                || uploaded.getFile() == null || !hasText(uploaded.getFile().getObjectKey())) {
            throw new IllegalStateException("资料上传失败，请稍后重试。");
        }
        FileReferenceDto reference = uploaded.getFile();
        try {
            FileTextExtractionResult extraction = fileService.extractText(reference.getObjectKey());
            StudyPlanningMaterialDto material = material(safeUserId, direction, request,
                    reference, extraction);
            return storage.saveMaterial(safeUserId, material);
        } catch (RuntimeException ex) {
            fileService.delete(reference.getObjectKey());
            throw ex;
        }
    }

    public List<StudyPlanningMaterialDto> list(String userId, String direction) {
        return storage.listMaterials(requireText(userId, "用户身份"), requireDirection(direction));
    }

    public StudyPlanningMaterialDeleteResult delete(String userId, String direction, String materialId) {
        String safeUserId = requireText(userId, "用户身份");
        String safeDirection = requireDirection(direction);
        String safeMaterialId = requireText(materialId, "资料标识");
        StudyPlanningMaterialDto material = storage.findMaterial(safeUserId, safeDirection, safeMaterialId);
        if (material == null) {
            throw new IllegalArgumentException("没有找到当前用户的这份资料。");
        }
        FileDeleteResult fileResult = fileService.delete(material.getObjectKey());
        if (fileResult != null && hasText(fileResult.getStatus())
                && !FileConstants.STATUS_OK.equals(fileResult.getStatus())
                && !FileConstants.STATUS_SKIPPED.equals(fileResult.getStatus())) {
            throw new IllegalStateException("资料文件暂时无法删除，请稍后重试。");
        }
        boolean deleted = storage.deleteMaterial(safeUserId, safeDirection, safeMaterialId);
        StudyPlanningMaterialDeleteResult result = new StudyPlanningMaterialDeleteResult();
        result.setMaterialId(safeMaterialId);
        result.setDeleted(Boolean.valueOf(deleted));
        result.setMessage(deleted ? "资料已删除。" : "资料已经不存在。");
        return result;
    }

    private StudyPlanningMaterialDto material(String userId, String direction,
                                              StudyPlanningMaterialUploadRequest request,
                                              FileReferenceDto reference,
                                              FileTextExtractionResult extraction) {
        LocalDateTime now = LocalDateTime.now();
        StudyPlanningMaterialDto material = new StudyPlanningMaterialDto();
        material.setMaterialId("spm-" + UUID.randomUUID().toString().replace("-", ""));
        material.setUserId(userId);
        material.setDirection(direction);
        material.setMaterialType(defaultText(request.getMaterialType(), "OTHER"));
        material.setTitle(defaultText(request.getTitle(), reference.getOriginalFilename()));
        material.setOriginalFilename(reference.getOriginalFilename());
        material.setObjectKey(reference.getObjectKey());
        material.setMediaType(request.getMediaType());
        material.setSizeBytes(reference.getSizeBytes());
        material.setExtractionStatus(extraction == null ? FileConstants.STATUS_FAILED : extraction.getStatus());
        material.setExtractionMessage(extraction == null ? "资料正文读取失败。" : extraction.getMessage());
        String text = extraction == null ? null : extraction.getText();
        boolean truncated = text != null && text.length() > MAX_STORED_TEXT_CHARS;
        if (truncated) text = text.substring(0, MAX_STORED_TEXT_CHARS);
        material.setExtractedText(text);
        material.setExtractedCharCount(Integer.valueOf(text == null ? 0 : text.length()));
        material.setTruncated(Boolean.valueOf(truncated || (extraction != null && Boolean.TRUE.equals(extraction.getTruncated()))));
        material.setCreatedAt(now);
        material.setUpdatedAt(now);
        return material;
    }

    private void validateFile(FileUploadRequest file) {
        if (file.getBytes() == null || file.getBytes().length == 0) {
            throw new IllegalArgumentException("请选择非空文件。");
        }
        if (file.getBytes().length > MAX_FILE_BYTES) {
            throw new IllegalArgumentException("单份考研资料不能超过 5MB。");
        }
        String filename = requireText(file.getOriginalFilename(), "文件名");
        String lower = filename.toLowerCase(Locale.ROOT);
        if (!(lower.endsWith(".pdf") || lower.endsWith(".doc") || lower.endsWith(".docx")
                || lower.endsWith(".txt") || lower.endsWith(".md"))) {
            throw new IllegalArgumentException("仅支持 PDF、Word、TXT 或 Markdown 资料。");
        }
    }

    private String requireDirection(String direction) {
        String value = requireText(direction, "升学方向").toUpperCase(Locale.ROOT);
        if (!CareerRouteContext.isStudyDirection(value)) {
            throw new IllegalArgumentException("请选择有效的升学方向。");
        }
        return value;
    }

    private String requireText(String value, String label) {
        if (!hasText(value)) throw new IllegalArgumentException(label + "不能为空。");
        return value.trim();
    }

    private String defaultText(String value, String fallback) {
        return hasText(value) ? value.trim() : fallback;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
