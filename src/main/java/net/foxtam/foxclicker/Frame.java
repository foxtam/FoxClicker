package net.foxtam.foxclicker;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.With;
import net.foxtam.foxclicker.context.Context;
import net.foxtam.foxclicker.exceptions.ImageNotFoundException;
import net.foxtam.foxclicker.exceptions.WaitForImageException;
import net.foxtam.foxclicker.mouse.Mouse;
import net.foxtam.foxclicker.window.Window;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static net.foxtam.foxclicker.GlobalLogger.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Frame {
    private final LifeController lifeController;
    private final Window window;
    private final Mouse mouse;

    @With
    private final double timeLimit;
    @With
    private final double tolerance;
    @With
    private final boolean inColor;

    Frame(LifeController lifeController,
          Context context,
          double timeLimit,
          double tolerance,
          boolean inColor) {
        this(lifeController, context.getWindow(tolerance, inColor), context.getMouse(lifeController), timeLimit, tolerance, inColor);
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
        double startTime = Time.getCurrentSeconds();
        while (true) {
            for (Image image : images) {
                lifeController.checkPauseOrInterrupt();
                if (window.getPointOf(image, tolerance, inColor).isPresent()) return exit(image);
                lifeController.checkPauseOrInterrupt();
                if (Time.getCurrentSeconds() > startTime + timeLimit) {
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
        mouse.drag(direction, lengthInPixel);
        exit();
    }

    public void leftClickAt(ScreenPoint point) {
        enter(point);
        mouse.leftClickAt(point);
        exit();
    }

    public void mouseMoveTo(ScreenPoint point) {
        enter(point);
        mouse.moveTo(point);
        exit();
    }

    public Rectangle getWidowRectangle() {
        return window.getRectangle();
    }
}