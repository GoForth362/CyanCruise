package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.FileConstants;
import v620.cc001.base.common.dto.career.FileReferenceDto;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class InMemoryCareerFileStorage implements CareerFileStorage {

    private final Map<String, byte[]> objects = new LinkedHashMap<String, byte[]>();
    private boolean previewAvailable = true;

    public void setPreviewAvailable(boolean previewAvailable) {
        this.previewAvailable = previewAvailable;
    }

    public FileReferenceDto put(FileReferenceDto reference, byte[] bytes) {
        reference.setProvider(FileConstants.PROVIDER_LOCAL);
        objects.put(reference.getObjectKey(), Arrays.copyOf(bytes, bytes.length));
        return reference;
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

    public String providerName() {
        return FileConstants.PROVIDER_LOCAL;
    }
}
