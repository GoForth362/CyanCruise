package v620.cc001.cloud01.app01.mservice.file;

public class FileAdapterUnavailableException extends RuntimeException {

    private final String status;

    public FileAdapterUnavailableException(String status, String message) {
        super(message);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
