package net.foxtam;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

import java.awt.*;

public abstract class Bot {
    private final GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true);
    private final Robot robot;
    private final KeyConfig keyConfig;

    private boolean isInterruptedWithKey = false;
    private boolean isGlobalPause = false;

    {
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            throw new AWTRuntimeException(e);
        }
    }

    public Bot(final KeyConfig keyConfig) {
        this.keyConfig = keyConfig;
        keyboardHook.addKeyListener(
                new GlobalKeyAdapter() {
                    @Override
                    public void keyPressed(GlobalKeyEvent event) {
                        if (event.getVirtualKeyCode() == keyConfig.getStopKey()) {
                            if (event.isControlPressed() == keyConfig.isCtrlPressed()
                                    && event.isShiftPressed() == keyConfig.isShiftPressed()) {
                                isInterruptedWithKey = true;
                            }
                        } else if (event.getVirtualKeyCode() == keyConfig.getPauseKey()) {
                            isGlobalPause = !isGlobalPause;
                        }
                    }
                });
    }
}
