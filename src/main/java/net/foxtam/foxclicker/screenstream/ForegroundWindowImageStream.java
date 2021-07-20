package net.foxtam.foxclicker.screenstream;

import com.sun.jna.platform.win32.WinDef;
import net.foxtam.foxclicker.Robo;
import net.foxtam.foxclicker.window.WindowFrame;

import java.awt.image.BufferedImage;

public class ForegroundWindowImageStream implements ImageStream {

    private final WindowFrame windowFrame;

    public ForegroundWindowImageStream(WinDef.HWND hWnd) {
        this.windowFrame = new WindowFrame(hWnd);
    }

    @Override
    public BufferedImage getNextImage() {
        return Robo.getInstance().createScreenCapture(windowFrame.getRectangle());
    }
}
