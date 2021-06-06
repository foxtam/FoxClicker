package net.foxtam.foxclicker;

import java.awt.*;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.util.Objects;

import static java.lang.Math.*;

public class Mouse {

    public static final Mouse INSTANCE = new Mouse();
    private static final int mouseSpeed = 2000;
    private static final int mouseDelay = 60;

    private Mouse() {
    }

    public void leftClickAt(ScreenPoint point) {
        moveTo(point);
        leftClickInPlace();
    }

    public void moveTo(ScreenPoint point) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int targetX = clip(point.x(), 0, screenSize.width - 1);
        int targetY = clip(point.y(), 0, screenSize.height - 1);

        ScreenPoint currentPoint = getCurrentMouseLocation();
        int currentX = currentPoint.x();
        int currentY = currentPoint.y();
        double diffX = targetX - currentX;
        double diffY = targetY - currentY;

        int n = ((int) max(abs(diffX), abs(diffY))) / 10;
        double dx = diffX / n;
        double dy = diffY / n;
        double path = sqrt(pow(diffX, 2) + pow(diffY, 2));
        double dt = path / mouseSpeed / n * 1000;

        for (int step = 1; step <= n; step++) {
            Robo.INSTANCE.delay((int) dt);
            int newX = (int) (currentX + dx * step);
            int newY = (int) (currentY + dy * step);
            ScreenPoint real;
            do {
                Robo.INSTANCE.mouseMove(newX, newY);
                real = getCurrentMouseLocation();
            } while (real.x() != newX || real.y() != newY);
        }

        Robo.INSTANCE.mouseMove(targetX, targetY);
        Robo.INSTANCE.delay(100);
    }

    public void leftClickInPlace() {
        clickInPlace(InputEvent.BUTTON1_DOWN_MASK);
    }

    private int clip(int value, int from, int to) {
        if (from > to) throw new IllegalArgumentException(from + " > " + to + "!");
        if (value < from) return from;
        return min(value, to);
    }

    private ScreenPoint getCurrentMouseLocation() {
        Point location = MouseInfo.getPointerInfo().getLocation();
        return new ScreenPoint(location.x, location.y);
    }

    private void clickInPlace(int button) {
        Robo.INSTANCE.mousePress(button);
        Robo.INSTANCE.delay(200);
        Robo.INSTANCE.mouseRelease(button);
    }

    public void drag(int lengthInPixel, Direction direction) {
        ScreenPoint point = getCurrentMouseLocation();
        switch (direction) {
            case UP -> dragTo(new ScreenPoint(point.x(), point.y() - lengthInPixel));
            case DOWN -> dragTo(new ScreenPoint(point.x(), point.y() + lengthInPixel));
            case RIGHT -> dragTo(new ScreenPoint(point.x() + lengthInPixel, point.y()));
            case LEFT -> dragTo(new ScreenPoint(point.x() - lengthInPixel, point.y()));
        }
    }

    public void dragTo(ScreenPoint point) {
        Robo.INSTANCE.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Robo.INSTANCE.delay(mouseDelay);
        moveTo(point);
        Robo.INSTANCE.delay(mouseDelay);
        Robo.INSTANCE.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Robo.INSTANCE.delay(mouseDelay);
    }
}
