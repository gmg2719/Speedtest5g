package cn.nokia.speedtest5g.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;

/**
 * 自定义base64加密解密方法
 * @author zwq
 *
 */
public class Base64Utils {
	
	private static char[] base64EncodeChars = new char[] { 'A', 'B', 'C', 'D',
			'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
			'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
			'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', '+', '/' };
	private static byte[] base64DecodeChars = new byte[] { -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59,
			60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
			10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1,
			-1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
			38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1,
			-1, -1 };

	public static String encode(byte[] data) {
		StringBuffer sb = new StringBuffer();
		int len = data.length;
		int i = 0;
		int b1, b2, b3;
		while (i < len) {
			b1 = data[i++] & 0xff;
			if (i == len) {
				sb.append(base64EncodeChars[b1 >>> 2]);
				sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
				sb.append("==");
				break;
			}
			b2 = data[i++] & 0xff;
			if (i == len) {
				sb.append(base64EncodeChars[b1 >>> 2]);
				sb.append(base64EncodeChars[((b1 & 0x03) << 4)
						| ((b2 & 0xf0) >>> 4)]);
				sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
				sb.append("=");
				break;
			}
			b3 = data[i++] & 0xff;
			sb.append(base64EncodeChars[b1 >>> 2]);
			sb.append(base64EncodeChars[((b1 & 0x03) << 4)
					| ((b2 & 0xf0) >>> 4)]);
			sb.append(base64EncodeChars[((b2 & 0x0f) << 2)
					| ((b3 & 0xc0) >>> 6)]);
			sb.append(base64EncodeChars[b3 & 0x3f]);
		}
		return sb.toString();
	}

	public static byte[] decode(String str) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		byte[] data = str.getBytes("US-ASCII");
		int len = data.length;
		int i = 0;
		int b1, b2, b3, b4;
		while (i < len) {

			do {
				b1 = base64DecodeChars[data[i++]];
			} while (i < len && b1 == -1);
			if (b1 == -1)
				break;

			do {
				b2 = base64DecodeChars[data[i++]];
			} while (i < len && b2 == -1);
			if (b2 == -1)
				break;
			sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));

			do {
				b3 = data[i++];
				if (b3 == 61)
					return sb.toString().getBytes("iso8859-1");
				b3 = base64DecodeChars[b3];
			} while (i < len && b3 == -1);
			if (b3 == -1)
				break;
			sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));

			do {
				b4 = data[i++];
				if (b4 == 61)
					return sb.toString().getBytes("iso8859-1");
				b4 = base64DecodeChars[b4];
			} while (i < len && b4 == -1);
			if (b4 == -1)
				break;
			sb.append((char) (((b3 & 0x03) << 6) | b4));
		}
		return sb.toString().getBytes("iso8859-1");
	}
	
    // SecretKey 负责保存对称密钥  
    private static SecretKey mDeskey;  
    // Cipher负责完成加密或解密工作  
    private static Cipher mCipher;  
    
    /**
     * 对字符串进行加密
     * ---先DES3后转base64
     * @param content
     * @return
     */
    @SuppressLint("TrulyRandom")
	public static String encrytorDes3(String content){
    	if (TextUtils.isEmpty(content)) {
			return "";
		}
    	try {
    		initKey();
    		// 根据密钥，对Cipher对象进行初始化，ENCRYPT_MODE表示加密模式  
    		mCipher.init(Cipher.ENCRYPT_MODE, mDeskey);  
	        // 加密，结果保存进cipherByte  
	        return encode(mCipher.doFinal(content.getBytes()));
	      //根据密钥进行加密
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return "";
    }
    
    /**
     * 对字符串进行解密
     * ---先反base64后解DES3
     * @param content
     * @return
     */
    public static String decryptorDes3(String content){
    	if (TextUtils.isEmpty(content)) {
			return "";
		}
    	try {
    		initKey();
    		 // 根据密钥，对Cipher对象进行初始化，DECRYPT_MODE表示加密模式  
    		mCipher.init(Cipher.DECRYPT_MODE, mDeskey);  
	        return new String(mCipher.doFinal(decode(content)));  
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return "";
    }  
    
    //初始化需要使用的加解密对象
    private static void initKey() throws Exception{
    	//获取SecretKey 负责保存对称密钥 
    	if (mDeskey == null) {
    		mDeskey = getSecretKey(decode(SpeedTest5g.getContext().getString(R.string.secretKeyHs)));
		}
		// 生成Cipher对象,指定其支持的DES算法  
    	if (mCipher == null) {
    		mCipher = Cipher.getInstance("DESede");
		}
    }
    
    /**
     * 根据密钥字节获取对应的密钥
     * @param keyBytes
     * @return
     * @throws Exception
     */
    private static SecretKey getSecretKey(byte[] keyBytes)throws Exception{
    	SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");  
        KeySpec keySpec = new DESedeKeySpec(keyBytes);
        return keyFactory.generateSecret(keySpec);  
    }

}
