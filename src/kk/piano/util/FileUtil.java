package kk.piano.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.stone.app.App;
import com.stone.log.Logger;

import kk.piano.R;

public class FileUtil {
	public static final char[] ILLEGAL_FILE_NAME_CHAR = {
		'/', '\0', '\\',
	};
	public static final int LEN_MIN = 1;
	public static final int LEN_MAX = 100;
	
	public static File DIR;
	
	public static void init(Context context) {
		if (DIR != null) {
			return;
		}
		
		DIR = new File(context.getFilesDir(), "opern");
		boolean needDelete = false;
		boolean needCreate = false;
		
		if (DIR.exists() == false) {
			needCreate = true;
		} else if (DIR.isDirectory() == false) {
			needDelete = true;
			needCreate = true;
		}
		
		if (needDelete) {
			DIR.delete();
		}
		
		if (needCreate) {
			try {
				DIR.mkdirs();
			} catch (Exception e) {
				Logger.print(null, e);
			}
		}
	}
	
	public static File getFile(String name) {
		return new File(DIR, name);
	}
	
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
	
	public static String writeString(OutputStream os, String str) {
		try {
			os.write(str.getBytes());
			return null;
		} catch (Exception e) {
			Logger.print(null, e);
		}
		
		return App.getInstance().getResources().getString(R.string.err_write_file_failed);
	}
	
	public static String readFile(String filename) {
		try {
			InputStream is = new FileInputStream(getFile(filename));
			return readString(is);
		} catch (Exception e) {
			Logger.print(null, e);
		}
		
		return null;
	}
	
	public static String writeFile(String filename, String content) {
		try {
			OutputStream os = new FileOutputStream(getFile(filename));
			return writeString(os, content);
		} catch (Exception e) {
			Logger.print(null, e);
		}
		
		return null;
	}
	
	public static List<String> getList() {
		File[] files = DIR.listFiles();
		List<String> list = new ArrayList<String>();
		
		for (File file : files) {
			list.add(file.getName());
		}
		
		return list;
	}
	
	public static String checkName(String name) {
		if (name == null || name.length() < LEN_MIN || name.length() > LEN_MAX) {
			return App.getInstance().getResources().getString(
					R.string.err_file_name_length_illegal,
					LEN_MIN, LEN_MAX);
		}
		
		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);
			
			for (char illegal : ILLEGAL_FILE_NAME_CHAR) {
				if (ch == illegal) {
					return App.getInstance().getResources().getString(
							R.string.err_file_name_illegal_char);
				}
			}
		}
		
		return null;
	}
	
	public static boolean exists(String name) {
		return getFile(name).exists();
	}
	
	public static String createFile(String name) {
		try {
			if (getFile(name).createNewFile()) {
				return null;
			}
		} catch (Exception e) {
			Logger.print(null, e);
		}
		
		return App.getInstance().getResources().getString(
				R.string.err_create_file_failed);
	}
	
	public static String deleteFile(String name) {
		try {
			if (getFile(name).delete()) {
				return null;
			}
		} catch (Exception e) {
			Logger.print(null, e);
		}
		
		return App.getInstance().getResources().getString(
				R.string.err_delete_file_failed);
	}
	
	public static String renameFile(String oldName, String newName) {
		try {
			File oldFile = getFile(oldName);
			File newFile = getFile(newName);
			
			if (oldFile.renameTo(newFile)) {
				return null;
			}
		} catch (Exception e) {
			Logger.print(null, e);
		}
		
		return App.getInstance().getResources().getString(
				R.string.err_rename_file_failed);
	}
}
