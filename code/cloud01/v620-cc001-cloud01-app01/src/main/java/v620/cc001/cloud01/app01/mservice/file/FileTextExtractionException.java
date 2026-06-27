package v620.cc001.cloud01.app01.mservice.file;

public class FileTextExtractionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String status;

    public FileTextExtractionException(String status, String message) {
        super(message);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
