package net.foxtam.foxclicker;

import net.foxtam.foxclicker.exceptions.UnableToFindWindow;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static com.sun.jna.platform.win32.WinDef.HWND;
import static com.sun.jna.platform.win32.WinDef.RECT;
import static com.sun.jna.platform.win32.WinUser.*;

public class Window {
    private final HWND hWnd;

    private Window(HWND hWnd) {
        this.hWnd = hWnd;
    }

    public static Window getByClass(String windowClassName) {
        HWND hWnd = User32.INSTANCE.FindWindow(windowClassName, null);
        if (hWnd == null) {
            throw new UnableToFindWindow("Unable to find the window with class: " + windowClassName);
        }
        return new Window(hWnd);
    }

    public static Window getByTitle(String windowTitle) {
        HWND hWnd = User32.INSTANCE.FindWindow(null, windowTitle);
        if (hWnd == null) {
            throw new UnableToFindWindow("Unable to find the window with title: " + windowTitle);
        }
        return new Window(hWnd);
    }

    public ScreenPoint getWindowCenterPoint() {
        activate();
        Rectangle rect = getRectangle();
        return new ScreenPoint(rect.x + rect.width / 2, rect.y + rect.height / 2);
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
            Robo.getInstance().delay(5);
        } while (!User32.INSTANCE.GetForegroundWindow().equals(hWnd));
    }

    public Rectangle getRectangle() {
        RECT rect = new RECT();
        activate();
        User32.INSTANCE.GetWindowRect(hWnd, rect);
        return rect.toRectangle();
    }

    public Optional<ScreenPoint> getPointOf(Image image, double tolerance, boolean inColor) {
        activate();
        Rectangle rect = getRectangle();
        return Screen.getInstance()
                .getCapture(rect)
                .getPointOf(image, tolerance, inColor)
                .map(p -> new ScreenPoint(p.x() + rect.x, p.y() + rect.y));
    }

    public List<ScreenPoint> getAllPointsOf(Image image, double tolerance, boolean inColor) {
        activate();
        Rectangle rect = getRectangle();
        return Screen.getInstance()
                .getCapture(rect)
                .getAllPointsOf(image, tolerance, inColor)
                .stream()
                .map(p -> new ScreenPoint(p.x() + rect.x, p.y() + rect.y))
                .toList();
    }
}
