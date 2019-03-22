package com.hotelnow.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.net.MalformedURLException;
import java.net.URL;
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
        str = str.replace("-","+").replace("_","/");
        try {
            byte[] keyData = secretKey.getBytes();
            SecretKey ks = new SecretKeySpec(keyData, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, ks);
            encryptedText = c.doFinal(str.getBytes("UTF-8"));
            return Base64.encodeToString(encryptedText, Base64.URL_SAFE);
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
                clearText = c.doFinal(Base64.decode(str, Base64.URL_SAFE));
                return new String(clearText, "UTF-8").replace("+","-").replace("/","_");
            } catch (Exception e) {
                LogUtil.e("xxxx", e.toString() + "");
                return "";
            }
        } else {
            return "";
        }
    }

}
