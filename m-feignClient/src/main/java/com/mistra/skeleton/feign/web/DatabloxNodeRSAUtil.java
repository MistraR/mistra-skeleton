package com.mistra.skeleton.feign.web;

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
public class DatabloxNodeRSAUtil {

    public static synchronized String encrypt(String text, String nodePublicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(nodePublicKey));
        byte[] dataBytes = text.getBytes();
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
    public static synchronized String decrypt(String encryptedData, String nodePrivateKey) throws Exception {
        System.out.println(encryptedData);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(nodePrivateKey));
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

    public static void main(String[] args) throws Exception {
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDSKZv4FwADq03bJpi7BD30QPaBf/rZHFch+EG7M3+Z/cVFEzwG1az" +
                "/KMmbVXVqE8mWH8blDCK6uQkQHaBCIZLt3Zpy8RdPIHETQo8ZeGGaM4/hVJFeTXJWrjGDyTiNJYGEa17Vls39ztQMRdhfPfiI5Q7ChyX+xeA/1lLft7QX/uBQyIL0zTXW8" +
                "/5feYniDNo1HH2WpiYTmi0+xJAMluCAP3AM64GKNfRvoBgJR4DYqtnIgbVG8G2V1afyekvbnKl/yLkKs+71Gx0lP44N+uEtlW3KXHWLW0HjEXN" +
                "++QO2kfligEQUF50RAunYqtXQ4LClhoRsynCHepQyxUv83odnAgMBAAECggEAIdJmcmepLe38qp8shzRokA2AXljtUtF3yFS" +
                "+p1TjXTH09GfjCiqLG8lBoMkBC2BB2fIlPSlV0X0D018hMmNa1QML3yGjoa1T5UiZ5Px1oK5ZombMukN2ejKwb0gL3NNDY78v1OnfHtCRWaRH7RP5EnbiiDXdzIGl1EA2uCBFUHL+ajzAOtDjW0bPPOYzSp6snbDhUpkmBmcwcy3R/sB+N1RKr3sm+1G6v+KmA9D3qRrGO15HjRT2OAybjNxGFCSaHEsj06pC03oOAbj+boaU5tgFF5x3yyC6YsjmKv81G/UaQCgiWau97gb3YvCYyRB1AfBckh7zkucAl+Fd/JS9eQKBgQD4maKaN2pTLttsEhSQyxYOr45vVlm718pkwS2e2DXaF5Mpfg3QuX0cIFA79IpywsUt8bm4hgjoYeUMH8l6GbB3sqsqlrdyfoxfb73UWBO99MtIkEAJRTf8ky3z2DWk1ItrFLaQWgat+7o5znTgrRHIUJ2jBtvp3yHdi3ZKCQ8kGwKBgQDYaxMcbRD8huMvKDLYl+xzBBlByz76LeX+8t3pAs2YiE/MOy3TnECZKaG1VMTulA3VcDGrv4Im4WTbxOiGma/Y8V9IbqTsjxmK6ZhX7dmn9O0ZXeYqHhNokTkJDzjYlIsT3jkZgV0iZoBoJcjWAblaHFJtp/bJNz91ULrzJcTmpQKBgQDYWIvjfdpwLdUTBeET6Ul22l0wk4tgJ/mhGWTkWfDzrZSKhYLsZKB2e8CXjMd3+/yxcS5ZYXwbECbPGFK0ierDIKBJXgSkBXGQFKS1STlVFNZcffzclHqWuldBYKW+nqH+PHZsxYwOmYWFjSC17qgNFes511xG+cJ1FUuKPrwIhQKBgAOXw3l1VhLbKmpeAHoYC5TdPI4bEbCVknvpDEYSBdfCa5C4aspYjbn9NvILtjk/u/sPIcP4KfD7TPFZE4MoigLP/wT00UgcGBB2UFElYbeoGUeSh3+rgCb8QjYkSo1N6gEqb0g9HY60pO0Vs3/aB3m3fqbTYwV0JVmNPY1L0zkBAoGBAIOLctNFSijAGq6kB17sQC8OFM3QUxq4xXP+RrrpiaMyQQT7aMlutNIwPxf4sfdA2qN/sDNi000xlQ1qaZ5a4/oqW4UvhzwKd2pKcdR0sehRpqbf1sraWMHwmfqlhc/IEIXULwhy/xXj+aVJWrKsNOw9myqBkjNqLM74RoFEnVp2";
        String data = "{\"bid\":\"did:bid:ef5xowFWH7ZkiERMV5ugC1zRSCoFxwzX\",\"companyId\":\"77f67ac9-b6a4-4c12-b156-f2d8be79d6b4\"," +
                "\"dataAssetId\":\"a22ab1b0-94ff-47b4-b4d4-5c67db6302ef\",\"databloxTimestamp\":1717061737084}";
        String publicRW = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0imb+BcAA6tN2yaYuwQ99ED2gX/62RxXIfhBuzN/mf3FRRM8BtWs/yjJm1V1ahPJlh" +
                "/G5QwiurkJEB2gQiGS7d2acvEXTyBxE0KPGXhhmjOP4VSRXk1yVq4xg8k4jSWBhGte1ZbN/c7UDEXYXz34iOUOwocl/sXgP9ZS37e0F/7gUMiC9M011vP" +
                "+X3mJ4gzaNRx9lqYmE5otPsSQDJbggD9wDOuBijX0b6AYCUeA2KrZyIG1RvBtldWn8npL25ypf8i5CrPu9RsdJT" +
                "+ODfrhLZVtylx1i1tB4xFzfvkDtpH5YoBEFBedEQLp2KrV0OCwpYaEbMpwh3qUMsVL/N6HZwIDAQAB";
        String encrypt = encrypt(data, publicRW);
        System.out.println(encrypt);
        String dataa = "sm/NSWfHM7f1W3QdLBQo7o2Y+nB6hFdCJnz5dKbaoI8S8aZePLqcRIfvAiiPo2GBKzwif/Q01JFmqN8fPq5rf+KAjf7ngYx5LF5" +
                "/jwCnlOHhJMQW7x7ZeEbC4F0pXMzbJ0InF8PVe+VE8tv8QvdEO+PcizgimwFt7cVODDdnW1" +
                "/Ne44uJYu04XSunBDtRy43HWoLkgern2WLAOl2xN20Uj94h9DSSWSOZKCaisEkUor0vI1Qg3kTev5bME0AqlfvtozjpIKrZ5zDdccVXN6UqXbzFOQK17wAN7s3R5NoRAOe2lFqqWCxTag8nHpnKUgMfVj0MFcU2Jl3ENVkIE16TQ==";
        String decrypt = decrypt(dataa, privateKey);
        System.out.println(decrypt);
    }
}
