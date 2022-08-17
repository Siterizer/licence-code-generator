package licence.code.generator.web.exception;

public final class UserAlreadyBlockedException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366287163L;

    public UserAlreadyBlockedException() {
        super();
    }

    public UserAlreadyBlockedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyBlockedException(final String message) {
        super(message);
    }

    public UserAlreadyBlockedException(final Throwable cause) {
        super(cause);
    }

}
