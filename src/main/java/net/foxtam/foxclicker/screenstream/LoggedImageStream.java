package net.foxtam.foxclicker.screenstream;

import lombok.SneakyThrows;
import net.foxtam.foxclicker.GlobalLogger;
import net.foxtam.foxclicker.Robo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;

public class LoggedImageStream implements ImageStream {

    private static final int numberOfLogScreenshots = 500;
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH-mm-ss-SSS");
    private final Queue<Path> screenshotPathQueue = new LinkedList<>();
    private final Path screenDirectory = GlobalLogger.LOG_DIRECTORY.resolve("screen");
    private final ImageStream imageStream;
    private BufferedImage lastScreenshot;

    public LoggedImageStream(ImageStream imageStream) {
        this.imageStream = imageStream;
        this.lastScreenshot = Robo.getInstance().createScreenCapture(new Rectangle(0, 0, 10, 10));
    }

    @Override
    public BufferedImage getNextImage() {
        BufferedImage image = imageStream.getNextImage();
        saveImage(image, LocalTime.now().format(timeFormatter));
        return image;
    }

    private void saveImage(BufferedImage image, String fileName) {
        Thread thread = new Thread(() -> save(image, fileName));
        thread.setDaemon(true);
        thread.start();
    }

    @SneakyThrows
    private void save(BufferedImage image, String fileName) {
        synchronized (screenshotPathQueue) {
            if (!compareImages(image, lastScreenshot)) {
                trySave(image, fileName);
                lastScreenshot = image;
            }
        }
    }

    public static boolean compareImages(BufferedImage first, BufferedImage second) {
        if (first.getWidth() != second.getWidth() || first.getHeight() != second.getHeight()) {
            return false;
        }
        int width = first.getWidth();
        int height = first.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (first.getRGB(x, y) != second.getRGB(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }

    @SneakyThrows
    private void trySave(BufferedImage image, String fileName) {
        Path imagePath = screenDirectory.resolve(fileName + ".jpeg");
        if (Files.notExists(screenDirectory)) Files.createDirectories(screenDirectory);
        ImageIO.write(image, "jpeg", imagePath.toFile());
        screenshotPathQueue.offer(imagePath);
        if (screenshotPathQueue.size() > numberOfLogScreenshots) {
            Files.deleteIfExists(screenshotPathQueue.remove());
        }
    }
}
