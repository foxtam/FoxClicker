package net.foxtam.foxclicker;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

import java.util.Optional;

import net.foxtam.foxclicker.exceptions.FoxClickerException;
import net.foxtam.foxclicker.exceptions.ImageNotFoundException;
import net.foxtam.foxclicker.exceptions.InterruptBotException;

public abstract class Bot {
    private final GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true);

    private final Window window;
    private final BotLifeController lifeController = new BotLifeController();

    protected Bot(String windowTitle, KeyConfig keyConfig) {
        this.window = Window.getByTitle(windowTitle, lifeController);
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

    public void run() {
        try {
            this.window.activate();
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

    protected abstract void action();

    protected void leftClickOn(Image image) {
        Optional<ScreenPoint> point = window.getCenterPointOf(image);
        if (point.isPresent()) {
            Mouse.INSTANCE.leftClickAt(point.get());
        } else {
            throw new ImageNotFoundException("Image not found: " + image);
        }
    }

    protected void waitForImage(Image image) {
        while (!isImageView(image)) {
            lifeController.sleep(1);
        }
    }

    protected boolean isImageView(Image image) {
        return window.getLeftTopPointOf(image).isPresent();
    }

}
