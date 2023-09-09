package de.nikogenia.nnmaster.utils;

import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class AESUtils {

    public static String generate() {

        return RandomStringUtils.randomAlphanumeric(16);

    }

    public static String encrypt(String message, String key) {

        try {

            byte[] initVector = new byte[16];
            (new Random()).nextBytes(initVector);
            IvParameterSpec iv = new IvParameterSpec(initVector);

            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            byte[] cipherBytes = cipher.doFinal(message.getBytes());

            byte[] messageBytes = new byte[initVector.length + cipherBytes.length];

            System.arraycopy(initVector, 0, messageBytes, 0, 16);
            System.arraycopy(cipherBytes, 0, messageBytes, 16, cipherBytes.length);

            return encode(messageBytes);

        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException |
                 IllegalBlockSizeException | UnsupportedEncodingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String decrypt(String message, String key) {

        try {

            byte[] cipherBytes = decode(message);

            byte[] initVector = Arrays.copyOfRange(cipherBytes,0,16);

            byte[] messageBytes = Arrays.copyOfRange(cipherBytes,16, cipherBytes.length);

            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

            byte[] byteArray = cipher.doFinal(messageBytes);

            return new String(byteArray, StandardCharsets.UTF_8);

        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException |
                 IllegalBlockSizeException | UnsupportedEncodingException | InvalidAlgorithmParameterException e) {
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
