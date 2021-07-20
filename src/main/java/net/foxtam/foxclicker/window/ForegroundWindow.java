package net.foxtam.foxclicker.window;

import net.foxtam.foxclicker.Image;
import net.foxtam.foxclicker.Pair;
import net.foxtam.foxclicker.ScreenPoint;
import net.foxtam.foxclicker.exceptions.UnableToFindWindow;
import net.foxtam.foxclicker.screen.CheckImageStream;
import net.foxtam.foxclicker.screenstream.ForegroundWindowImageStream;
import net.foxtam.foxclicker.screenstream.ImageStream;
import net.foxtam.foxclicker.screenstream.LoggedImageStream;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static com.sun.jna.platform.win32.WinDef.HWND;

public class ForegroundWindow implements Window {
    private final WindowFrame wFrame;
    private final ImageStream imageStream;

    public ForegroundWindow(HWND hWnd,
                            double tolerance,
                            boolean inColor,
                            List<Pair<Image, Runnable>> checks) {
        if (hWnd == null) {
            throw new UnableToFindWindow("Unable to find the window!");
        }
        this.wFrame = new WindowFrame(hWnd);
        this.imageStream =
                new CheckImageStream(
                        new LoggedImageStream(new ForegroundWindowImageStream(hWnd)),
                        tolerance,
                        inColor,
                        checks);
    }

    @Override
    public ScreenPoint getWindowCenterPoint() {
        return wFrame.getWindowCenterPoint();
    }

    @Override
    public Optional<ScreenPoint> getPointOf(Image image, double tolerance, boolean inColor) {
        wFrame.activate();
        Rectangle rect = getRectangle();
        return Image.from(imageStream.getNextImage())
                .getPointOf(image, tolerance, inColor)
                .map(p -> ScreenPoint.of(p.getX() + rect.x, p.getY() + rect.y));
    }

    @Override
    public Rectangle getRectangle() {
        return wFrame.getRectangle();
    }

    @Override
    public List<ScreenPoint> getAllPointsOf(Image image, double tolerance, boolean inColor) {
        wFrame.activate();
        Rectangle rect = getRectangle();
        return Image.from(imageStream.getNextImage())
                .getAllPointsOf(image, tolerance, inColor)
                .stream()
                .map(p -> ScreenPoint.of(p.getX() + rect.x, p.getY() + rect.y))
                .toList();
    }
}
