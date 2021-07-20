package net.foxtam.foxclicker.context;

import com.sun.jna.platform.win32.WinDef;
import net.foxtam.foxclicker.*;
import net.foxtam.foxclicker.mouse.ForegroundMouse;
import net.foxtam.foxclicker.window.ForegroundWindow;

import java.util.List;

class ForegroundContext implements Context {
    
    private final WinDef.HWND hWnd;
    private final List<Pair<Image, Runnable>> checks;

    public ForegroundContext(WinDef.HWND hWnd, List<Pair<Image, Runnable>> checks) {
        this.hWnd = hWnd;
        this.checks = checks;
    }

    @Override
    public ForegroundWindow getWindow(double tolerance, boolean inColor) {
        return new ForegroundWindow(hWnd, tolerance, inColor, checks);
    }

    @Override
    public ForegroundMouse getMouse(LifeController lifeController) {
        return new ForegroundMouse(lifeController, hWnd);
    }
}
