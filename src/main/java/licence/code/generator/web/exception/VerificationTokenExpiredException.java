package licence.code.generator.web.exception;

public class VerificationTokenExpiredException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366287163L;

    public VerificationTokenExpiredException() {
        super();
    }

    public VerificationTokenExpiredException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public VerificationTokenExpiredException(final String message) {
        super(message);
    }

    public VerificationTokenExpiredException(final Throwable cause) {
        super(cause);
    }

}