package com.hotelnow.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES256Chiper {
    public static byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
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

        if(!TextUtils.isEmpty(str)) {
            byte[] clearText = null;
            try {
                byte[] keyData = secretKey.getBytes();
                SecretKey ks = new SecretKeySpec(keyData, "AES");
                Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.DECRYPT_MODE, ks);
                clearText = c.doFinal(Base64.decode(str, Base64.DEFAULT));
                return new String(clearText, "UTF-8");
            } catch (Exception e) {
                LogUtil.e("xxxx", e.toString()+"");
                return "";
            }
        }
        else{
            return "";
        }
    }

}
