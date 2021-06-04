package net.foxtam;

import net.foxtam.exceptions.BufferedImageProcessingException;
import net.foxtam.exceptions.LoadImageException;
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


public class Image {
    private static final Cleaner cleaner = Cleaner.create();

    static {
        nu.pattern.OpenCV.loadShared();
    }

    private final Mat matrix;

    private Image(Mat matrix) {
        this.matrix = matrix;
        cleaner.register(this, matrix::release);
    }

    public static Image loadFromBufferedImage(BufferedImage bufferedImage) {
        return new Image(bufferedImgToMat(bufferedImage));
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
        Mat matrix = bufferedImgToMat(image);
        Imgproc.cvtColor(matrix, matrix, Imgproc.COLOR_BGR2GRAY);
        return new Image(matrix);
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
            Mat matrix = bufferedImgToMat(image);
            Imgproc.cvtColor(matrix, matrix, Imgproc.COLOR_BGR2GRAY);
            return new Image(matrix);
        }
    }

    private static Mat bufferedImgToMat(BufferedImage bufferedImage) {
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
}
