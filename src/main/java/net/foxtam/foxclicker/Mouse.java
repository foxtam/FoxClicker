package net.foxtam.foxclicker;

import java.awt.event.InputEvent;

public class Mouse {

    public static final Mouse INSTANCE = new Mouse();

    private Mouse() {
    }

    public void leftClickAt(ScreenPoint point) {
        moveTo(point);
        leftClickInPlace();
    }

    public void moveTo(ScreenPoint point) {
        Robo.INSTANCE.mouseMove(point.x(), point.y());
        Robo.INSTANCE.delay(100);
    }

    public void leftClickInPlace() {
        clickInPlace(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void clickInPlace(int button) {
        Robo.INSTANCE.mousePress(button);
        Robo.INSTANCE.delay(200);
        Robo.INSTANCE.mouseRelease(button);
    }

}
