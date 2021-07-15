package net.foxtam.foxclicker.screen;

import lombok.AllArgsConstructor;
import net.foxtam.foxclicker.Image;
import net.foxtam.foxclicker.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@AllArgsConstructor(staticName = "of")
public class CheckScreen implements Screen {

    private final double tolerance;
    private final boolean inColor;
    private final List<Pair<Image, Runnable>> checks;

    @Override
    public BufferedImage getScreenCapture(Rectangle rectangle) {
        BufferedImage screenCapture = LoggedScreen.getInstance().getScreenCapture(rectangle);
        check(Image.from(screenCapture));
        return screenCapture;
    }

    private void check(Image screenImage) {
        for (Pair<Image, Runnable> check : checks) {
            if (screenImage.getPointOf(check.getFirst(), tolerance, inColor).isPresent()) {
                check.getSecond().run();
            }
        }
    }
}
