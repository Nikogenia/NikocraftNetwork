package de.nikogenia.nnproxy.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtils {

    public static KeyPair generate() {

        try {

            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

            generator.initialize(2048);

            return generator.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
        }

    }

    public static String exportKey(PublicKey key) {
        return encode(key.getEncoded());
    }

    public static PublicKey importKey(String key) {

        try {

            KeyFactory generator = KeyFactory.getInstance("RSA");

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decode(key));

            return generator.generatePublic(keySpec);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String encrypt(String message, PublicKey key) {

        try {

            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");

            cipher.init(Cipher.ENCRYPT_MODE, key);

            return encode(cipher.doFinal(message.getBytes(StandardCharsets.UTF_8)));

        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String decrypt(String message, PrivateKey key) {

        try {

            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");

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
