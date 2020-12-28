package it.mulders.columbia.shared;

public class TechnicalException extends Exception {
    public TechnicalException(final String message) {
        super(message);
    }

    public TechnicalException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
