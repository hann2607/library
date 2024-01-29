package net.sparkminds.library.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;

public class ImageBase64Utils {

    /**
     * Encode a MultipartFile to Base64.
     *
     * @param file MultipartFile to be encoded.
     * @return Base64 encoded string.
     * @throws IOException If there is an issue reading the MultipartFile.
     */
    public static String encodeMultipartFileToBase64(MultipartFile file) throws IOException {
        byte[] fileContent = file.getBytes();
        return Base64.encodeBase64String(fileContent);
    }
    
    public static String encodeImageToBase64(Path imagePath) throws IOException {
        byte[] imageBytes = Files.readAllBytes(imagePath);
        byte[] base64EncodedBytes = Base64.encodeBase64(imageBytes);
        return new String(base64EncodedBytes);
    }
}
