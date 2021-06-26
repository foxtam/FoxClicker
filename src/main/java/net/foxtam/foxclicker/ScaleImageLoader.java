package net.foxtam.foxclicker;

import lombok.Cleanup;
import lombok.SneakyThrows;
import net.foxtam.foxclicker.exceptions.LoadImageException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ScaleImageLoader {
    private final double scale;

    public ScaleImageLoader(double scale) {
        this.scale = scale;
    }

    @SneakyThrows
    public Image loadFromFile(String path) {
        BufferedImage image = ImageIO.read(new File(path));
        return new Image(image, path, scale);
    }

    @SneakyThrows
    public Image loadFromResource(String path) {
        @Cleanup
        InputStream resourceAsStream = Image.class.getResourceAsStream(path);
        if (resourceAsStream == null) {
            throw new IOException("Failed to load resource: " + path);
        }
        BufferedImage image = ImageIO.read(resourceAsStream);
        return new Image(image, path, scale);
    }
}
