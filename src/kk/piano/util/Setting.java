package kk.piano.util;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Setting {
	public static final Locale LOCALE = Locale.ENGLISH;
	
	public static final String KEY_FILE_NAME = "key_file_name";
	public static final String KEY_KEY_SCROLL_X = "key_key_scroll_x";
	public static final String KEY_PLAY_INTER = "key_play_inter";
	public static final String KEY_PLAY_TYPE = "key_play_type";
	public static final String KEY_SHOW_HINT = "key_show_hint";
	public static final String KEY_SHOW_STATUS = "show_status";
	public static final String KEY_VOLUME = "key_volume";
	public static final String KEY_WIDTH = "key_width";

	private static SharedPreferences sPrefer;

	public static void init(Context context) {
		if (sPrefer != null) {
			return;
		}

		sPrefer = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
	}
	
	public static boolean putBoolean(String key, boolean value) {
		Editor editor = sPrefer.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}
	
	public static boolean putInt(String key, int value) {
		Editor editor = sPrefer.edit();
		editor.putInt(key, value);
		return editor.commit();
	}
	
	public static boolean putLong(String key, long value) {
		Editor editor = sPrefer.edit();
		editor.putLong(key, value);
		return editor.commit();
	}
	
	public static boolean putFloat(String key, float value) {
		Editor editor = sPrefer.edit();
		editor.putFloat(key, value);
		return editor.commit();
	}
	
	public static boolean putString(String key, String value) {
		Editor editor = sPrefer.edit();
		editor.putString(key, value);
		return editor.commit();
	}
	
	public static boolean getBoolean(String key, boolean def) {
		return sPrefer.getBoolean(key, def);
	}
	
	public static boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	public static int getInt(String key, int def) {
		return sPrefer.getInt(key, def);
	}
	
	public static int getInt(String key) {
		return getInt(key, 0);
	}
	
	public static long getLong(String key, long def) {
		return sPrefer.getLong(key, def);
	}
	
	public static long getLong(String key) {
		return getLong(key, 0L);
	}
	
	public static float getFloat(String key, float def) {
		return sPrefer.getFloat(key, def);
	}
	
	public static float getFloat(String key) {
		return getFloat(key, 0f);
	}
	
	public static String getString(String key, String def) {
		return sPrefer.getString(key, def);
	}
	
	public static String getString(String key) {
		return getString(key, null);
	}
}
