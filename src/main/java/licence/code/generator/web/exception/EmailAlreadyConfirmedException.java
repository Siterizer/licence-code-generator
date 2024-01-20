package licence.code.generator.web.exception;

public final class EmailAlreadyConfirmedException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366287163L;

    public EmailAlreadyConfirmedException() {
        super();
    }

    public EmailAlreadyConfirmedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EmailAlreadyConfirmedException(final String message) {
        super(message);
    }

    public EmailAlreadyConfirmedException(final Throwable cause) {
        super(cause);
    }

}
