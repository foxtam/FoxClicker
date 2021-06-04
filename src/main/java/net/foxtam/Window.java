package net.foxtam;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import java.util.concurrent.TimeUnit;

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
        User32.INSTANCE.SetForegroundWindow(hWnd);
        while (User32.INSTANCE.GetForegroundWindow() != hWnd) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
