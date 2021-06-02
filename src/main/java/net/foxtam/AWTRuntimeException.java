package net.foxtam;

import java.awt.*;

public class AWTRuntimeException extends RuntimeException {
    public AWTRuntimeException(AWTException e) {
        super(e);
    }
}
