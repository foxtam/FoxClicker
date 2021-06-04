package net.foxtam.foxclicker;

import java.awt.*;
import java.util.Optional;

import static com.sun.jna.platform.win32.WinDef.*;
import static com.sun.jna.platform.win32.WinUser.*;

public class Window {
    private final HWND hWnd;
    private final Screen screen;

    private Window(HWND hWnd, Screen screen) {
        this.hWnd = hWnd;
        this.screen = screen;
    }

    public static Window getByClass(String windowClassName, Screen screen) {
        return new Window(User32.INSTANCE.FindWindow(windowClassName, null), screen);
    }

    public static Window getByTitle(String windowTitle, Screen screen) {
        return new Window(User32.INSTANCE.FindWindow(null, windowTitle), screen);
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
            Util.sleep(5);
        } while (!User32.INSTANCE.GetForegroundWindow().equals(hWnd));
    }

    public Optional<ScreenPoint> getCenterPointOf(Image image) {
        Image screenshot = screen.getCapture(getRectangle());
        return screenshot.getCenterPointOf(image);
    }

    private Rectangle getRectangle() {
        RECT rect = new RECT();
        User32.INSTANCE.GetWindowRect(hWnd, rect);
        return rect.toRectangle();
    }
}
