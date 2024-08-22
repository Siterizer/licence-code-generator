package licence.code.generator.web.exception;

public class PasswordChangeTokenExpiredException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366287163L;

    public PasswordChangeTokenExpiredException() {
        super();
    }

    public PasswordChangeTokenExpiredException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PasswordChangeTokenExpiredException(final String message) {
        super(message);
    }

    public PasswordChangeTokenExpiredException(final Throwable cause) {
        super(cause);
    }

}