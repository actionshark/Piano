package kk.piano.activity;

import com.stone.app.Res;
import com.stone.app.Setting;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import kk.piano.util.Const;

public abstract class BaseActivity extends Activity {
	protected View mStatusBar;

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			mStatusBar = new View(this);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					Res.getInstance().getStatusBarHeight());
			params.gravity = Gravity.TOP;
			mStatusBar.setLayoutParams(params);
			mStatusBar.setBackgroundColor(0xcccccc);

			ViewGroup decor = (ViewGroup) getWindow().getDecorView();
			decor.addView(mStatusBar);
		}
		
		if (Setting.getInstance().getBoolean(Const.KEY_SHOW_STATUS, false) == false) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			if (mStatusBar != null) {
				mStatusBar.setVisibility(View.GONE);
			}
		}
	}
}
