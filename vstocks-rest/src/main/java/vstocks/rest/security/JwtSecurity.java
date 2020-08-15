package vstocks.rest.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.empty;
import static java.util.concurrent.TimeUnit.HOURS;
import static vstocks.config.Config.JWT_EXPIRATION_HOURS;
import static vstocks.config.Config.JWT_SIGNATURE_SECRET;

public class JwtSecurity {
    private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int ITERATIONS = 256;
    private static final int KEY_LENGTH = 256;

    private final SecretKey secretKey;

    public JwtSecurity() {
        secretKey = createKey(JWT_SIGNATURE_SECRET.getString());
    }

    static SecretKey createKey(String secret) {
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
            KeySpec keySpec = new PBEKeySpec(secret.toCharArray(), secret.getBytes(UTF_8), ITERATIONS, KEY_LENGTH);
            return secretKeyFactory.generateSecret(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    static JWTClaimsSet getClaimsSet(String userId) {
        long expirationSeconds = HOURS.toSeconds(JWT_EXPIRATION_HOURS.getInt());
        Date expiration = new Date(Instant.now().plusSeconds(expirationSeconds).toEpochMilli());
        return new JWTClaimsSet.Builder()
                .subject(userId)
                .audience("vstocks")
                .issuer("vstocks")
                .issueTime(new Date())
                .expirationTime(expiration)
                .build();
    }

    static Optional<String> getUserId(JWTClaimsSet claimsSet) {
        return Stream.of(claimsSet)
                .filter(Objects::nonNull)
                .filter(claims -> claims.getSubject() != null)
                .filter(claims -> "vstocks".equals(claims.getIssuer()))
                .filter(claims -> claims.getAudience() != null && claims.getAudience().contains("vstocks"))
                .filter(claims -> claims.getExpirationTime() != null)
                .filter(claims -> !claims.getExpirationTime().before(new Date()))
                .map(JWTClaimsSet::getSubject)
                .findFirst();
    }

    public String generateToken(String userId) {
        try {
            JWSSigner signer = new MACSigner(secretKey);
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), getClaimsSet(userId));
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<String> validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secretKey);
            if (!signedJWT.verify(verifier)) {
                return empty();
            }

            return getUserId(signedJWT.getJWTClaimsSet());
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }
}
