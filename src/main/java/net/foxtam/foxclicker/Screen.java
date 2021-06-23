package net.foxtam.foxclicker;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;

public class Screen {

    public static final Screen INSTANCE = new Screen();
    private static final Queue<Path> screenshotPathQueue = new LinkedList<>();
    private static final Path screenDirectory = GlobalLogger.LOG_DIRECTORY.resolve("screen");
    private static final int numberOfLogScreenshots = 500;
    private Image lastScreenshot;

    private Screen() {
        BufferedImage capture =
                Robo.INSTANCE.createScreenCapture(
                        new Rectangle(0, 0, 10, 10));
        this.lastScreenshot = new Image(capture, "empty");
    }

    public Image getCapture(Rectangle rectangle) {
        BufferedImage screenBuffered = Robo.INSTANCE.createScreenCapture(rectangle);
        LocalTime now = LocalTime.now();
        Image screenImage = new Image(screenBuffered, "screenshot-" + now);
        saveImage(screenImage, screenBuffered, now);
        return screenImage;
    }

    private void saveImage(Image image, BufferedImage buffered, LocalTime time) {
        Thread thread = new Thread(() -> save(image, buffered, time));
        thread.setDaemon(true);
        thread.start();
    }

    private void save(Image image, BufferedImage buffered, LocalTime time) {
        synchronized (screenshotPathQueue) {
            try {
                if (!image.equals(lastScreenshot)) {
                    trySave(buffered, time);
                    lastScreenshot = image;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void trySave(BufferedImage image, LocalTime time) throws IOException {
        String strTime = time.format(DateTimeFormatter.ofPattern("HH-mm-ss-SSS"));
        Path imagePath = screenDirectory.resolve(strTime + ".jpeg");
        if (Files.notExists(screenDirectory)) Files.createDirectories(screenDirectory);
        ImageIO.write(image, "jpeg", imagePath.toFile());
        screenshotPathQueue.offer(imagePath);
        if (screenshotPathQueue.size() > numberOfLogScreenshots) {
            Files.deleteIfExists(screenshotPathQueue.remove());
        }
    }
}
