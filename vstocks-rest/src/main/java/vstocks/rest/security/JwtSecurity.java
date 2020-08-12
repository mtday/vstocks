package vstocks.rest.security;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jwt.config.encryption.EncryptionConfiguration;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;
import vstocks.model.User;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MINUTES;
import static vstocks.config.Config.*;

public class JwtSecurity {
    private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int ITERATIONS = 256;
    private static final int KEY_LENGTH = 256;

    private final SignatureConfiguration signatureConfiguration;
    private final EncryptionConfiguration encryptionConfiguration;

    public JwtSecurity() {
        signatureConfiguration = new SecretSignatureConfiguration(createKey(JWT_SIGNATURE_SECRET.getString()));
        encryptionConfiguration = new SecretEncryptionConfiguration(createKey(JWT_ENCRYPTION_SECRET.getString()));
    }

    static byte[] createKey(String secret) {
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
            KeySpec keySpec = new PBEKeySpec(secret.toCharArray(), secret.getBytes(UTF_8), ITERATIONS, KEY_LENGTH);
            return secretKeyFactory.generateSecret(keySpec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateToken(User user) {
        long expirationSeconds = MINUTES.toSeconds(JWT_EXPIRATION_MINUTES.getInt());
        Date expiration = new Date(Instant.now().plusSeconds(expirationSeconds).toEpochMilli());

        JwtGenerator<CommonProfile> jwtGenerator = new JwtGenerator<>();
        jwtGenerator.setSignatureConfiguration(signatureConfiguration);
        jwtGenerator.setEncryptionConfiguration(encryptionConfiguration);
        jwtGenerator.setExpirationTime(expiration);
        return jwtGenerator.generate(user.getJwtClaims());
    }

    public Optional<User> validateToken(String token) {
        JwtAuthenticator jwtAuthenticator = new JwtAuthenticator();
        jwtAuthenticator.setSignatureConfiguration(signatureConfiguration);
        jwtAuthenticator.setEncryptionConfiguration(encryptionConfiguration);
        return User.getUserFromJwtClaims(jwtAuthenticator.validateTokenAndGetClaims(token));
    }
}
