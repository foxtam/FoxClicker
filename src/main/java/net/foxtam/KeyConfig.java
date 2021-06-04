package net.foxtam;

import lc.kra.system.keyboard.event.GlobalKeyEvent;

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

    public static KeyConfig getDefault() {
        return new KeyConfig(GlobalKeyEvent.VK_F8, GlobalKeyEvent.VK_F4, false, false);
    }
}
