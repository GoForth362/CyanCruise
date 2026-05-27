package v620.cc001.cloud01.app01.mservice;

public interface FileTextExtractor {

    String extract(byte[] bytes, String objectKey);

    boolean available();
}
