package licence.code.generator.web.exception;

public final class UnauthorizedUserException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366287163L;

    public UnauthorizedUserException() {
        super();
    }

    public UnauthorizedUserException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedUserException(final String message) {
        super(message);
    }

    public UnauthorizedUserException(final Throwable cause) {
        super(cause);
    }

}
