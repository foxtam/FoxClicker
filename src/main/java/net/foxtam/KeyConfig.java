package net.foxtam;

public class KeyConfig {
    private final int stopKey;
    private final int pauseKey;
    private final boolean ctrlPressed;
    private final boolean shiftPressed;

    public int getStopKey() {
        return stopKey;
    }

    public int getPauseKey() {
        return pauseKey;
    }

    public boolean isCtrlPressed() {
        return ctrlPressed;
    }

    public boolean isShiftPressed() {
        return shiftPressed;
    }

    public KeyConfig(int stopKey, int pauseKey, boolean ctrlPressed, boolean shiftPressed) {
        this.stopKey = stopKey;
        this.pauseKey = pauseKey;
        this.ctrlPressed = ctrlPressed;
        this.shiftPressed = shiftPressed;
    }
}
