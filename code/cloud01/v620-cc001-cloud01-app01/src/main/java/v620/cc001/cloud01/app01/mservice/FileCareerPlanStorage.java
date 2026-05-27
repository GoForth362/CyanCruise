package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.CareerPlanRecordDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Durable local career plan adapter used until Cosmic datamodel storage exists.
 */
public class FileCareerPlanStorage implements CareerPlanStorage {

    public static final String STORAGE_DIR_PROPERTY = "cc001.career.plan.storage.dir";

    private final File baseDir;

    public FileCareerPlanStorage() {
        this(defaultBaseDir());
    }

    public FileCareerPlanStorage(File baseDir) {
        this.baseDir = baseDir;
    }

    public synchronized CareerPlanRecordDto load(String userId) {
        File file = file(userId);
        if (!file.isFile()) {
            return null;
        }
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new FileInputStream(file));
            Object value = input.readObject();
            return value instanceof CareerPlanRecordDto ? (CareerPlanRecordDto) value : null;
        } catch (Exception ignored) {
            return null;
        } finally {
            close(input);
        }
    }

    public synchronized void save(String userId, CareerPlanRecordDto plan) {
        if (plan == null) {
            return;
        }
        File file = file(userId);
        File dir = file.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("Unable to create career plan storage directory: " + dir.getAbsolutePath());
        }

        File temp = new File(dir, "plan.ser.tmp");
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(temp));
            output.writeObject(plan);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to write career plan storage file: " + temp.getAbsolutePath(), e);
        } finally {
            close(output);
        }

        if (file.exists() && !file.delete()) {
            throw new IllegalStateException("Unable to replace career plan storage file: " + file.getAbsolutePath());
        }
        if (!temp.renameTo(file)) {
            throw new IllegalStateException("Unable to move career plan storage file into place: " + file.getAbsolutePath());
        }
    }

    public synchronized boolean exists(String userId) {
        return file(userId).isFile();
    }

    private File file(String userId) {
        return new File(new File(baseDir, sanitize(userId)), "plan.ser");
    }

    private String sanitize(String userId) {
        if (userId == null || userId.trim().length() == 0) {
            throw new IllegalArgumentException("userId is required");
        }
        return userId.trim().replaceAll("[^a-zA-Z0-9._-]", "_");
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
        return new File("filestorage/career-plan");
    }
}
