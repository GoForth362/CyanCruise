package v620.cc001.cloud01.app01.mservice;

public interface CareerFileStorage {

    void put(String objectKey, byte[] bytes, String originalFilename);

    byte[] get(String objectKey);

    String presign(String objectKey, long ttlSeconds);

    boolean delete(String objectKey);

    boolean previewAvailable();
}
