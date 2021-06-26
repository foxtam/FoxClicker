package net.foxtam.foxclicker;

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

    public Image loadFromFile(String path) {
        try {
            return tryLoadFromFile(path);
        } catch (IOException e) {
            throw new LoadImageException("Failed to load image: " + path, e);
        }
    }

    private Image tryLoadFromFile(String path) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));
        return new Image(image, path, scale);
    }

    public Image loadFromResource(String path) {
        try {
            return tryLoadFromResource(path);
        } catch (IOException e) {
            throw new LoadImageException("Failed to load image: " + path, e);
        }
    }

    private Image tryLoadFromResource(String path) throws IOException {
        try (InputStream resourceAsStream = Image.class.getResourceAsStream(path)) {
            if (resourceAsStream == null) {
                throw new IOException();
            }
            BufferedImage image = ImageIO.read(resourceAsStream);
            return new Image(image, path, scale);
        }
    }
}
