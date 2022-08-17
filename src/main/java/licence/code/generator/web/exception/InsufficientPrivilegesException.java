package licence.code.generator.web.exception;

public final class InsufficientPrivilegesException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366287163L;

    public InsufficientPrivilegesException() {
        super();
    }

    public InsufficientPrivilegesException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InsufficientPrivilegesException(final String message) {
        super(message);
    }

    public InsufficientPrivilegesException(final Throwable cause) {
        super(cause);
    }

}
