package net.foxtam.foxclicker.window;

import com.sun.jna.platform.win32.*;
import net.foxtam.foxclicker.*;
import net.foxtam.foxclicker.Image;
import net.foxtam.foxclicker.exceptions.UnableToFindWindow;
import net.foxtam.foxclicker.screen.CheckScreen;
import net.foxtam.foxclicker.screen.Screen;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

public class BackgroundWindow implements Window {
    private final WindowFrame wFrame;
    private final Screen screen;

    public BackgroundWindow(WinDef.HWND hWnd, double tolerance, boolean inColor, List<Pair<Image, Runnable>> checks) {
        if (hWnd == null) {
            throw new UnableToFindWindow("Unable to find the window!");
        }
        this.wFrame = new WindowFrame(hWnd);
        this.screen = CheckScreen.of(tolerance, inColor, checks);
    }

    @Override
    public ScreenPoint getWindowCenterPoint() {
        return wFrame.getWindowCenterPoint();
    }

    @Override
    public Optional<ScreenPoint> getPointOf(Image image, double tolerance, boolean inColor) {
        BufferedImage windowCapture = Capture.getWindowCapture(wFrame.getHWND());
        Rectangle rect = getRectangle();
        return Image.from(windowCapture)
                .getPointOf(image, tolerance, inColor)
                .map(p -> ScreenPoint.of(p.getX() + rect.x, p.getY() + rect.y));
    }

    @Override
    public Rectangle getRectangle() {
        return wFrame.getRectangle();
    }

    @Override
    public List<ScreenPoint> getAllPointsOf(Image image, double tolerance, boolean inColor) {
        return null;
    }
}
