package com.hotelnow.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES256Chiper {
    public static String secretKey = "ABCDEF0123456124";

    //AES256 암호화
    public static String AES_Encode(String str) {

        byte[] encryptedText = null;
        try {
            byte[] keyData = secretKey.getBytes();
            SecretKey ks = new SecretKeySpec(keyData, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, ks);
            encryptedText = c.doFinal(str.getBytes("UTF-8"));
            return Base64.encodeToString(encryptedText, Base64.DEFAULT);
        } catch (Exception e) {
            LogUtil.e("xxxx", e.toString());
            return "";
        }
    }

    //AES256 복호화
    public static String AES_Decode(String str) {

        if (!TextUtils.isEmpty(str)) {
            byte[] clearText = null;
            try {
                byte[] keyData = secretKey.getBytes();
                SecretKey ks = new SecretKeySpec(keyData, "AES");
                Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.DECRYPT_MODE, ks);
                clearText = c.doFinal(Base64.decode(str, Base64.DEFAULT));
                return new String(clearText, "UTF-8");
            } catch (Exception e) {
                LogUtil.e("xxxx", e.toString() + "");
                return "";
            }
        } else {
            return "";
        }
    }

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    /**
     * @description byte 배열을 16진수로 변환한다.
     */
    private static String toHexString(byte[] bytes) {

        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();

    }

    /**
     * @description byte 배열을 Base64로 인코딩한다.
     */
    public static String toBase64String(byte[] bytes){

        String byteArray = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return new String(byteArray);

    }

    /**
     * @description HmacSHA1로 암호화한다. (HmacSHA1은 hash algorism이라서 복호화는 불가능)
     */
    public static String encryption(String data, String key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {

        byte[] key2 = Base64.decode(key.replace('-', '+').replace('_', '/'), Base64.DEFAULT);
        SecretKeySpec signingKey = new SecretKeySpec(key2, HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);

        return toBase64String(mac.doFinal(data.getBytes()));

    }

}
