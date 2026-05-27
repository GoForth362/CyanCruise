package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Durable local diagnosis storage used until Cosmic datamodel storage is implemented.
 */
public class FileResumeDiagnosisStorage implements ResumeDiagnosisStorage {

    public static final String STORAGE_DIR_PROPERTY = "cc001.resume.diagnosis.storage.dir";

    private final File baseDir;

    public FileResumeDiagnosisStorage() {
        this(defaultBaseDir());
    }

    public FileResumeDiagnosisStorage(File baseDir) {
        this.baseDir = baseDir;
    }

    public synchronized ResumeDiagnosisResultDto saveDiagnosis(ResumeDiagnosisResultDto result) {
        if (result != null && result.getResumeId() != null) {
            Map<Long, ResumeDiagnosisResultDto> map = loadDiagnoses();
            map.put(result.getResumeId(), result);
            write("diagnoses.ser", (Serializable) new LinkedHashMap<Long, ResumeDiagnosisResultDto>(map));
        }
        return result;
    }

    public synchronized ResumeDiagnosisResultDto loadDiagnosis(Long resumeId) {
        return resumeId == null ? null : loadDiagnoses().get(resumeId);
    }

    public synchronized ResumeKeywordStatusDto saveKeywordStatus(ResumeKeywordStatusDto status) {
        if (status != null && status.getResumeId() != null) {
            Map<Long, ResumeKeywordStatusDto> map = loadKeywordStatuses();
            map.put(status.getResumeId(), status);
            write("keywords.ser", (Serializable) new LinkedHashMap<Long, ResumeKeywordStatusDto>(map));
        }
        return status;
    }

    public synchronized ResumeKeywordStatusDto loadKeywordStatus(Long resumeId) {
        return resumeId == null ? null : loadKeywordStatuses().get(resumeId);
    }

    @SuppressWarnings("unchecked")
    private Map<Long, ResumeDiagnosisResultDto> loadDiagnoses() {
        Object value = read("diagnoses.ser");
        return value instanceof Map
                ? new LinkedHashMap<Long, ResumeDiagnosisResultDto>((Map<Long, ResumeDiagnosisResultDto>) value)
                : new LinkedHashMap<Long, ResumeDiagnosisResultDto>();
    }

    @SuppressWarnings("unchecked")
    private Map<Long, ResumeKeywordStatusDto> loadKeywordStatuses() {
        Object value = read("keywords.ser");
        return value instanceof Map
                ? new LinkedHashMap<Long, ResumeKeywordStatusDto>((Map<Long, ResumeKeywordStatusDto>) value)
                : new LinkedHashMap<Long, ResumeKeywordStatusDto>();
    }

    private Object read(String name) {
        File file = new File(baseDir, name);
        if (!file.isFile()) {
            return null;
        }
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new FileInputStream(file));
            return input.readObject();
        } catch (Exception ignored) {
            return null;
        } finally {
            close(input);
        }
    }

    private void write(String name, Serializable value) {
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            throw new IllegalStateException("Unable to create resume diagnosis storage directory: " + baseDir.getAbsolutePath());
        }
        File file = new File(baseDir, name);
        File temp = new File(baseDir, name + ".tmp");
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(temp));
            output.writeObject(value);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to write resume diagnosis storage file: " + temp.getAbsolutePath(), e);
        } finally {
            close(output);
        }
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("Unable to replace resume diagnosis storage file: " + file.getAbsolutePath());
        }
        if (!temp.renameTo(file)) {
            throw new IllegalStateException("Unable to move resume diagnosis storage file into place: " + file.getAbsolutePath());
        }
    }

    private void close(ObjectInputStream input) {
        if (input == null) return;
        try {
            input.close();
        } catch (Exception ignored) {
        }
    }

    private void close(ObjectOutputStream output) {
        if (output == null) return;
        try {
            output.close();
        } catch (Exception ignored) {
        }
    }

    private static File defaultBaseDir() {
        String configured = System.getProperty(STORAGE_DIR_PROPERTY);
        if (configured != null && configured.trim().length() > 0) {
            return new File(configured.trim());
        }
        return new File("filestorage/resume-diagnosis");
    }
}
