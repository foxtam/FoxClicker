package net.foxtam.foxclicker.screen;

import net.foxtam.foxclicker.Image;
import net.foxtam.foxclicker.Pair;
import net.foxtam.foxclicker.screenstream.ImageStream;

import java.awt.image.BufferedImage;
import java.util.List;

public class CheckImageStream implements ImageStream {

    private final ImageStream imageStream;
    private final double tolerance;
    private final boolean inColor;
    private final List<Pair<Image, Runnable>> checks;

    public CheckImageStream(ImageStream imageStream,
                            double tolerance,
                            boolean inColor,
                            List<Pair<Image, Runnable>> checks) {
        this.imageStream = imageStream;
        this.tolerance = tolerance;
        this.inColor = inColor;
        this.checks = checks;
    }

    @Override
    public BufferedImage getNextImage() {
        BufferedImage screenCapture = imageStream.getNextImage();
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
