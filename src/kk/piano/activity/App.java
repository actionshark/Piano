package kk.piano.activity;

import com.stone.app.Setting;

import android.annotation.SuppressLint;
import android.app.Application;
import android.widget.Toast;

import kk.piano.util.AudioUtil;
import kk.piano.util.FileUtil;
import kk.piano.util.NoteUtil;

public class App extends Application {
	private static Toast sToast;

	@SuppressLint("ShowToast")
	@Override
	public void onCreate() {
		super.onCreate();

		sToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
		sToast.setText("");
		sToast.setDuration(Toast.LENGTH_SHORT);
		
		com.stone.app.App.getInstance().onApplicationCreate(this);
		Setting.setInstance("setting");
		
		AudioUtil.init(this);
		NoteUtil.init(this);
		FileUtil.init(this);
	}

	public static void showToast(int resId, Object... args) {
		sToast.setText(com.stone.app.App.getInstance().getResources().getString(resId, args));
		sToast.show();
	}

	public static void showToast(String text, Object... args) {
		sToast.setText(String.format(text, args));
		sToast.show();
	}
}
