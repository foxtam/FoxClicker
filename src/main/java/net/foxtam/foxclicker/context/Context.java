package net.foxtam.foxclicker.context;

import net.foxtam.foxclicker.LifeController;
import net.foxtam.foxclicker.mouse.ForegroundMouse;
import net.foxtam.foxclicker.mouse.Mouse;
import net.foxtam.foxclicker.window.Window;

public interface Context {
    Window getWindow(double tolerance, boolean inColor);
    Mouse getMouse(LifeController lifeController);
}
