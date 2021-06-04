package net.foxtam.foxclicker.exceptions;

public class FoxClickerException extends RuntimeException {
    public FoxClickerException() {
    }

    public FoxClickerException(String message) {
        super(message);
    }

    public FoxClickerException(String message, Throwable cause) {
        super(message, cause);
    }

    public FoxClickerException(Throwable cause) {
        super(cause);
    }
}
