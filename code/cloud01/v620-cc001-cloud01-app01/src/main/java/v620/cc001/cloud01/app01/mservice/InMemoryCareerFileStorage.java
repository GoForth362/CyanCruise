package v620.cc001.cloud01.app01.mservice;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class InMemoryCareerFileStorage implements CareerFileStorage {

    private final Map<String, byte[]> objects = new LinkedHashMap<String, byte[]>();
    private boolean previewAvailable = true;

    public void setPreviewAvailable(boolean previewAvailable) {
        this.previewAvailable = previewAvailable;
    }

    public void put(String objectKey, byte[] bytes, String originalFilename) {
        objects.put(objectKey, Arrays.copyOf(bytes, bytes.length));
    }

    public byte[] get(String objectKey) {
        byte[] bytes = objects.get(objectKey);
        return bytes == null ? null : Arrays.copyOf(bytes, bytes.length);
    }

    public String presign(String objectKey, long ttlSeconds) {
        if (!previewAvailable) {
            return null;
        }
        return "https://careerloop.local/files/" + objectKey + "?ttl=" + ttlSeconds;
    }

    public boolean delete(String objectKey) {
        return objects.remove(objectKey) != null;
    }

    public boolean previewAvailable() {
        return previewAvailable;
    }
}
