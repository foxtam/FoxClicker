package net.foxtam.exceptions;

public class InterruptBotException extends RuntimeException {
    public InterruptBotException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterruptBotException(String message) {
        super(message);
    }
}
