package net.foxtam;

import java.awt.*;

public class AWTRuntimeException extends FoxClickerException {
    public AWTRuntimeException(AWTException e) {
        super(e);
    }
}
