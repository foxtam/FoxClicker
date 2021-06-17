package net.foxtam.foxclicker.exceptions;

public class WrongGlobalLoggerStack extends RuntimeException {
    public WrongGlobalLoggerStack(String msg) {
        super(msg);
    }
}