package imagehash.exception;

import imagehash.ImageHashAlgorithm;

public class NoSuchAlgorithmException extends RuntimeException {

    public NoSuchAlgorithmException(ImageHashAlgorithm algorithm) {
        super("No such image hash algorithm " + algorithm);
    }
}
