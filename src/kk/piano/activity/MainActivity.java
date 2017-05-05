package kk.piano.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;
import kk.piano.R;
import kk.piano.util.AppUtil;
import kk.piano.util.AudioUtil;
import kk.piano.util.FileUtil;
import kk.piano.util.Setting;
import kk.piano.util.ToneUtil;
import kk.piano.util.Const.PlayType;
import kk.piano.util.ToneUtil.ToneNode;
import kk.piano.view.IDialogClickListener;
import kk.piano.view.KeyScrollView;
import kk.piano.view.SimpleDialog;
import kk.piano.view.KeyScrollView.OnScrollListener;

public class MainActivity extends BaseActivity {
	private TextView mTvFile;
	private TextView mTvPlayType;
	
	private ImageView[] mIvKeys;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 音量
		SeekBar sbVolume = (SeekBar) findViewById(R.id.sb_volume);
		sbVolume.setProgress((int)(AudioUtil.getVolume() * 100));
		sbVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar sb) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar sb) {
			}
			
			@Override
			public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
				AudioUtil.setVolume(progress / 100f);
			}
		});
		
		// 文件
		mTvFile = (TextView) findViewById(R.id.tv_file);
		String filename = Setting.getString(Setting.KEY_FILE_NAME);
		setFile(filename);
		mTvFile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				
			}
		});
		
		// 播放类型
		mTvPlayType = (TextView) findViewById(R.id.tv_play_type);
		int type = Setting.getInt(Setting.KEY_PLAY_TYPE, PlayType.Free.ordinal());
		PlayType pt = PlayType.values()[type];
		setPlayType(pt);
		mTvPlayType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				SimpleDialog dialog = new SimpleDialog(MainActivity.this);
				dialog.setMessage(R.string.wd_select_play_type);
				dialog.setButtons(R.string.pt_free, R.string.pt_auto,
					R.string.pt_follow, R.string.pt_edit);
				dialog.setClickListener(new IDialogClickListener() {
					@Override
					public void onClick(Dialog dialog, int index, ClickType type) {
						PlayType[] values = PlayType.values();
						if (index >= 0 && index < values.length) {
							setPlayType(values[index]);
						}
						
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
		
		// 琴键
		mIvKeys = new ImageView[ToneUtil.size()];
		
		final KeyScrollView ksvKey = (KeyScrollView) findViewById(R.id.ksv_key);
		AppUtil.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ksvKey.scrollTo(Setting.getInt(Setting.KEY_SCROLL_X, 0), 0);
			}
		}, 100);
		ksvKey.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollChanged(int x, int y, int oldx, int oldy) {
				Setting.putInt(Setting.KEY_SCROLL_X, x);
			}
		});
		
		ViewGroup llWhite = (ViewGroup) ksvKey.findViewById(R.id.ll_white);
		ViewGroup llBlack = (ViewGroup) ksvKey.findViewById(R.id.ll_black);
		
		LayoutParams wp = new LayoutParams(AppUtil.getPixcel(
			Setting.getFloat(Setting.KEY_WIDTH, 60)),
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
		
		for (int i = 1; i <= mIvKeys.length; i++) {
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
			
			mIvKeys[i - 1] = view;
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
						AudioUtil.play(tn.file);
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
			mIvKeys[mIvKeys.length - 1].setLayoutParams(tp);
		} else {
			tv = new View(this);
			tp = new LayoutParams(0, LayoutParams.MATCH_PARENT);
			tp.weight = 1f;
			tv.setLayoutParams(tp);
			llBlack.addView(tv);
		}
	}
	
	private void setFile(String filename) {
		if (filename == null) {
			Setting.putString(Setting.KEY_FILE_NAME, null);
		} else if (FileUtil.readFile(filename) == null) {
			Setting.putString(Setting.KEY_FILE_NAME, null);
			App.showToast(R.string.err_file_reading_error);
		} else {
			Setting.putString(Setting.KEY_FILE_NAME, filename);
			mTvFile.setText(filename);
		}
	}
	
	private void setPlayType(PlayType pt) {
		Setting.putInt(Setting.KEY_PLAY_TYPE, pt.ordinal());
		
		if (pt == PlayType.Free) {
			mTvPlayType.setText(R.string.pt_free);
		} else if (pt == PlayType.Auto) {
			mTvPlayType.setText(R.string.pt_auto);
		} else if (pt == PlayType.Follow) {
			mTvPlayType.setText(R.string.pt_follow);
		} else if (pt == PlayType.Edit) {
			mTvPlayType.setText(R.string.pt_edit);
		}
	}
}
