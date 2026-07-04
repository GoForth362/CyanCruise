package v620.cc001.cloud01.app01.mservice.file;

import java.util.Map;

public interface BosFileServiceClient {

    boolean available();

    String upload(String objectKey, String originalFilename, byte[] bytes);

    Map<String, Object> preview(String originalFilename, String objectKey);

    byte[] download(String objectKey);

    void delete(String objectKey);

    String httpUrlPrefix();
}
