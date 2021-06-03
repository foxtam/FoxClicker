package net.foxtam;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import net.foxtam.exceptions.AWTRuntimeException;
import net.foxtam.exceptions.FoxClickerException;
import net.foxtam.exceptions.InterruptBotException;

import java.awt.*;


public abstract class Bot {
    private final GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true);
    private final KeyConfig keyConfig;
    private final Robot robot;

    {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new AWTRuntimeException(e);
        }
    }

    private final BotLifeController lifeController = new BotLifeController(robot);

    public Bot(final KeyConfig keyConfig) {
        this.keyConfig = keyConfig;
        keyboardHook.addKeyListener(
            new GlobalKeyAdapter() {
                @Override
                public void keyPressed(GlobalKeyEvent event) {
                    if (event.getVirtualKeyCode() == keyConfig.getStopKey()) {
                        if (event.isControlPressed() == keyConfig.isCtrlPressed()
                            && event.isShiftPressed() == keyConfig.isShiftPressed()) {
                            lifeController.interrupt();
                        }
                    } else if (event.getVirtualKeyCode() == keyConfig.getPauseKey()) {
                        if (lifeController.isGlobalPause()) {
                            lifeController.cancelGlobalPause();
                        } else {
                            lifeController.setGlobalPause();
                        }
                    }
                }
            });
    }

    protected abstract void action();

    public void run() {
        try {
            action();
        } catch (FoxClickerException e) {
            System.out.println("Interruption reason: " + e.getMessage());
            if (!(e instanceof InterruptBotException)) {
                e.printStackTrace();
            }
        } finally {
            keyboardHook.shutdownHook();
        }
    }

}
