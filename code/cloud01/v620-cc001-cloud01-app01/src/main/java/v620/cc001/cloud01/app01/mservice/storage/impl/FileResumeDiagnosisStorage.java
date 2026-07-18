package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
            Map<Long, ResumeDiagnosisResultDto> history = loadHistory();
            long diagnosisId = result.getDiagnosisId() == null ? nextHistoryId(history) : result.getDiagnosisId().longValue();
            result.setDiagnosisId(Long.valueOf(diagnosisId));
            history.put(Long.valueOf(diagnosisId), result);
            write("history.ser", (Serializable) new LinkedHashMap<Long, ResumeDiagnosisResultDto>(history));
        }
        return result;
    }

    public synchronized ResumeDiagnosisResultDto loadDiagnosis(Long resumeId) {
        return resumeId == null ? null : loadDiagnoses().get(resumeId);
    }

    public synchronized List<ResumeDiagnosisResultDto> listDiagnoses(final String userId, final Long resumeId) {
        List<ResumeDiagnosisResultDto> result = new ArrayList<ResumeDiagnosisResultDto>();
        for (ResumeDiagnosisResultDto item : loadHistory().values()) {
            if (same(userId, item.getUserId()) && same(resumeId, item.getResumeId())) {
                result.add(item);
            }
        }
        Collections.sort(result, new Comparator<ResumeDiagnosisResultDto>() {
            public int compare(ResumeDiagnosisResultDto left, ResumeDiagnosisResultDto right) {
                if (left.getDiagnosedAt() == null) return right.getDiagnosedAt() == null ? 0 : 1;
                if (right.getDiagnosedAt() == null) return -1;
                return right.getDiagnosedAt().compareTo(left.getDiagnosedAt());
            }
        });
        return result;
    }

    public synchronized boolean deleteDiagnosis(String userId, Long diagnosisId) {
        Map<Long, ResumeDiagnosisResultDto> history = loadHistory();
        ResumeDiagnosisResultDto item = history.get(diagnosisId);
        if (item == null || !same(userId, item.getUserId())) {
            return false;
        }
        history.remove(diagnosisId);
        write("history.ser", (Serializable) new LinkedHashMap<Long, ResumeDiagnosisResultDto>(history));
        Map<Long, ResumeDiagnosisResultDto> latest = loadDiagnoses();
        ResumeDiagnosisResultDto latestItem = latest.get(item.getResumeId());
        if (latestItem != null && same(diagnosisId, latestItem.getDiagnosisId())) {
            List<ResumeDiagnosisResultDto> remaining = listDiagnoses(userId, item.getResumeId());
            if (remaining.isEmpty()) latest.remove(item.getResumeId());
            else latest.put(item.getResumeId(), remaining.get(0));
            write("diagnoses.ser", (Serializable) new LinkedHashMap<Long, ResumeDiagnosisResultDto>(latest));
        }
        return true;
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

    @SuppressWarnings("unchecked")
    private Map<Long, ResumeDiagnosisResultDto> loadHistory() {
        Object value = read("history.ser");
        return value instanceof Map
                ? new LinkedHashMap<Long, ResumeDiagnosisResultDto>((Map<Long, ResumeDiagnosisResultDto>) value)
                : new LinkedHashMap<Long, ResumeDiagnosisResultDto>();
    }

    private long nextHistoryId(Map<Long, ResumeDiagnosisResultDto> history) {
        long next = 1L;
        for (Long id : history.keySet()) {
            if (id != null && id.longValue() >= next) next = id.longValue() + 1L;
        }
        return next;
    }

    private boolean same(Object left, Object right) {
        return left == null ? right == null : left.equals(right);
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
