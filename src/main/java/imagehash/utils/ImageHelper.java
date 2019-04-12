package imagehash.utils;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

public class ImageHelper {

    private ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);

    public BufferedImage grayscale(BufferedImage img) {
        return colorConvert.filter(img, null);
    }

    public BufferedImage resize(BufferedImage img, int size) {
        BufferedImage resizedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(img, 0, 0, size, size, null);
        g.dispose();
        return resizedImage;
    }

    public int[] getSingleValuePixels(BufferedImage img) {
        int[] rgb = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
        for (int i = 0; i < rgb.length; i++) {
            rgb[i] = rgb[i] & 0xff;
        }
        return rgb;
    }

    // https://stackoverflow.com/questions/4240490/problems-with-dct-and-idct-algorithm-in-java
    public double[][] applyDCT(BufferedImage img, int size) {
        double[][] result = new double[size][size];
        for (int u = 0; u < size; u++) {
            for (int v = 0; v < size; v++) {
                double sum = 0.0;
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        sum += Math.cos(((2 * i + 1) / (2.0 * size)) * u * Math.PI)
                                * Math.cos(((2 * j + 1) / (2.0 * size)) * v * Math.PI)
                                * (img.getRGB(i, j) & 0xff);
                    }
                }
                sum *= ((getDCTCoeff(u) * getDCTCoeff(v)) / 4.0);
                result[u][v] = sum;
            }
        }
        return result;
    }

    private double getDCTCoeff(int idx) {
        return idx == 0 ? 1 / Math.sqrt(2) : 1;
    }
}
