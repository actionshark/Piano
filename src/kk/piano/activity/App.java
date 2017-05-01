package kk.piano.activity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.widget.Toast;

import kk.piano.util.AppUtil;
import kk.piano.util.Setting;
import kk.piano.util.ToneUtil;

public class App extends Application {
	private static Toast sToast;

	@SuppressLint("ShowToast")
	@Override
	public void onCreate() {
		super.onCreate();

		sToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
		sToast.setText("");
		sToast.setDuration(Toast.LENGTH_SHORT);
		
		AppUtil.init(this);
		Setting.init(this);
		ToneUtil.init(this);
	}

	public static void showToast(int resId, Object... args) {
		sToast.setText(AppUtil.getString(resId, args));
		sToast.show();
	}

	public static void showToast(String text, Object... args) {
		sToast.setText(String.format(text, args));
		sToast.show();
	}
}
