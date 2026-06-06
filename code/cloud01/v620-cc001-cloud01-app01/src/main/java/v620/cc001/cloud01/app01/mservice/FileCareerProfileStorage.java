package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.CareerProfileDraftDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Durable local adapter used until the Cosmic datamodel storage is implemented.
 */
public class FileCareerProfileStorage implements CareerProfileStorage {

    public static final String STORAGE_DIR_PROPERTY = "cc001.career.profile.storage.dir";

    private final File baseDir;

    public FileCareerProfileStorage() {
        this(defaultBaseDir());
    }

    public FileCareerProfileStorage(File baseDir) {
        this.baseDir = baseDir;
    }

    public synchronized UserProfileSnapshot loadSnapshot(String userId) {
        Object value = read(userId, "snapshot.ser");
        return value instanceof UserProfileSnapshot ? (UserProfileSnapshot) value : new UserProfileSnapshot();
    }

    public synchronized void saveSnapshot(String userId, UserProfileSnapshot snapshot) {
        write(userId, "snapshot.ser", snapshot == null ? new UserProfileSnapshot() : snapshot);
    }

    @SuppressWarnings("unchecked")
    public synchronized Map<String, String> loadFacts(String userId) {
        Object value = read(userId, "facts.ser");
        return value instanceof Map
                ? new LinkedHashMap<String, String>((Map<String, String>) value)
                : new LinkedHashMap<String, String>();
    }

    public synchronized void saveFact(String userId, String key, String value) {
        if (!hasText(key) || !hasText(value)) {
            return;
        }
        Map<String, String> facts = loadFacts(userId);
        facts.put(key.trim(), value.trim());
        write(userId, "facts.ser", (Serializable) new LinkedHashMap<String, String>(facts));
    }

    public synchronized CareerUserProfileDto loadProfile(String userId) {
        Object value = read(userId, "profile.ser");
        return value instanceof CareerUserProfileDto ? (CareerUserProfileDto) value : null;
    }

    public synchronized void saveProfile(String userId, CareerUserProfileDto profile) {
        if (profile != null) {
            write(userId, "profile.ser", profile);
        }
    }

    public synchronized CareerProfileDraftDto loadDraft(String userId) {
        Object value = read(userId, "draft.ser");
        return value instanceof CareerProfileDraftDto ? (CareerProfileDraftDto) value : new CareerProfileDraftDto();
    }

    public synchronized void saveDraft(String userId, CareerProfileDraftDto draft) {
        write(userId, "draft.ser", draft == null ? new CareerProfileDraftDto() : draft);
    }

    public synchronized void clearDraft(String userId) {
        File file = file(userId, "draft.ser");
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("Unable to delete career profile draft file: " + file.getAbsolutePath());
        }
    }

    private Object read(String userId, String fileName) {
        File file = file(userId, fileName);
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

    private void write(String userId, String fileName, Serializable value) {
        File file = file(userId, fileName);
        File dir = file.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("Unable to create career profile storage directory: " + dir.getAbsolutePath());
        }

        File temp = new File(dir, fileName + ".tmp");
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(temp));
            output.writeObject(value);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to write career profile storage file: " + temp.getAbsolutePath(), e);
        } finally {
            close(output);
        }

        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("Unable to replace career profile storage file: " + file.getAbsolutePath());
        }
        if (!temp.renameTo(file)) {
            throw new IllegalStateException("Unable to move career profile storage file into place: " + file.getAbsolutePath());
        }
    }

    private File file(String userId, String fileName) {
        return new File(new File(baseDir, sanitize(userId)), fileName);
    }

    private String sanitize(String userId) {
        if (!hasText(userId)) {
            throw new IllegalArgumentException("userId is required");
        }
        return userId.trim().replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private void close(ObjectInputStream input) {
        if (input == null) {
            return;
        }
        try {
            input.close();
        } catch (Exception ignored) {
        }
    }

    private void close(ObjectOutputStream output) {
        if (output == null) {
            return;
        }
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
        return new File("filestorage/career-profile");
    }
}
