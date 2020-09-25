package com.android.volley.util;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Base64;

/**
 * 加解密算法
 */
public class EncryptionUtil {

    // 密钥 长度不得小于24
    private static final String secretKey = "IBADANBgkqhkiG9w0BAQEFAASCBKgkqhkiG9w0BAQEFAA" ;
    // 向量 可有可无 终端后台也要约定
    private static final String iv = "01234567" ;
    // 加解密统一使用的编码方式
    private static final String encoding = "UTF-8" ;

    /**
     * 3DES加密
     *
     * @param plainText
     *            普通文本
     * @return
     */
    @SuppressLint("TrulyRandom")
	public static String encode(String plainText){
        try {
            if (TextUtils.isEmpty(plainText)){
                return "";
            }
            Key deskey = null ;
            DESedeKeySpec spec = new DESedeKeySpec(secretKey .getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance( "desede");
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance( "desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec( iv.getBytes());
            cipher.init(Cipher. ENCRYPT_MODE , deskey, ips);
            byte [] encryptData = cipher.doFinal(plainText.getBytes(encoding ));
            return Base64.encodeToString(encryptData,Base64. DEFAULT );
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 3DES解密
     *
     * @param encryptText
     *            加密文本
     * @return
     */
    public static String decode(String encryptText){
        try {
            if (TextUtils.isEmpty(encryptText)){
                return "";
            }
            Key deskey = null ;
            DESedeKeySpec spec = new DESedeKeySpec( secretKey.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance( "desede" );
            deskey = keyfactory. generateSecret(spec);
            Cipher cipher = Cipher.getInstance( "desede/CBC/PKCS5Padding" );
            IvParameterSpec ips = new IvParameterSpec( iv.getBytes());
            cipher. init(Cipher. DECRYPT_MODE, deskey, ips);
            byte [] decryptData = cipher.doFinal(Base64. decode(encryptText, Base64. DEFAULT));
            return new String (decryptData, encoding);
        } catch (Exception e) {
            return "";
        }
    }
}
