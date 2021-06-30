package net.foxtam.foxclicker;

import lombok.Value;

@Value(staticConstructor = "of")
public class LocalPoint {
    int x;
    int y;
}
