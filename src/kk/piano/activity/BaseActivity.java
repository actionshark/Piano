package kk.piano.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;

import kk.piano.R;
import kk.piano.util.AppUtil;
import kk.piano.util.Setting;

public abstract class BaseActivity extends Activity {
	protected View mStatusBar;

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			mStatusBar = new View(this);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, AppUtil
				.getStatusBarHeight());
			params.gravity = Gravity.TOP;
			mStatusBar.setLayoutParams(params);
			mStatusBar.setBackgroundColor(AppUtil.getColor(R.color.title_bg));

			ViewGroup decor = (ViewGroup) getWindow().getDecorView();
			decor.addView(mStatusBar);
		}
		
		if (Setting.getShowStatus() == false) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			if (mStatusBar != null) {
				mStatusBar.setVisibility(View.GONE);
			}
		}
	}
}
