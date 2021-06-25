package net.foxtam.foxclicker;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
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
    private final Mat grayMat;

    public Image(BufferedImage image, String name) {
        colorMat = new MatFromBuffered(image);
        cleaner.register(this, colorMat::release);

        grayMat = toGrayMat(colorMat);
        cleaner.register(this, grayMat::release);

        this.name = name;
    }

    private Mat toGrayMat(Mat colored) {
        Mat gray = new Mat();
        Imgproc.cvtColor(colored, gray, Imgproc.COLOR_BGR2GRAY);
        return gray;
    }

    public Image(BufferedImage image, String name, double scale) {
        Mat original = new MatFromBuffered(image);
        colorMat = resize(original, scale);
        original.release();
        cleaner.register(this, colorMat::release);

        grayMat = toGrayMat(colorMat);
        cleaner.register(this, grayMat::release);

        this.name = name;
    }

    private static Mat resize(Mat original, double scale) {
        Mat resized = new Mat();
        Imgproc.resize(original, resized, new Size(), scale, scale, Imgproc.INTER_AREA);
        return resized;
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
            Imgproc.matchTemplate(colorMat, image.colorMat, result, Imgproc.TM_CCOEFF_NORMED);
        } else {
            Imgproc.matchTemplate(grayMat, image.grayMat, result, Imgproc.TM_CCOEFF_NORMED);
        }
        Core.MinMaxLocResult loc = Core.minMaxLoc(result);
        result.release();

        if (loc.maxVal >= tolerance) {
            return Optional.of(new WindowPoint((int) loc.maxLoc.x, (int) loc.maxLoc.y));
        } else {
            return Optional.empty();
        }
    }

    public List<WindowPoint> getAllPointsOf(Image image, double tolerance, boolean inColor) {
        if (this.colorMat.width() < image.colorMat.width() || this.colorMat.height() < image.colorMat.height()) {
            return Collections.emptyList();
        }
        Mat result = new Mat();
        if (inColor) {
            Imgproc.matchTemplate(colorMat, image.colorMat, result, Imgproc.TM_CCOEFF_NORMED);
        } else {
            Imgproc.matchTemplate(grayMat, image.grayMat, result, Imgproc.TM_CCOEFF_NORMED);
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
        return matEquals(grayMat, image.grayMat);
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
