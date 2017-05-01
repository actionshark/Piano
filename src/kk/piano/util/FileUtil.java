package kk.piano.util;

import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
	public static String readString(InputStream is) {
		try {
			byte[] buf = new byte[1024 * 1024];
			int len;
			StringBuilder sb = new StringBuilder();
	
			while ((len = is.read(buf)) > 0) {
				sb.append(new String(buf, 0, len));
			}
	
			return sb.toString();
		} catch (Exception e) {
			Logger.print(null, e);
		}
		
		return null;
	}
	
	public static boolean writeString(OutputStream os, String str) {
		try {
			os.write(str.getBytes());
			return true;
		} catch (Exception e) {
			Logger.print(null, e);
		}
		
		return false;
	}
}
