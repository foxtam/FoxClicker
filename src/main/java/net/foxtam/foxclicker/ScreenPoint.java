package net.foxtam.foxclicker;

import lombok.Value;

@Value(staticConstructor = "of")
public class ScreenPoint {
    int x;
    int y;

    public ScreenPoint shift(int dx, int dy) {
        return ScreenPoint.of(x + dx, y + dy);
    }
}
