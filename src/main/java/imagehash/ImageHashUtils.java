package imagehash;

import imagehash.exception.NoSuchAlgorithmException;
import imagehash.utils.ImageHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ImageHashUtils {

    public static final int HASH_SIZE = 8;

    private static final ImageHelper imageHelper = new ImageHelper();

    public static String getImageHash(InputStream is, ImageHashAlgorithm algorithm) throws IOException {
        switch (algorithm) {
            case AVERAGE_HASH:
                return averageHash(is);
            case PERCEPTUAL_HASH:
                return pHash(is);
            default:
                throw new NoSuchAlgorithmException(algorithm);
        }

    }

    /**
     * Implementation of average hash algorithm following http://www.hackerfactor.com/blog/index.php?/archives/432-Looks-Like-It.html
     */
    public static String averageHash(InputStream is) throws IOException {
        BufferedImage img = ImageIO.read(is);

        img = imageHelper.resize(img, HASH_SIZE);

        img = imageHelper.grayscale(img);

        int[] pixels = imageHelper.getSingleValuePixels(img);

        double mean = Arrays.stream(pixels).average().orElse(0);

        return Arrays.stream(pixels)
                .boxed()
                .map(p -> p > mean ? "1" : "0")
                .collect(Collectors.joining());
    }

    /**
     * Implementation of pHash algorithm following http://www.hackerfactor.com/blog/index.php?/archives/432-Looks-Like-It.html
     */
    public static String pHash(InputStream is) throws IOException {
        BufferedImage img = ImageIO.read(is);

        final int highFreqFactor = 4;

        img = imageHelper.resize(img, HASH_SIZE * highFreqFactor);

        img = imageHelper.grayscale(img);

        double[][] dct = imageHelper.applyDCT(img, HASH_SIZE * highFreqFactor);

        double[][] dctLowFreq = new double[HASH_SIZE][HASH_SIZE];
        for (int i = 0; i < HASH_SIZE; i++) {
            dctLowFreq[i] = Arrays.copyOfRange(dct[i], 0, HASH_SIZE);
        }

        // skip first pixel, since it may differ a lot from others
        double mean = Arrays.stream(dctLowFreq).flatMapToDouble(Arrays::stream).skip(1).average().orElse(0);

        return Arrays.stream(dctLowFreq)
                .flatMapToDouble(Arrays::stream)
                .boxed()
                .map(p -> p > mean ? "1" : "0")
                .collect(Collectors.joining());
    }
}
