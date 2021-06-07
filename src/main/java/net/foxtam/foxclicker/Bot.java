package net.foxtam.foxclicker;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import net.foxtam.foxclicker.exceptions.FoxClickerException;
import net.foxtam.foxclicker.exceptions.ImageNotFoundException;
import net.foxtam.foxclicker.exceptions.InterruptBotException;
import net.foxtam.foxclicker.exceptions.WaitForImageException;

import java.util.Arrays;
import java.util.Optional;

public abstract class Bot {
    private static final double imageWaitingTime = 2;
    private final Window window;
    private final Mouse mouse;
    private final GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true);
    private final BotLifeController lifeController = new BotLifeController();

    protected Bot(KeyConfig keyConfig, Window window) {
        this.window = window;
        this.mouse = new Mouse(lifeController);

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

    protected Image waitForAnyImage(double seconds, Image... images) {
        if (images.length == 0) throw new IllegalArgumentException("Need at least one image");
        double startTime = getCurrentSeconds();
        while (true) {
            for (Image image : images) {
                if (window.isImageVisibleNow(image)) return image;
                lifeController.sleep(1);
                if (getCurrentSeconds() > startTime + seconds) {
                    throw new WaitForImageException("None of them " + Arrays.toString(images) + " did not appear for " + seconds + " sec");
                }
            }
        }
    }

    private double getCurrentSeconds() {
        return System.nanoTime() / 1_000_000_000.0;
    }

    protected void sleep(double seconds) {
        lifeController.sleep((int) (seconds * 1000));
    }

    protected void leftClickOn(Image image) {
        if (!isImageVisible(image)) throw new ImageNotFoundException("Image not found: " + image);
        //noinspection OptionalGetWithoutIsPresent
        mouse.leftClickAt(window.getCenterPointOf(image).get());
    }

    protected boolean isImageVisible(Image image) {
        return isImageVisible(image, imageWaitingTime);
    }

    protected boolean isImageVisible(Image image, double seconds) {
        double startTime = getCurrentSeconds();
        while (!window.isImageVisibleNow(image)) {
            lifeController.sleep(1);
            if (getCurrentSeconds() > startTime + seconds) {
                return false;
            }
        }
        return true;
    }

    protected void waitForImage(Image image, double seconds) {
        if (!isImageVisible(image, seconds)) {
            throw new WaitForImageException("The " + image + " did not appear for " + seconds + " sec");
        }
    }

    public void mouseMoveTo(Image image) {
        if (!isImageVisible(image)) throw new ImageNotFoundException("Image not found: " + image);
        //noinspection OptionalGetWithoutIsPresent
        mouse.moveTo(window.getCenterPointOf(image).get());
    }

    public void mouseDragTo(Image image) {
        if (!isImageVisible(image)) throw new ImageNotFoundException("Image not found: " + image);
        //noinspection OptionalGetWithoutIsPresent
        mouse.dragTo(window.getCenterPointOf(image).get());
    }

    protected void mouseDragDirection(Direction direction, int lengthInPixel) {
        mouse.drag(direction, lengthInPixel);
    }

    protected void leftClickAt(ScreenPoint point) {
        mouse.leftClickAt(point);
    }

    protected ScreenPoint getWindowCenterPoint() {
        return window.getWindowCenterPoint();
    }
}
