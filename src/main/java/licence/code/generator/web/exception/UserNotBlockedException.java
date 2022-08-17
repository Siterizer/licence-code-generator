package licence.code.generator.web.exception;

public final class UserNotBlockedException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366287163L;

    public UserNotBlockedException() {
        super();
    }

    public UserNotBlockedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserNotBlockedException(final String message) {
        super(message);
    }

    public UserNotBlockedException(final Throwable cause) {
        super(cause);
    }

}
