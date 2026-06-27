package v620.cc001.cloud01.app01.mservice.auth;

public class IdentityBoundaryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String status;

    public IdentityBoundaryException(String status, String message) {
        super(message);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
