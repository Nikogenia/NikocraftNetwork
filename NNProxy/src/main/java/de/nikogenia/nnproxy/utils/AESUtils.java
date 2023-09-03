package de.nikogenia.nnproxy.utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

public class AESUtils {

    public static SecretKey generate() {

        byte[] key = new byte[32];

        new Random().nextBytes(key);

        return new SecretKeySpec(key, "AES");

    }

    public static String exportKey(SecretKey key) {
        return encode(key.getEncoded());
    }

    public static SecretKey importKey(String key) {
        return new SecretKeySpec(decode(key), "AES");
    }

    public static String encrypt(String message, SecretKey key) {

        try {

            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, key);

            return encode(cipher.doFinal(message.getBytes(StandardCharsets.UTF_8)));

        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String decrypt(String message, SecretKey key) {

        try {

            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, key);

            return new String(cipher.doFinal(decode(message)), StandardCharsets.UTF_8);

        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return null;

    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

}
