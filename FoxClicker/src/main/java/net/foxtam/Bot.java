package net.foxtam;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;


public abstract class Bot {
    private final GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true);
    private final KeyConfig keyConfig;
    private final BotLifeController lifeController = new BotLifeController();

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
        } catch (InterruptBotException e) {
            System.out.println("Interruption reason: " + e.getMessage());
        } finally {
            keyboardHook.shutdownHook();
        }
    }

}
