package net.foxtam.foxclicker.window;

import net.foxtam.foxclicker.Image;
import net.foxtam.foxclicker.ScreenPoint;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public interface Window {
    ScreenPoint getWindowCenterPoint();

    Rectangle getRectangle();

    Optional<ScreenPoint> getPointOf(Image image, double tolerance, boolean inColor);

    List<ScreenPoint> getAllPointsOf(Image image, double tolerance, boolean inColor);
}
