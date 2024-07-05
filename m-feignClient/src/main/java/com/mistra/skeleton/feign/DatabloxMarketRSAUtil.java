package com.mistra.skeleton.feign;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import org.springframework.stereotype.Component;

/**
 * @ author: rui.wang@yamu.com
 * @ description:
 * @ date: 2024/4/19
 */
@Component
public class DatabloxMarketRSAUtil {

    private static DatabloxMarketProperties properties;

    public DatabloxMarketRSAUtil(DatabloxMarketProperties properties) {
        DatabloxMarketRSAUtil.properties = properties;
    }

    public static synchronized String encrypt(String text) throws Exception {
        PublicKey publicKey = getPublicKey(properties.getPublicKey());
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static synchronized String decrypt(String encryptedText) throws Exception {
        PrivateKey privateKey = getPrivateKey(properties.getPrivateKey());
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    private static PublicKey getPublicKey(String publicKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private static PrivateKey getPrivateKey(String privateKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }


    public static String encryptBlockData(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(properties.getPublicKey()));
        byte[] dataBytes = data.getBytes();
        int inputLen = dataBytes.length;
        StringBuilder encryptedData = new StringBuilder();
        int offset = 0;
        byte[] cache;
        while (inputLen - offset > 0) {
            if (inputLen - offset > 245) {
                cache = cipher.doFinal(dataBytes, offset, 245);
            } else {
                cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
            }
            encryptedData.append(Base64.getEncoder().encodeToString(cache));
            offset += 245;
        }
        return encryptedData.toString();
    }

    // 分块解密
    public static String decryptBlockData(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(properties.getPrivateKey()));
        int inputLen = encryptedData.length();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < inputLen; i = i + 344) {
            if (i + 344 > inputLen) {
                String substring = encryptedData.substring(i);
                byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(substring));
                stringBuilder.append(new String(bytes));
                break;
            } else {
                String substring = encryptedData.substring(i, i + 344);
                byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(substring));
                stringBuilder.append(new String(bytes));
            }
        }
        return stringBuilder.toString();
    }

}
