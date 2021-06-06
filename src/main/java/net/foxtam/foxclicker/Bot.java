package net.foxtam.foxclicker;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

import java.util.Arrays;
import java.util.Optional;

import net.foxtam.foxclicker.exceptions.FoxClickerException;
import net.foxtam.foxclicker.exceptions.ImageNotFoundException;
import net.foxtam.foxclicker.exceptions.InterruptBotException;
import net.foxtam.foxclicker.exceptions.WaitForImageException;

public abstract class Bot {
    private final GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true);
    private final BotLifeController lifeController = new BotLifeController();
    private final Window window;

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
            if (!(e instanceof InterruptBotException)) e.printStackTrace();
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

    
    
    protected void waitForImage(Image image, double seconds) {
        long startTime = getCurrentMillis();
        while (!isImageView(image)) {
            lifeController.sleep(1);
            if (getCurrentMillis() > startTime + seconds * 1000L) {
                throw new WaitForImageException("The " + image + " did not appear for " + seconds + " sec");
            }
        }
    }

    private long getCurrentMillis() {
        return System.nanoTime() / 1_000_000;
    }

    protected boolean isImageView(Image image) {
        return window.getLeftTopPointOf(image).isPresent();
    }

    protected Image waitForAnyImage(double seconds, Image... images) {
        if (images.length == 0) throw new IllegalArgumentException("Need at least one image");
        long startTime = getCurrentMillis();
        while (true) {
            for (Image image : images) {
                if (isImageView(image)) return image;
                lifeController.sleep(1);
                if (getCurrentMillis() > startTime + seconds * 1000L) {
                    throw new WaitForImageException("None of them " + Arrays.toString(images) + " did not appear for " + seconds + " sec");
                }
            }
        }
    }

    protected void mouseDrag(int lengthInPixel, Direction direction) {
        Mouse.INSTANCE.drag(lengthInPixel, direction);
    }

}
