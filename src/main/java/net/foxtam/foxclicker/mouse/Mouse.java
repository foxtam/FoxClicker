package net.foxtam.foxclicker.mouse;

import net.foxtam.foxclicker.Direction;
import net.foxtam.foxclicker.ScreenPoint;

public interface Mouse {
    void drag(Direction direction, int lengthInPixel);
    void dragTo(ScreenPoint point);
    void moveTo(ScreenPoint point);
    void leftClickAt(ScreenPoint point);
}
