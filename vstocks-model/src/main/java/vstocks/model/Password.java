package vstocks.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Password {
    private static final String ALGORITHM = "SHA-512";
    private static final String SALT = "correct-horse-battery-staple";

    public static String hash(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
            StringBuilder hashed = new StringBuilder();
            String saltedPass = password + SALT;
            messageDigest.update(saltedPass.getBytes(UTF_8));
            byte[] digest = messageDigest.digest();
            for (byte b : digest) {
                hashed.append(String.format("%02x", 0xFF & b));
            }
            return hashed.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
