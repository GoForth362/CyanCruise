package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.AssistantChatMessageDto;
import v620.cc001.base.common.dto.career.AssistantChatSessionDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Durable local assistant chat adapter used until Cosmic datamodel storage exists.
 */
public class FileAssistantChatStorage implements AssistantChatStorage {

    public static final String STORAGE_DIR_PROPERTY = "cc001.assistant.chat.storage.dir";

    private final File baseDir;

    public FileAssistantChatStorage() {
        this(defaultBaseDir());
    }

    public FileAssistantChatStorage(File baseDir) {
        this.baseDir = baseDir;
    }

    public synchronized AssistantChatSessionDto saveSession(AssistantChatSessionDto session) {
        Map<Long, AssistantChatSessionDto> sessions = loadSessions();
        if (session.getSessionId() == null) {
            session.setSessionId(Long.valueOf(nextSessionId(sessions)));
        }
        sessions.put(session.getSessionId(), session);
        write("sessions.ser", (Serializable) new LinkedHashMap<Long, AssistantChatSessionDto>(sessions));
        return session;
    }

    public synchronized AssistantChatSessionDto loadSession(Long sessionId) {
        return loadSessions().get(sessionId);
    }

    public synchronized List<AssistantChatSessionDto> listSessions(String userId) {
        List<AssistantChatSessionDto> result = new ArrayList<AssistantChatSessionDto>();
        for (AssistantChatSessionDto session : loadSessions().values()) {
            if (session != null && userId.equals(session.getUserId())) {
                result.add(session);
            }
        }
        InMemoryAssistantChatStorage.sortSessions(result);
        return result;
    }

    public synchronized void deleteSession(Long sessionId) {
        Map<Long, AssistantChatSessionDto> sessions = loadSessions();
        sessions.remove(sessionId);
        write("sessions.ser", (Serializable) new LinkedHashMap<Long, AssistantChatSessionDto>(sessions));
    }

    public synchronized AssistantChatMessageDto saveMessage(AssistantChatMessageDto message) {
        Map<Long, List<AssistantChatMessageDto>> messages = loadMessages();
        if (message.getMsgId() == null) {
            message.setMsgId(Long.valueOf(nextMessageId(messages)));
        }
        List<AssistantChatMessageDto> list = messages.get(message.getSessionId());
        if (list == null) {
            list = new ArrayList<AssistantChatMessageDto>();
            messages.put(message.getSessionId(), list);
        }
        list.add(message);
        write("messages.ser", (Serializable) new LinkedHashMap<Long, List<AssistantChatMessageDto>>(messages));
        return message;
    }

    public synchronized List<AssistantChatMessageDto> listMessages(Long sessionId) {
        List<AssistantChatMessageDto> list = loadMessages().get(sessionId);
        return list == null ? new ArrayList<AssistantChatMessageDto>() : new ArrayList<AssistantChatMessageDto>(list);
    }

    public synchronized void deleteMessages(Long sessionId) {
        Map<Long, List<AssistantChatMessageDto>> messages = loadMessages();
        messages.remove(sessionId);
        write("messages.ser", (Serializable) new LinkedHashMap<Long, List<AssistantChatMessageDto>>(messages));
    }

    @SuppressWarnings("unchecked")
    private Map<Long, AssistantChatSessionDto> loadSessions() {
        Object value = read("sessions.ser");
        return value instanceof Map
                ? new LinkedHashMap<Long, AssistantChatSessionDto>((Map<Long, AssistantChatSessionDto>) value)
                : new LinkedHashMap<Long, AssistantChatSessionDto>();
    }

    @SuppressWarnings("unchecked")
    private Map<Long, List<AssistantChatMessageDto>> loadMessages() {
        Object value = read("messages.ser");
        return value instanceof Map
                ? new LinkedHashMap<Long, List<AssistantChatMessageDto>>((Map<Long, List<AssistantChatMessageDto>>) value)
                : new LinkedHashMap<Long, List<AssistantChatMessageDto>>();
    }

    private long nextSessionId(Map<Long, AssistantChatSessionDto> sessions) {
        long max = 0L;
        for (Long id : sessions.keySet()) {
            if (id != null && id.longValue() > max) max = id.longValue();
        }
        return max + 1L;
    }

    private long nextMessageId(Map<Long, List<AssistantChatMessageDto>> messages) {
        long max = 0L;
        for (List<AssistantChatMessageDto> list : messages.values()) {
            if (list == null) continue;
            for (AssistantChatMessageDto message : list) {
                if (message.getMsgId() != null && message.getMsgId().longValue() > max) {
                    max = message.getMsgId().longValue();
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
            throw new IllegalStateException("Unable to create assistant chat storage directory: " + baseDir.getAbsolutePath());
        }
        File file = new File(baseDir, fileName);
        File temp = new File(baseDir, fileName + ".tmp");
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(temp));
            output.writeObject(value);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to write assistant chat storage file: " + temp.getAbsolutePath(), e);
        } finally {
            close(output);
        }
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("Unable to replace assistant chat storage file: " + file.getAbsolutePath());
        }
        if (!temp.renameTo(file)) {
            throw new IllegalStateException("Unable to move assistant chat storage file into place: " + file.getAbsolutePath());
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
        return new File("filestorage/assistant-chat");
    }
}
