package net.foxtam.foxclicker;

import lombok.Value;

@Value(staticConstructor = "of")
public class FreePoint {
    int x;
    int y;
}
