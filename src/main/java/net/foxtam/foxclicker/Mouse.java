package net.foxtam.foxclicker;

import java.awt.*;
import java.awt.event.InputEvent;

import static java.lang.Math.*;

public class Mouse {

    private static final int mouseSpeed = 2000;
    private static final int mouseDelay = 60;

    private final BotLifeController lifeController;

    public Mouse(BotLifeController lifeController) {
        this.lifeController = lifeController;
    }

    public void drag(Direction direction, int lengthInPixel) {
        ScreenPoint point = getCurrentMouseLocation();
        switch (direction) {
            case UP -> dragTo(new ScreenPoint(point.x(), point.y() - lengthInPixel));
            case DOWN -> dragTo(new ScreenPoint(point.x(), point.y() + lengthInPixel));
            case RIGHT -> dragTo(new ScreenPoint(point.x() + lengthInPixel, point.y()));
            case LEFT -> dragTo(new ScreenPoint(point.x() - lengthInPixel, point.y()));
        }
    }

    private ScreenPoint getCurrentMouseLocation() {
        Point location = MouseInfo.getPointerInfo().getLocation();
        return new ScreenPoint(location.x, location.y);
    }

    public void dragTo(ScreenPoint point) {
        Robo.getInstance().mousePress(InputEvent.BUTTON1_DOWN_MASK);
        lifeController.sleep(mouseDelay);
        moveTo(point);
        lifeController.sleep(mouseDelay);
        Robo.getInstance().mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        lifeController.sleep(mouseDelay);
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
        
        if (diffX != 0 || diffY != 0) {
            int n = ((int) max(abs(diffX), abs(diffY))) / 10;
            double dx = diffX / n;
            double dy = diffY / n;
            double path = sqrt(pow(diffX, 2) + pow(diffY, 2));
            double dt = path / mouseSpeed / n * 1000;

            for (int step = 1; step <= n; step++) {
                lifeController.sleep((int) dt);
                int newX = (int) (currentX + dx * step);
                int newY = (int) (currentY + dy * step);
                ScreenPoint real;
                do {
                    Robo.getInstance().mouseMove(newX, newY);
                    real = getCurrentMouseLocation();
                } while (real.x() != newX || real.y() != newY);
            }

            Robo.getInstance().mouseMove(targetX, targetY);
            lifeController.sleep(100);
        }
    }

    private int clip(int value, int from, int to) {
        if (from > to) throw new IllegalArgumentException(from + " > " + to + "!");
        if (value < from) return from;
        return min(value, to);
    }

    public void leftClickAt(ScreenPoint point) {
        moveTo(point);
        leftClickInPlace();
    }

    public void leftClickInPlace() {
        clickInPlace(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void clickInPlace(int button) {
        Robo.getInstance().mousePress(button);
        lifeController.sleep(100);
        Robo.getInstance().mouseRelease(button);
    }
}
