package net.foxtam.foxclicker;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Screen {
    
    public Image getCapture(Rectangle rectangle) {
        BufferedImage screenCapture = Robo.INSTANCE.createScreenCapture(rectangle);
        Mat matScreenCapture = Image.getMatFrom(screenCapture);
        Mat grayScreen = new Mat();
        Imgproc.cvtColor(matScreenCapture, grayScreen, Imgproc.COLOR_BGR2GRAY);
        matScreenCapture.release();
        return Image.fromMat(grayScreen);
    }
}
