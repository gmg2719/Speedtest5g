package cn.nokia.speedtest5g.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符判断是否有中文
 * @author zwq
 *
 */
public class UtilChinese {

	private static UtilChinese uXY;

	public static synchronized UtilChinese getInstance() {
		if (uXY == null) {
			uXY = new UtilChinese();
		}
		return uXY;
	}

	/**
	 * 判断是否有中文
	 * @param strName
	 * @return
	 */
	public boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}

	private boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}
	
	public boolean isIP(String addr){
      if(addr.length() < 7 || addr.length() > 15 || "".equals(addr)){
        return false;
      }
      /**
       * 判断IP格式和范围
       */
      String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
      
      Pattern pat = Pattern.compile(rexp);  
      Matcher mat = pat.matcher(addr);  
      boolean ipAddress = mat.find();
      return ipAddress;
    }
}
