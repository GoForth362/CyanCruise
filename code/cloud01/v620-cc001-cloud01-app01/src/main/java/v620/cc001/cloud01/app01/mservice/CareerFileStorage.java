package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.FileReferenceDto;

public interface CareerFileStorage {

    FileReferenceDto put(FileReferenceDto reference, byte[] bytes);

    byte[] get(String objectKey);

    String presign(String objectKey, long ttlSeconds);

    boolean delete(String objectKey);

    boolean previewAvailable();

    String providerName();
}
