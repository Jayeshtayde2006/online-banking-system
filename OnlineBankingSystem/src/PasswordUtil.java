import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

    public static String hashPassword(String password) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);

            byte[] hash = createHash(password, salt);

            return Base64.getEncoder().encodeToString(salt)
                    + ":"
                    + Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new RuntimeException("Could not protect password.", e);
        }
    }

    public static boolean verifyPassword(String password, String savedPassword) {
        try {
            String[] parts = savedPassword.split(":");

            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] savedHash = Base64.getDecoder().decode(parts[1]);
            byte[] enteredHash = createHash(password, salt);

            return MessageDigest.isEqual(savedHash, enteredHash);

        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] createHash(String password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                65536,
                256
        );

        SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        return factory.generateSecret(spec).getEncoded();
    }
}