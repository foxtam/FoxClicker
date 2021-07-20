package net.foxtam.foxclicker.mouse;

import com.sun.jna.platform.win32.WinDef;
import net.foxtam.foxclicker.Direction;
import net.foxtam.foxclicker.LifeController;
import net.foxtam.foxclicker.Robo;
import net.foxtam.foxclicker.ScreenPoint;
import net.foxtam.foxclicker.window.WindowFrame;

import java.awt.*;
import java.awt.event.InputEvent;

import static java.lang.Math.*;

public class ForegroundMouse implements Mouse {

    private static final int mouseSpeed = 2000;
    private static final int mouseDelay = 100;

    private final LifeController lifeController;
    private final WindowFrame wFrame;

    public ForegroundMouse(LifeController lifeController, WinDef.HWND hWnd) {
        this.lifeController = lifeController;
        this.wFrame = new WindowFrame(hWnd);
    }

    @Override
    public void drag(Direction direction, int lengthInPixel) {
        wFrame.activate();
        ScreenPoint point = getCurrentMouseLocation();
        switch (direction) {
            case UP -> dragTo(point.shift(0, -lengthInPixel));
            case DOWN -> dragTo(point.shift(0, lengthInPixel));
            case RIGHT -> dragTo(point.shift(lengthInPixel, 0));
            case LEFT -> dragTo(point.shift(-lengthInPixel, 0));
        }
    }

    private ScreenPoint getCurrentMouseLocation() {
        Point location = MouseInfo.getPointerInfo().getLocation();
        return ScreenPoint.of(location.x, location.y);
    }

    @Override
    public void dragTo(ScreenPoint point) {
        wFrame.activate();
        Robo.getInstance().mousePress(InputEvent.BUTTON1_DOWN_MASK);
        lifeController.sleep(mouseDelay);
        moveTo(point);
        lifeController.sleep(mouseDelay);
        Robo.getInstance().mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        lifeController.sleep(mouseDelay);
    }

    @Override
    public void moveTo(ScreenPoint point) {
        wFrame.activate();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int targetX = clip(point.getX(), 0, screenSize.width - 1);
        int targetY = clip(point.getY(), 0, screenSize.height - 1);

        ScreenPoint currentPoint = getCurrentMouseLocation();
        int currentX = currentPoint.getX();
        int currentY = currentPoint.getY();
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
                } while (real.getX() != newX || real.getY() != newY);
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

    @Override
    public void leftClickAt(ScreenPoint point) {
        moveTo(point);
        leftClickInPlace();
    }

    public void leftClickInPlace() {
        clickInPlace(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void clickInPlace(int button) {
        wFrame.activate();
        Robo.getInstance().mousePress(button);
        lifeController.sleep(mouseDelay);
        Robo.getInstance().mouseRelease(button);
    }
}
