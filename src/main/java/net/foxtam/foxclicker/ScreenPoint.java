package net.foxtam.foxclicker;

public record ScreenPoint(int x, int y) {
    public ScreenPoint shift(int dx, int dy) {
        return new ScreenPoint(x + dx, y + dy);
    }
}
