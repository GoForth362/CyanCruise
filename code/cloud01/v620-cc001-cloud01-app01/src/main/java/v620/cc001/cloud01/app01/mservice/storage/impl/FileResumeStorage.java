package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.ResumeRecordDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Durable local resume storage used until Cosmic datamodel storage is implemented.
 */
public class FileResumeStorage implements ResumeStorage {

    public static final String STORAGE_DIR_PROPERTY = "cc001.resume.storage.dir";

    private final File baseDir;

    public FileResumeStorage() {
        this(defaultBaseDir());
    }

    public FileResumeStorage(File baseDir) {
        this.baseDir = baseDir;
    }

    public synchronized ResumeRecordDto save(ResumeRecordDto record) {
        if (record == null) {
            throw new IllegalArgumentException("record is required");
        }
        Map<Long, ResumeRecordDto> records = loadAll();
        ResumeRecordDto copy = copy(record);
        if (copy.getResumeId() == null) {
            copy.setResumeId(nextId(records));
        }
        records.put(copy.getResumeId(), copy);
        writeAll(records);
        return copy(copy);
    }

    public synchronized ResumeRecordDto load(Long resumeId) {
        if (resumeId == null) {
            return null;
        }
        return copy(loadAll().get(resumeId));
    }

    public synchronized List<ResumeRecordDto> listByUser(String userId) {
        List<ResumeRecordDto> records = new ArrayList<ResumeRecordDto>();
        if (!hasText(userId)) {
            return records;
        }
        String safeUserId = userId.trim();
        for (ResumeRecordDto record : loadAll().values()) {
            if (record != null && safeUserId.equals(record.getUserId())) {
                records.add(copy(record));
            }
        }
        sortByUpdatedAtDesc(records);
        return records;
    }

    public synchronized void delete(Long resumeId) {
        if (resumeId == null) {
            return;
        }
        Map<Long, ResumeRecordDto> records = loadAll();
        if (records.remove(resumeId) != null) {
            writeAll(records);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Long, ResumeRecordDto> loadAll() {
        File file = file();
        if (!file.isFile()) {
            return new LinkedHashMap<Long, ResumeRecordDto>();
        }
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new FileInputStream(file));
            Object value = input.readObject();
            if (value instanceof Map) {
                return new LinkedHashMap<Long, ResumeRecordDto>((Map<Long, ResumeRecordDto>) value);
            }
        } catch (Exception ignored) {
        } finally {
            close(input);
        }
        return new LinkedHashMap<Long, ResumeRecordDto>();
    }

    private void writeAll(Map<Long, ResumeRecordDto> records) {
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            throw new IllegalStateException("Unable to create resume storage directory: " + baseDir.getAbsolutePath());
        }
        File file = file();
        File temp = new File(baseDir, "resumes.ser.tmp");
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(temp));
            output.writeObject((Serializable) new LinkedHashMap<Long, ResumeRecordDto>(records));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to write resume storage file: " + temp.getAbsolutePath(), e);
        } finally {
            close(output);
        }
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("Unable to replace resume storage file: " + file.getAbsolutePath());
        }
        if (!temp.renameTo(file)) {
            throw new IllegalStateException("Unable to move resume storage file into place: " + file.getAbsolutePath());
        }
    }

    private Long nextId(Map<Long, ResumeRecordDto> records) {
        long max = 0L;
        for (Long id : records.keySet()) {
            if (id != null && id.longValue() > max) {
                max = id.longValue();
            }
        }
        return Long.valueOf(max + 1L);
    }

    private File file() {
        return new File(baseDir, "resumes.ser");
    }

    private void sortByUpdatedAtDesc(List<ResumeRecordDto> records) {
        Collections.sort(records, new Comparator<ResumeRecordDto>() {
            public int compare(ResumeRecordDto left, ResumeRecordDto right) {
                if (left == right) return 0;
                if (left == null) return 1;
                if (right == null) return -1;
                if (left.getUpdatedAt() == null && right.getUpdatedAt() == null) return 0;
                if (left.getUpdatedAt() == null) return 1;
                if (right.getUpdatedAt() == null) return -1;
                return right.getUpdatedAt().compareTo(left.getUpdatedAt());
            }
        });
    }

    private ResumeRecordDto copy(ResumeRecordDto source) {
        if (source == null) {
            return null;
        }
        ResumeRecordDto copy = new ResumeRecordDto();
        copy.setResumeId(source.getResumeId());
        copy.setUserId(source.getUserId());
        copy.setTitle(source.getTitle());
        copy.setTargetJob(source.getTargetJob());
        copy.setFileKey(source.getFileKey());
        copy.setVersion(source.getVersion());
        copy.setStatus(source.getStatus());
        copy.setParsedContent(source.getParsedContent());
        copy.setDiagnosisScore(source.getDiagnosisScore());
        copy.setCreatedAt(source.getCreatedAt());
        copy.setUpdatedAt(source.getUpdatedAt());
        return copy;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
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
        return new File("filestorage/resume-core");
    }
}
