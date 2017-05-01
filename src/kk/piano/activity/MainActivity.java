package kk.piano.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import kk.piano.R;
import kk.piano.util.AppUtil;
import kk.piano.util.AudioUtil;
import kk.piano.util.Logger;
import kk.piano.util.Setting;
import kk.piano.util.ToneUtil;
import kk.piano.util.ToneUtil.ToneNode;

public class MainActivity extends BaseActivity {
	private ImageView[] mKeys;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mKeys = new ImageView[ToneUtil.size()];
		
		ViewGroup llWhite = (ViewGroup) findViewById(R.id.ll_white);
		ViewGroup llBlack = (ViewGroup) findViewById(R.id.ll_black);
		
		LayoutParams wp = new LayoutParams(AppUtil.getPixcel(
			Setting.getFloat(Setting.KEY_KEY_WIDTH, 60)),
			LayoutParams.MATCH_PARENT);
		wp.topMargin = 1;
		wp.bottomMargin = 1;
		wp.leftMargin = 1;
		wp.rightMargin = 1;
		
		LayoutParams bp = new LayoutParams(0, LayoutParams.MATCH_PARENT);
		bp.weight = 2f;
		
		boolean preBlack = true;
		
		View tv = new View(this);
		LayoutParams tp = new LayoutParams(0, LayoutParams.MATCH_PARENT);
		tp.weight = 1f;
		tv.setLayoutParams(tp);
		llBlack.addView(tv);
		
		for (int i = 1; i <= mKeys.length; i++) {
			ToneNode tn = ToneUtil.getNodeById(i);
			ImageView view = new ImageView(this);
			view.setScaleType(ScaleType.FIT_XY);
			
			if (tn.isWhite) {
				view.setLayoutParams(wp);
				view.setImageResource(R.drawable.white_key_nor);
				llWhite.addView(view);
				
				tv = new View(this);
				tp = new LayoutParams(0, LayoutParams.MATCH_PARENT);
				tp.weight = preBlack ? 1f : 3f;
				tv.setLayoutParams(tp);
				llBlack.addView(tv);
				preBlack = false;
			} else {
				view.setLayoutParams(bp);
				view.setImageResource(R.drawable.black_key_nor);
				llBlack.addView(view);
				preBlack = true;
			}
			
			mKeys[i - 1] = view;
			view.setTag(tn);
			view.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent event) {
					ImageView iv = (ImageView) view;
					ToneNode tn = (ToneNode) view.getTag();
					int action = event.getActionMasked();
					
					if (action == MotionEvent.ACTION_DOWN) {
						iv.setImageResource(tn.isWhite ? R.drawable.white_key_pre :
							R.drawable.black_key_pre);
						AudioUtil.play(MainActivity.this, tn.file);
					} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
						iv.setImageResource(tn.isWhite ? R.drawable.white_key_nor :
							R.drawable.black_key_nor);
					}
					
					return true;
				}
			});
		}
		
		if (preBlack) {
			tp = new LayoutParams(0, LayoutParams.MATCH_PARENT);
			tp.weight = 1f;
			mKeys[mKeys.length - 1].setLayoutParams(tp);
		} else {
			tv = new View(this);
			tp = new LayoutParams(0, LayoutParams.MATCH_PARENT);
			tp.weight = 1f;
			tv.setLayoutParams(tp);
			llBlack.addView(tv);
		}
	}
}
