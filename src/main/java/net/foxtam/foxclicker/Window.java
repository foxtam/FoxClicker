package net.foxtam.foxclicker;

import java.awt.*;
import java.util.Optional;

import static com.sun.jna.platform.win32.WinDef.*;
import static com.sun.jna.platform.win32.WinUser.*;

public class Window {
    private final HWND hWnd;

    private Window(HWND hWnd) {
        this.hWnd = hWnd;
    }

    public static Window getByClass(String windowClassName) {
        return new Window(User32.INSTANCE.FindWindow(windowClassName, null));
    }

    public static Window getByTitle(String windowTitle) {
        return new Window(User32.INSTANCE.FindWindow(null, windowTitle));
    }

    public void activate() {
        var winPlace = new WINDOWPLACEMENT();
        User32.INSTANCE.GetWindowPlacement(hWnd, winPlace);
        switch (winPlace.showCmd) {
            case SW_SHOWMAXIMIZED -> User32.INSTANCE.ShowWindow(hWnd, SW_SHOWMAXIMIZED);
            case SW_SHOWMINIMIZED -> User32.INSTANCE.ShowWindow(hWnd, SW_RESTORE);
            default -> User32.INSTANCE.ShowWindow(hWnd, SW_NORMAL);
        }
        do {
            User32.INSTANCE.SetForegroundWindow(hWnd);
            Robo.INSTANCE.delay(5);
        } while (!User32.INSTANCE.GetForegroundWindow().equals(hWnd));
    }

    public Optional<ScreenPoint> getLeftTopPointOf(Image image) {
        Rectangle rectangle = getRectangle();
        Image windowScreenshot = Screen.INSTANCE.getCapture(rectangle);
        Optional<WindowPoint> optionalPoint = windowScreenshot.getLeftTopPointOf(image);
        if (optionalPoint.isEmpty()) {
            return Optional.empty();
        }
        WindowPoint point = optionalPoint.get();
        return Optional.of(new ScreenPoint(point.x() + rectangle.x, point.y() + rectangle.y));
    }

    private Rectangle getRectangle() {
        RECT rect = new RECT();
        User32.INSTANCE.GetWindowRect(hWnd, rect);
        return rect.toRectangle();
    }

    public Optional<ScreenPoint> getCenterPointOf(Image image) {
        Rectangle rectangle = getRectangle();
        Image windowScreenshot = Screen.INSTANCE.getCapture(rectangle);
        Optional<WindowPoint> optionalPoint = windowScreenshot.getCenterPointOf(image);
        if (optionalPoint.isEmpty()) {
            return Optional.empty();
        }
        WindowPoint point = optionalPoint.get();
        return Optional.of(new ScreenPoint(point.x() + rectangle.x, point.y() + rectangle.y));
    }

    public boolean isImageVisibleNow(Image image) {
        return getLeftTopPointOf(image).isPresent();
    }

    public ScreenPoint getWindowCenterPoint() {
        Rectangle rect = getRectangle();
        return new ScreenPoint(rect.x + rect.width / 2, rect.y + rect.height / 2);
    }

}
