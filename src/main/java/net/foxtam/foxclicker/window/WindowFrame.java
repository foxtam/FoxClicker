package net.foxtam.foxclicker.window;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import net.foxtam.foxclicker.Robo;
import net.foxtam.foxclicker.ScreenPoint;
import net.foxtam.foxclicker.User32;

import java.awt.*;

import static com.sun.jna.platform.win32.WinUser.*;

public class WindowFrame {
    private final WinDef.HWND hWnd;

    public WindowFrame(WinDef.HWND hWnd) {
        this.hWnd = hWnd;
    }

    public ScreenPoint getWindowCenterPoint() {
        activate();
        Rectangle rect = getRectangle();
        return ScreenPoint.of(rect.x + rect.width / 2, rect.y + rect.height / 2);
    }

    public void activate() {
        var winPlace = new WinUser.WINDOWPLACEMENT();
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
}
