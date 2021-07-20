package net.foxtam.foxclicker.context;

import com.sun.jna.platform.win32.WinDef;
import net.foxtam.foxclicker.Image;
import net.foxtam.foxclicker.LifeController;
import net.foxtam.foxclicker.Pair;
import net.foxtam.foxclicker.mouse.BackgroundMouse;
import net.foxtam.foxclicker.mouse.Mouse;
import net.foxtam.foxclicker.window.BackgroundWindow;
import net.foxtam.foxclicker.window.Window;

import java.util.List;

public class BackgroundContext implements Context {
    private final WinDef.HWND hWnd;
    private final List<Pair<Image, Runnable>> checks;

    public BackgroundContext(WinDef.HWND hWnd, List<Pair<Image, Runnable>> checks) {
        this.hWnd = hWnd;
        this.checks = checks;
    }

    @Override
    public Window getWindow(double tolerance, boolean inColor) {
        return new BackgroundWindow(hWnd, tolerance, inColor, checks);
    }

    @Override
    public Mouse getMouse(LifeController lifeController) {
        return new BackgroundMouse(lifeController, hWnd);
    }
}
