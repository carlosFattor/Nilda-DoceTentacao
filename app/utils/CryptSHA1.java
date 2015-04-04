package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CryptSHA1 {

	public static Optional<String> cipher(String value) {
        String algorithm = "SHA1";
        byte[] buffer = value.getBytes();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm);
            md.update(buffer);
            byte[] digest = md.digest();
            String hexValue = "";
            for (int i = 0; i < digest.length; i++) {
                int b = digest[i] & 0xff;
                if (Integer.toHexString(b).length() == 1) {
                    hexValue = hexValue.concat("0");
                }
                hexValue = hexValue + Integer.toHexString(b);
            }
            return Optional.ofNullable(hexValue);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CryptSHA1.class.getName()).log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }
}
