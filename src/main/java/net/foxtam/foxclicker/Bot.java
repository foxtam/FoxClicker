package net.foxtam.foxclicker;

import com.sun.jna.platform.win32.WinDef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.With;
import net.foxtam.foxclicker.exceptions.FoxClickerException;
import net.foxtam.foxclicker.exceptions.ImageNotFoundException;
import net.foxtam.foxclicker.exceptions.InterruptBotException;
import net.foxtam.foxclicker.exceptions.WaitForImageException;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static net.foxtam.foxclicker.GlobalLogger.*;

public abstract class Bot {
    private final Mouse mouse;
    private final BotLifeController lifeController;

    protected Bot(KeyConfig keyConfig, Runnable onStop, Runnable onPause) {
        enter(keyConfig);
        this.lifeController = new BotLifeController(keyConfig, onStop, onPause);
        this.mouse = new Mouse(lifeController);
        exit();
    }

    public void run() {
        enter();
        try {
            action();
        } catch (FoxClickerException e) {
            trace("Interruption reason: " + e.getMessage());
            System.out.println("Interruption reason: " + e.getMessage());
            if (!(e instanceof InterruptBotException)) throw exception(e);
        } catch (RuntimeException e) {
            throw exception(e);
        } finally {
            lifeController.close();
        }
        exit();
    }

    protected abstract void action();

    protected void sleep(double seconds) {
        enter(seconds);
        lifeController.sleep((int) (seconds * 1000));
        exit();
    }

    protected double getCurrentSeconds() {
        return System.nanoTime() / 1_000_000_000.0;
    }

    protected Frame createFrame(WinDef.HWND hWnd,
                                double timeLimitInSeconds,
                                double tolerance,
                                boolean inColor,
                                List<Pair<Image, Runnable>> checks) {
        Objects.requireNonNull(hWnd);
        return new Frame(hWnd, timeLimitInSeconds, tolerance, inColor, checks);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public class Frame {
        private final Window window;
        @With
        private final double timeLimit;
        @With
        private final double tolerance;
        @With
        private final boolean inColor;

        private Frame(WinDef.HWND hWnd,
                      double timeLimit,
                      double tolerance,
                      boolean inColor,
                      List<Pair<Image, Runnable>> checks) {
            this(new Window(hWnd, tolerance, inColor, checks), timeLimit, tolerance, inColor);
            enter(timeLimit, tolerance, inColor);
            exit();
        }

        public boolean isImageVisible(Image image) {
            enter(image);
            return exit(isAnyImageVisible(image));
        }

        public boolean isAnyImageVisible(Image... images) {
            enter((Object[]) images);
            try {
                waitForAnyImage(images);
                return exit(true);
            } catch (WaitForImageException ignore) {
                return exit(false);
            }
        }

        public Image waitForAnyImage(Image... images) {
            enter((Object[]) images);
            if (images.length == 0) throw exception(new IllegalArgumentException("Need at least one image"));
            double startTime = getCurrentSeconds();
            while (true) {
                for (Image image : images) {
                    lifeController.checkPauseOrInterrupt();
                    if (window.getPointOf(image, tolerance, inColor).isPresent()) return exit(image);
                    lifeController.checkPauseOrInterrupt();
                    if (getCurrentSeconds() > startTime + timeLimit) {
                        throw exception(
                                new WaitForImageException(
                                        "None of this "
                                                + Arrays.toString(images)
                                                + " appeared in "
                                                + timeLimit
                                                + " seconds"));
                    }
                }
            }
        }

        public void waitUntilImageHide(Image image) {
            throw new UnsupportedOperationException();
        }

        public void mouseMoveTo(Image image) {
            enter(image);
            waitForImage(image);
            window.activate();
            mouse.moveTo(getCenterPointOf(image));
            exit();
        }

        public void waitForImage(Image image) {
            enter(image);
            try {
                waitForAnyImage(image);
            } catch (WaitForImageException e) {
                throw exception(
                        new WaitForImageException(image + " didn't appear for " + timeLimit + " seconds"));
            }
            exit();
        }

        public ScreenPoint getCenterPointOf(Image image) {
            enter(image);
            return exit(
                    window.getPointOf(image, tolerance, inColor)
                            .map(p -> ScreenPoint.of(p.getX() + image.width() / 2, p.getY() + image.height() / 2))
                            .orElseThrow(() -> exception(new ImageNotFoundException("" + image))));
        }

        public void mouseDragTo(Image image) {
            enter(image);
            waitForImage(image);
            window.activate();
            mouse.dragTo(getCenterPointOf(image));
            exit();
        }

        public List<ScreenPoint> getAllCenterPointsOf(Image image) {
            enter(image);
            return exit(
                    window
                            .getAllPointsOf(image, tolerance, inColor)
                            .stream()
                            .map(p -> ScreenPoint.of(p.getX() + image.width() / 2, p.getY() + image.height() / 2))
                            .sorted(Comparator.comparingInt(ScreenPoint::getX))
                            .toList());
        }

        public void leftClickAnyImage(Image... images) {
            enter((Object[]) images);
            leftClickOn(waitForAnyImage(images));
            exit();
        }

        public void leftClickOn(Image image) {
            enter(image);
            waitForImage(image);
            mouse.leftClickAt(getCenterPointOf(image));
            exit();
        }

        public ScreenPoint getCenterPoint() {
            enter();
            return exit(window.getWindowCenterPoint());
        }

        public void mouseDragDirection(Direction direction, int lengthInPixel) {
            enter(direction, lengthInPixel);
            window.activate();
            mouse.drag(direction, lengthInPixel);
            exit();
        }

        public void leftClickAt(ScreenPoint point) {
            enter(point);
            window.activate();
            mouse.leftClickAt(point);
            exit();
        }

        public void mouseMoveTo(ScreenPoint point) {
            enter(point);
            window.activate();
            mouse.moveTo(point);
            exit();
        }

        public Rectangle getWidowRectangle() {
            return window.getRectangle();
        }
    }
}
