package net.foxtam.foxclicker;

import lombok.Value;

@Value(staticConstructor = "of")
public class FreePoint {
    int x;
    int y;

    public FreePoint shift(int dx, int dy) {
        return FreePoint.of(x + dx, y + dy);
    }
}
