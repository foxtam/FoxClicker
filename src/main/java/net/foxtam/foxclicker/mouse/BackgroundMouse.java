package net.foxtam.foxclicker.mouse;

import com.sun.jna.platform.win32.WinDef;
import lombok.SneakyThrows;
import net.foxtam.foxclicker.Direction;
import net.foxtam.foxclicker.LifeController;
import net.foxtam.foxclicker.ScreenPoint;
import net.foxtam.foxclicker.User32;
import net.foxtam.foxclicker.window.WindowFrame;

import java.awt.*;

public class BackgroundMouse implements Mouse {
    public static final int WM_LBUTTONUP = 514;
    public static final int WM_LBUTTONDOWN = 513;
    public static final int WM_LBUTTONDBLCLK = 0x203;
    public static final int MK_LBUTTON = 0x0001;
    public static final int WM_MOUSEMOVE = 0x0200;
    private static final int mouseDelay = 100;
    private final LifeController lifeController;
    private final WindowFrame windowFrame;

    public BackgroundMouse(LifeController lifeController, WinDef.HWND hWnd) {
        this.lifeController = lifeController;
        this.windowFrame = new WindowFrame(hWnd);
    }

    @Override
    public void drag(Direction direction, int lengthInPixel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void dragTo(ScreenPoint point) {
        throw new UnsupportedOperationException();
    }

    @SneakyThrows
    @Override
    public void leftClickAt(ScreenPoint point) {
        Rectangle rect = windowFrame.getRectangle();

        moveTo(point);
        User32.INSTANCE.SendMessage(
                windowFrame.getHWND(),
                WM_LBUTTONDOWN,
                new WinDef.WPARAM(MK_LBUTTON),
                makeLParam(point.getX() - rect.x, point.getY() - rect.y));

        lifeController.sleep(mouseDelay);

        moveTo(point);
        User32.INSTANCE.SendMessage(
                windowFrame.getHWND(),
                WM_LBUTTONUP,
                new WinDef.WPARAM(MK_LBUTTON),
                makeLParam(point.getX() - rect.x, point.getY() - rect.y));
    }

    @Override
    public void moveTo(ScreenPoint point) {
        lifeController.sleep(1);
        Rectangle rect = windowFrame.getRectangle();
        User32.INSTANCE.SendMessage(
                windowFrame.getHWND(),
                WM_MOUSEMOVE,
                new WinDef.WPARAM(0),
                makeLParam(point.getX() - rect.x, point.getY() - rect.y));
    }

    // int args are needed for unsigned 16-bit values
    private static WinDef.LPARAM makeLParam(int l, int h) {
        // note the high word bitmask must include L
        return new WinDef.LPARAM((l & 0xffff) | (h & 0xffffL) << 16);
    }
}
