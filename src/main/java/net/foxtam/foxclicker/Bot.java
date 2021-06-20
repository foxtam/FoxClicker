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

import static net.foxtam.foxclicker.GlobalLogger.*;

public abstract class Bot {
    private final Window window;
    private final Mouse mouse;
    private final GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true);
    private final BotLifeController lifeController = new BotLifeController();

    protected Bot(KeyConfig keyConfig, Window window, Runnable onStop, Runnable onPause) {
        enter(keyConfig, window);
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
                              onStop.run();
                          }
                      } else if (event.getVirtualKeyCode() == keyConfig.getPauseKey()) {
                          if (lifeController.isGlobalPause()) {
                              lifeController.cancelGlobalPause();
                          } else {
                              lifeController.setGlobalPause();
                          }
                          onPause.run();
                      }
                  }
              });
        exit();
    }

    public void run() {
        enter();
        try {
            window.activate();
            action();
        } catch (FoxClickerException e) {
            trace("Interruption reason: " + e.getMessage());
            System.out.println("Interruption reason: " + e.getMessage());
            if (!(e instanceof InterruptBotException)) throw exception(e);
        } catch (RuntimeException e) {
            throw exception(e);
        } finally {
            keyboardHook.shutdownHook();
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

    protected ScreenPoint getWindowCenterPoint() {
        enter();
        return exit(window.getWindowCenterPoint());
    }

    protected void mouseDragDirection(Direction direction, int lengthInPixel) {
        enter(direction, lengthInPixel);
        window.activate();
        mouse.drag(direction, lengthInPixel);
        exit();
    }

    protected void leftClickAt(ScreenPoint point) {
        enter(point);
        window.activate();
        mouse.leftClickAt(point);
        exit();
    }

    protected void mouseMoveTo(ScreenPoint point) {
        enter(point);
        window.activate();
        mouse.moveTo(point);
        exit();
    }

    public class Finder {
        private final double timeLimitInSeconds;
        private final double tolerance;
        private final boolean inColor;

        public Finder(double timeLimitInSeconds, double tolerance, boolean inColor) {
            enter(timeLimitInSeconds, tolerance, inColor);
            this.timeLimitInSeconds = timeLimitInSeconds;
            this.tolerance = tolerance;
            this.inColor = inColor;
            exit();
        }

        public Finder withTime(double timeLimitInSeconds) {
            enter(timeLimitInSeconds);
            return exit(new Finder(timeLimitInSeconds, tolerance, inColor));
        }

        public Finder withTolerance(double tolerance) {
            return new Finder(timeLimitInSeconds, tolerance, inColor);
        }

        public Finder withColor(boolean inColor) {
            enter(inColor);
            return exit(new Finder(timeLimitInSeconds, tolerance, inColor));
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
                    if (getCurrentSeconds() > startTime + timeLimitInSeconds) {
                        throw exception(
                              new WaitForImageException(
                                    "None of this "
                                          + Arrays.toString(images)
                                          + " appeared in "
                                          + timeLimitInSeconds
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
                      new WaitForImageException(image + " didn't appear for " + timeLimitInSeconds + " seconds"));
            }
            exit();
        }

        public ScreenPoint getCenterPointOf(Image image) {
            enter(image);
            return exit(
                  window
                        .getPointOf(image, tolerance, inColor)
                        .map(p -> new ScreenPoint(p.x() + image.width() / 2, p.y() + image.height() / 2))
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
                        .map(p -> new ScreenPoint(p.x() + image.width() / 2, p.y() + image.height() / 2))
                        .sorted(Comparator.comparingInt(ScreenPoint::x))
                        .toList());
        }

        public void leftClickAnyImage(Image... images) {
            enter((Object[]) images);
            leftClickOn(waitForAnyImage(images));
            exit();
        }

        public void leftClickOn(Image image) {
            enter(image);
            window.activate();
            waitForImage(image);
            mouse.leftClickAt(getCenterPointOf(image));
            exit();
        }
    }
}
