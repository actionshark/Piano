package kk.piano.util;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Setting {
	public static final Locale LOCALE = Locale.ENGLISH;
	
	public static final String KEY_SHOW_STATUS = "show_status";

	private static SharedPreferences sPrefer;

	public static void init(Context context) {
		if (sPrefer != null) {
			return;
		}

		sPrefer = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
	}
	
	public static void setShowStatus(boolean show) {
		Editor editor = sPrefer.edit();
		editor.putBoolean(KEY_SHOW_STATUS, show);
		editor.commit();
	}

	public static boolean getShowStatus() {
		return sPrefer.getBoolean(KEY_SHOW_STATUS, false);
	}
}
