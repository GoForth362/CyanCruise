package v620.cc001.cloud01.app01.mservice.file;

public interface FileTextExtractor {

    String extract(byte[] bytes, String objectKey);

    boolean available();
}
