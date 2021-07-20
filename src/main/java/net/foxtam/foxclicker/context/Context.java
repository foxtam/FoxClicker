package net.foxtam.foxclicker.context;

import com.sun.jna.platform.win32.WinDef;
import net.foxtam.foxclicker.Image;
import net.foxtam.foxclicker.LifeController;
import net.foxtam.foxclicker.Pair;
import net.foxtam.foxclicker.mouse.Mouse;
import net.foxtam.foxclicker.window.Window;

import java.util.List;

public interface Context {
    static Context foreground(WinDef.HWND hWnd, List<Pair<Image, Runnable>> checks) {
        return new ForegroundContext(hWnd, checks);
    }

    static Context background(WinDef.HWND hWnd, List<Pair<Image, Runnable>> checks) {
        return new BackgroundContext(hWnd, checks);
    }

    Window getWindow(double tolerance, boolean inColor);

    Mouse getMouse(LifeController lifeController);
}
