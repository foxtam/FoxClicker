package net.foxtam.foxclicker;

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
        if (winPlace.showCmd == SW_SHOWMAXIMIZED) {
            User32.INSTANCE.ShowWindow(hWnd, SW_SHOWMAXIMIZED);
        } else if (winPlace.showCmd == SW_SHOWMINIMIZED) {
            User32.INSTANCE.ShowWindow(hWnd, SW_RESTORE);
        } else {
            User32.INSTANCE.ShowWindow(hWnd, SW_NORMAL);
        }
        do {
            User32.INSTANCE.SetForegroundWindow(hWnd);
            Util.sleep(5);
        } while (!User32.INSTANCE.GetForegroundWindow().equals(hWnd));
    }
}
