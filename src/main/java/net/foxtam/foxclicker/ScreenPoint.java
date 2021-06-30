package net.foxtam.foxclicker;

import lombok.Value;

@Value(staticConstructor = "of")
public class ScreenPoint {
    int x;
    int y;
}
