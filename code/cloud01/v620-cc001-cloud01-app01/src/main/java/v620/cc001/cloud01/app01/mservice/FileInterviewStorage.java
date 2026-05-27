package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.InterviewMessageDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;

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
 * Durable local interview adapter used until Cosmic datamodel storage exists.
 */
public class FileInterviewStorage implements InterviewStorage {

    public static final String STORAGE_DIR_PROPERTY = "cc001.interview.storage.dir";

    private final File baseDir;

    public FileInterviewStorage() {
        this(defaultBaseDir());
    }

    public FileInterviewStorage(File baseDir) {
        this.baseDir = baseDir;
    }

    public synchronized InterviewSessionDto saveInterview(InterviewSessionDto interview) {
        Map<Long, InterviewSessionDto> interviews = loadInterviews();
        long nextId = nextInterviewId(interviews);
        if (interview.getInterviewId() == null) {
            interview.setInterviewId(Long.valueOf(nextId));
        }
        interviews.put(interview.getInterviewId(), interview);
        write("interviews.ser", (Serializable) new LinkedHashMap<Long, InterviewSessionDto>(interviews));
        return interview;
    }

    public synchronized InterviewSessionDto loadInterview(Long interviewId) {
        return loadInterviews().get(interviewId);
    }

    public synchronized List<InterviewSessionDto> listByUser(String userId) {
        List<InterviewSessionDto> result = new ArrayList<InterviewSessionDto>();
        for (InterviewSessionDto interview : loadInterviews().values()) {
            if (interview != null && userId.equals(interview.getUserId())) {
                result.add(interview);
            }
        }
        Collections.sort(result, new Comparator<InterviewSessionDto>() {
            public int compare(InterviewSessionDto left, InterviewSessionDto right) {
                int startedAtOrder = compareStartedAt(left, right);
                if (startedAtOrder != 0) {
                    return startedAtOrder;
                }
                return compareInterviewId(left, right);
            }
        });
        return result;
    }

    private int compareStartedAt(InterviewSessionDto left, InterviewSessionDto right) {
        if (left.getStartedAt() == null && right.getStartedAt() == null) return 0;
        if (left.getStartedAt() == null) return 1;
        if (right.getStartedAt() == null) return -1;
        return right.getStartedAt().compareTo(left.getStartedAt());
    }

    private int compareInterviewId(InterviewSessionDto left, InterviewSessionDto right) {
        if (left.getInterviewId() == null && right.getInterviewId() == null) return 0;
        if (left.getInterviewId() == null) return 1;
        if (right.getInterviewId() == null) return -1;
        return right.getInterviewId().compareTo(left.getInterviewId());
    }

    public synchronized void deleteInterview(Long interviewId) {
        Map<Long, InterviewSessionDto> interviews = loadInterviews();
        interviews.remove(interviewId);
        write("interviews.ser", (Serializable) new LinkedHashMap<Long, InterviewSessionDto>(interviews));
    }

    public synchronized InterviewMessageDto saveMessage(InterviewMessageDto message) {
        Map<Long, List<InterviewMessageDto>> messages = loadMessages();
        if (message.getMessageId() == null) {
            message.setMessageId(Long.valueOf(nextMessageId(messages)));
        }
        List<InterviewMessageDto> list = messages.get(message.getInterviewId());
        if (list == null) {
            list = new ArrayList<InterviewMessageDto>();
            messages.put(message.getInterviewId(), list);
        }
        list.add(message);
        write("messages.ser", (Serializable) new LinkedHashMap<Long, List<InterviewMessageDto>>(messages));
        return message;
    }

    public synchronized List<InterviewMessageDto> listMessages(Long interviewId) {
        List<InterviewMessageDto> list = loadMessages().get(interviewId);
        return list == null ? new ArrayList<InterviewMessageDto>() : new ArrayList<InterviewMessageDto>(list);
    }

    public synchronized void deleteMessages(Long interviewId) {
        Map<Long, List<InterviewMessageDto>> messages = loadMessages();
        messages.remove(interviewId);
        write("messages.ser", (Serializable) new LinkedHashMap<Long, List<InterviewMessageDto>>(messages));
    }

    @SuppressWarnings("unchecked")
    private Map<Long, InterviewSessionDto> loadInterviews() {
        Object value = read("interviews.ser");
        return value instanceof Map
                ? new LinkedHashMap<Long, InterviewSessionDto>((Map<Long, InterviewSessionDto>) value)
                : new LinkedHashMap<Long, InterviewSessionDto>();
    }

    @SuppressWarnings("unchecked")
    private Map<Long, List<InterviewMessageDto>> loadMessages() {
        Object value = read("messages.ser");
        return value instanceof Map
                ? new LinkedHashMap<Long, List<InterviewMessageDto>>((Map<Long, List<InterviewMessageDto>>) value)
                : new LinkedHashMap<Long, List<InterviewMessageDto>>();
    }

    private long nextInterviewId(Map<Long, InterviewSessionDto> interviews) {
        long max = 0L;
        for (Long id : interviews.keySet()) {
            if (id != null && id.longValue() > max) max = id.longValue();
        }
        return max + 1L;
    }

    private long nextMessageId(Map<Long, List<InterviewMessageDto>> messages) {
        long max = 0L;
        for (List<InterviewMessageDto> list : messages.values()) {
            if (list == null) continue;
            for (InterviewMessageDto message : list) {
                if (message.getMessageId() != null && message.getMessageId().longValue() > max) {
                    max = message.getMessageId().longValue();
                }
            }
        }
        return max + 1L;
    }

    private Object read(String fileName) {
        File file = new File(baseDir, fileName);
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

    private void write(String fileName, Serializable value) {
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            throw new IllegalStateException("Unable to create interview storage directory: " + baseDir.getAbsolutePath());
        }
        File file = new File(baseDir, fileName);
        File temp = new File(baseDir, fileName + ".tmp");
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(temp));
            output.writeObject(value);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to write interview storage file: " + temp.getAbsolutePath(), e);
        } finally {
            close(output);
        }
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("Unable to replace interview storage file: " + file.getAbsolutePath());
        }
        if (!temp.renameTo(file)) {
            throw new IllegalStateException("Unable to move interview storage file into place: " + file.getAbsolutePath());
        }
    }

    private void close(ObjectInputStream input) {
        if (input == null) return;
        try { input.close(); } catch (Exception ignored) {}
    }

    private void close(ObjectOutputStream output) {
        if (output == null) return;
        try { output.close(); } catch (Exception ignored) {}
    }

    private static File defaultBaseDir() {
        String configured = System.getProperty(STORAGE_DIR_PROPERTY);
        if (configured != null && configured.trim().length() > 0) {
            return new File(configured.trim());
        }
        return new File("filestorage/interview-core");
    }
}
