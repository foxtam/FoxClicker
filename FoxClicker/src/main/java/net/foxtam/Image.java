package net.foxtam;

import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;

public class Image {
    static {
        nu.pattern.OpenCV.loadShared();
    }

    private final Mat matrix;

    public Image(Mat matrix) {
        this.matrix = matrix;
    }

    private static Mat bufferedImgToMat(BufferedImage bufferedImage) {
        DataBuffer dataBuffer = bufferedImage.getRaster().getDataBuffer();
        byte[] imgPixels;

        if (dataBuffer instanceof DataBufferInt) {
            int byteSize = bufferedImage.getWidth() * bufferedImage.getHeight();
            imgPixels = new byte[byteSize * 3];
            int[] imgIntegerPixels = ((DataBufferInt) dataBuffer).getData();
            for (int i = 0; i < byteSize; i++) {
                imgPixels[i * 3 + 2] = (byte) ((imgIntegerPixels[i] & 0x00FF0000) >> 16);
                imgPixels[i * 3 + 1] = (byte) ((imgIntegerPixels[i] & 0x0000FF00) >> 8);
                imgPixels[i * 3] = (byte) (imgIntegerPixels[i] & 0x000000FF);
            }
        } else if(dataBuffer instanceof DataBufferByte) {
            imgPixels = ((DataBufferByte) dataBuffer).getData();
        } else {
            throw new BufferedImageProcessingException();
        }
    }
}
