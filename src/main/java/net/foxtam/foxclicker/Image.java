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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class Image {
    private static final Cleaner cleaner = Cleaner.create();

    static {
        nu.pattern.OpenCV.loadLocally();
    }

    private final Mat colorMat;
    private final String name;
    private Mat grayMat;

    private Image(Mat colorMat, String name) {
        this.colorMat = colorMat;
        this.name = name;
        cleaner.register(this, this.colorMat::release);
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
//        Imgproc.cvtColor(matrix, matrix, Imgproc.COLOR_BGR2GRAY);
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
//            Imgproc.cvtColor(matrix, matrix, Imgproc.COLOR_BGR2GRAY);
            return new Image(matrix, path);
        }
    }

    public int width() {
        return colorMat.width();
    }

    public int height() {
        return colorMat.height();
    }

    public Optional<WindowPoint> getPointOf(Image image, double tolerance, boolean inColor) {
        if (this.colorMat.width() < image.colorMat.width() || this.colorMat.height() < image.colorMat.height()) {
            return Optional.empty();
        }
        Mat result = new Mat();
        if (inColor) {
            Imgproc.matchTemplate(this.colorMat(), image.colorMat(), result, Imgproc.TM_CCOEFF_NORMED);
        } else {
            Imgproc.matchTemplate(this.grayMat(), image.grayMat(), result, Imgproc.TM_CCOEFF_NORMED);
        }
        Core.MinMaxLocResult loc = Core.minMaxLoc(result);
        result.release();

        if (loc.maxVal >= tolerance) {
            return Optional.of(new WindowPoint((int) loc.maxLoc.x, (int) loc.maxLoc.y));
        } else {
            return Optional.empty();
        }
    }

    private Mat colorMat() {
        return colorMat;
    }

    private Mat grayMat() {
        if (grayMat == null) {
            grayMat = new Mat();
            cleaner.register(this, this.grayMat::release);
            Imgproc.cvtColor(colorMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        }
        return grayMat;
    }

    public List<WindowPoint> getAllPointsOf(Image image, double tolerance, boolean inColor) {
        if (this.colorMat.width() < image.colorMat.width() || this.colorMat.height() < image.colorMat.height()) {
            return Collections.emptyList();
        }
        Mat result = new Mat();
        if (inColor) {
            Imgproc.matchTemplate(this.colorMat(), image.colorMat(), result, Imgproc.TM_CCOEFF_NORMED);
        } else {
            Imgproc.matchTemplate(this.grayMat(), image.grayMat(), result, Imgproc.TM_CCOEFF_NORMED);
        }
        ArrayList<WindowPoint> points = selectPoints(result, tolerance);
        result.release();
        return points;
    }

    private ArrayList<WindowPoint> selectPoints(Mat result, double tolerance) {
        ArrayList<WindowPoint> points = new ArrayList<>();
        for (int row = 0; row < result.height(); row++) {
            for (int col = 0; col < result.width(); col++) {
                if (result.get(row, col)[0] >= tolerance) {
                    points.add(new WindowPoint(col, row));
                }
            }
        }
        return points;
    }

    @Override
    public String toString() {
        return "Image{" +
              "name='" + name + '\'' +
              '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return matEquals(this.grayMat(), image.grayMat());
    }

    private static boolean matEquals(Mat src1, Mat src2) {
        if (src1.cols() != src2.cols() || src1.rows() != src2.rows() || src1.type() != src2.type())
            return false;
        Mat cmpRes = new Mat();
        Core.compare(src1, src2, cmpRes, Core.CMP_NE);
        boolean result = Core.countNonZero(cmpRes) == 0;
        cmpRes.release();
        return result;
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }
}
