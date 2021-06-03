package net.foxtam;

import net.foxtam.exceptions.BufferedImageProcessingException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.lang.ref.Cleaner;

public class Image {
    private static final Cleaner cleaner = Cleaner.create();
    static {
        nu.pattern.OpenCV.loadShared();
    }

    private final Mat matrix;

    public Image(BufferedImage bufferedImage) {
        this.matrix = bufferedImgToMat(bufferedImage);
        cleaner.register(this, matrix::release);
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
