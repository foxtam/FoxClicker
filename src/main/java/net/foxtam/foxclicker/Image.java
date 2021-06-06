package net.foxtam.foxclicker;

import net.foxtam.foxclicker.exceptions.BufferedImageProcessingException;
import net.foxtam.foxclicker.exceptions.LoadImageException;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Cleaner;
import java.util.Optional;


public class Image {
    private static final Cleaner cleaner = Cleaner.create();
    private static final double tolerance = 0.8;

    static {
        nu.pattern.OpenCV.loadLocally();
    }

    private final Mat mat;
    private final String name;

    private Image(Mat mat, String name) {
        this.mat = mat;
        this.name = name;
        cleaner.register(this, this.mat::release);
    }

    public static Image from(BufferedImage bufferedImage, String name) {
        return new Image(getMatFrom(bufferedImage), name);
    }

    private static Mat getMatFrom(BufferedImage bufferedImage) {
        DataBuffer dataBuffer = bufferedImage.getRaster().getDataBuffer();
        byte[] imgPixels;

        if (dataBuffer instanceof DataBufferInt) {
            int byteSize = bufferedImage.getWidth() * bufferedImage.getHeight();
            imgPixels = new byte[byteSize * 3];
            int[] imgIntegerPixels = ((DataBufferInt) dataBuffer).getData();
            for (int i = 0; i < byteSize; i++) {
                imgPixels[i * 3] = (byte) (imgIntegerPixels[i] & 0x000000FF);
                imgPixels[i * 3 + 1] = (byte) ((imgIntegerPixels[i] & 0x0000FF00) >> 8);
                imgPixels[i * 3 + 2] = (byte) ((imgIntegerPixels[i] & 0x00FF0000) >> 16);
            }
        } else if (dataBuffer instanceof DataBufferByte) {
            imgPixels = ((DataBufferByte) dataBuffer).getData();
        } else {
            throw new BufferedImageProcessingException();
        }

        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, imgPixels);
        return mat;
    }

    public static Image loadFromFile(String path) {
        try {
            return tryLoadFromFile(path);
        } catch (IOException e) {
            throw new LoadImageException("Failed to load image: " + path, e);
        }
    }

    private static Image tryLoadFromFile(String path) throws IOException {
        BufferedImage image = ImageIO.read(new FileInputStream(path));
        Mat matrix = getMatFrom(image);
        Imgproc.cvtColor(matrix, matrix, Imgproc.COLOR_BGR2GRAY);
        return new Image(matrix, path);
    }

    public static Image loadFromResource(String path) {
        try {
            return tryLoadFromResource(path);
        } catch (IOException e) {
            throw new LoadImageException("Failed to load image: " + path, e);
        }
    }

    private static Image tryLoadFromResource(String path) throws IOException {
        try (InputStream resourceAsStream = Image.class.getResourceAsStream(path)) {
            if (resourceAsStream == null) {
                throw new IOException();
            }
            BufferedImage image = ImageIO.read(resourceAsStream);
            Mat matrix = getMatFrom(image);
            Imgproc.cvtColor(matrix, matrix, Imgproc.COLOR_BGR2GRAY);
            return new Image(matrix, path);
        }
    }

    public Image toGray() {
        Mat gray = new Mat();
        Imgproc.cvtColor(this.mat, gray, Imgproc.COLOR_BGR2GRAY);
        return new Image(gray, "(gray) " + name);
    }

    public Optional<WindowPoint> getCenterPointOf(Image image) {
        Optional<WindowPoint> leftTop = getLeftTopPointOf(image);
        if (leftTop.isEmpty()) {
            return leftTop;
        }
        WindowPoint point = leftTop.get();
        return Optional.of(
            new WindowPoint(
                point.x() + image.width() / 2,
                point.y() + image.height() / 2));
    }

    public Optional<WindowPoint> getLeftTopPointOf(Image image) {
        if (this.mat.width() < image.mat.width() || this.mat.height() < image.mat.height()) {
            return Optional.empty();
        }
        Mat result = new Mat();
        Imgproc.matchTemplate(this.mat, image.mat, result, Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult loc = Core.minMaxLoc(result);
        result.release();

        if (loc.maxVal >= tolerance) {
            return Optional.of(new WindowPoint((int) loc.maxLoc.x, (int) loc.maxLoc.y));
        } else {
            return Optional.empty();
        }
    }

    private int width() {
        return mat.width();
    }

    private int height() {
        return mat.height();
    }

    @Override
    public String toString() {
        return "Image{" +
            "name='" + name + '\'' +
            '}';
    }
}
