package net.foxtam.foxclicker;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.WinUser.*;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface User32 extends StdCallLibrary {
    User32 INSTANCE = (User32) Native.load("user32", User32.class, W32APIOptions.UNICODE_OPTIONS);

    HWND FindWindow(String className, String windowName);

    boolean SetForegroundWindow(HWND hWnd);

    boolean ShowWindow(HWND hWnd, int nCmdShow);

    BOOL GetWindowPlacement(HWND hWnd, WINDOWPLACEMENT lpwndpl);

    HWND FindWindowEx(HWND parent, HWND child, String className, String window);

    HWND GetForegroundWindow();

    boolean GetWindowRect(HWND hWnd, RECT rect);
}
