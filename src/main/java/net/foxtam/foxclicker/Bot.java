package net.foxtam.foxclicker;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import net.foxtam.foxclicker.exceptions.FoxClickerException;
import net.foxtam.foxclicker.exceptions.ImageNotFoundException;
import net.foxtam.foxclicker.exceptions.InterruptBotException;
import net.foxtam.foxclicker.exceptions.WaitForImageException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public abstract class Bot {
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

    protected void sleep(double seconds) {
        lifeController.sleep((int) (seconds * 1000));
    }

    protected double getCurrentSeconds() {
        return System.nanoTime() / 1_000_000_000.0;
    }

    protected ScreenPoint getWindowCenterPoint() {
        return window.getWindowCenterPoint();
    }

    protected void mouseDragDirection(Direction direction, int lengthInPixel) {
        mouse.drag(direction, lengthInPixel);
    }

    protected void leftClickAt(ScreenPoint point) {
        mouse.leftClickAt(point);
    }

    protected void mouseMoveTo(ScreenPoint point) {
        mouse.moveTo(point);
    }

    public class Finder {
        private final double timeLimitInSeconds;
        private final double tolerance;
        private final boolean inColor;

        public Finder(double timeLimitInSeconds, double tolerance, boolean inColor) {
            this.timeLimitInSeconds = timeLimitInSeconds;
            this.tolerance = tolerance;
            this.inColor = inColor;
        }

        public Finder withTime(double timeLimitInSeconds) {
            return new Finder(timeLimitInSeconds, tolerance, inColor);
        }

        public Finder withTolerance(double tolerance) {
            return new Finder(timeLimitInSeconds, tolerance, inColor);
        }

        public Finder withInColor(boolean inColor) {
            return new Finder(timeLimitInSeconds, tolerance, inColor);
        }

        public boolean isImageVisible(Image image) {
            return isAnyImageVisible(image);
        }

        public boolean isAnyImageVisible(Image... images) {
            try {
                waitForAnyImage(images);
                return true;
            } catch (WaitForImageException ignore) {
                return false;
            }
        }

        public Image waitForAnyImage(Image... images) {
            if (images.length == 0) throw new IllegalArgumentException("Need at least one image");
            double startTime = getCurrentSeconds();
            while (true) {
                for (Image image : images) {
                    if (window.getPointOf(image, tolerance, inColor).isPresent()) return image;
                    lifeController.sleep(1);
                    if (getCurrentSeconds() > startTime + timeLimitInSeconds) {
                        throw new WaitForImageException("None of them " + Arrays.toString(images) + " did not appear for " + timeLimitInSeconds + " sec");
                    }
                }
            }
        }

        public void waitUntilImageHide(Image image) {
            throw new UnsupportedOperationException();
        }

        public void mouseMoveTo(Image image) {
            waitForImage(image);
            mouse.moveTo(getCenterPointOf(image));
        }

        public void waitForImage(Image image) {
            waitForAnyImage(image);
        }

        public ScreenPoint getCenterPointOf(Image image) {
            return window
                .getPointOf(image, tolerance, inColor)
                .map(p -> new ScreenPoint(p.x() + image.width() / 2, p.y() + image.height() / 2))
                .orElseThrow(() -> new ImageNotFoundException("" + image));
        }

        public void mouseDragTo(Image image) {
            waitForImage(image);
            mouse.dragTo(getCenterPointOf(image));
        }

        public List<ScreenPoint> getAllCenterPointsOf(Image image) {
            return window
                .getAllPointsOf(image, tolerance, inColor)
                .stream()
                .map(p -> new ScreenPoint(p.x() + image.width() / 2, p.y() + image.height() / 2))
                .sorted(Comparator.comparingInt(ScreenPoint::x))
                .toList();
        }

        public void leftClickAnyImage(Image... images) {
            leftClickOn(waitForAnyImage(images));
        }

        public void leftClickOn(Image image) {
            waitForImage(image);
            mouse.leftClickAt(getCenterPointOf(image));
        }
    }
}
