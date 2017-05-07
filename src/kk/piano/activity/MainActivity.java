package kk.piano.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.List;

import kk.piano.R;
import kk.piano.util.AppUtil;
import kk.piano.util.AudioUtil;
import kk.piano.util.FileUtil;
import kk.piano.util.Setting;
import kk.piano.util.NoteUtil;
import kk.piano.util.Const.PlayType;
import kk.piano.util.NoteUtil.Note;
import kk.piano.view.FileDialog;
import kk.piano.view.IDialogClickListener;
import kk.piano.view.IOnFileSelectListener;
import kk.piano.view.KeyScrollView;
import kk.piano.view.SimpleDialog;
import kk.piano.view.KeyScrollView.OnScrollListener;

public class MainActivity extends BaseActivity {
	public static enum KeyStatus {
		Normal, Pressed, Highlight,
	}
	
	private TextView mTvFileName;
	private String mFileName;
	
	private ImageView mIvButton;
	private ImageView mIvButton2;
	private boolean mIsPause = true;
	
	private TextView mTvPlayType;
	private PlayType mPlayType;
	
	private static class NoteGrid {
		public ImageView top;
		public TextView center;
		public ImageView bottom;
	}
	private NoteGrid[] mNgNoteGrids = new NoteGrid[15];
	private int mNgCursor = mNgNoteGrids.length / 2;
	private Runnable mNgRunnable;
	
	private List<Note> mNoteList = new ArrayList<Note>();
	private int mNoteIndex = -1;
	
	private ImageView[] mIvKeys;
	
	private long mPlayInter;
	private long mLastPlayPoint;
	private Runnable mPlayRunnable = new Runnable() {
		@Override
		public void run() {
			if (mIsPause) {
				return;
			}
			
			long now = System.currentTimeMillis();
			if (now - mLastPlayPoint < mPlayInter) {
				return;
			}
			
			if (mNoteIndex >= mNoteList.size() - 1) {
				mIsPause = true;
				
				if (mPlayType == PlayType.Auto) {
					mIvButton.setImageResource(R.drawable.triangle_right);
					setKeyImage(-1, KeyStatus.Normal);
				}
				
				return;
			}
			
			mLastPlayPoint = now;
			
			mNoteIndex++;
			Note note = mNoteList.get(mNoteIndex);
			
			AudioUtil.play(note.file);
			
			displayNotes();
			
			setKeyImage(note.id - 1, KeyStatus.Highlight);
		}
	};
	
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
		
		SeekBar sbRate = (SeekBar) findViewById(R.id.sb_rate);
		mPlayInter = NoteUtil.getInter();
		int progress = NoteUtil.inter2Progress(mPlayInter);
		sbRate.setProgress(100 - progress);
		sbRate.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar sb) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar sb) {
			}
			
			@Override
			public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
				mPlayInter = NoteUtil.progress2Inter(100 - progress);
				NoteUtil.setInter(mPlayInter);
			}
		});
		
		// 文件
		mTvFileName = (TextView) findViewById(R.id.tv_file);
		mTvFileName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				FileDialog dialog = new FileDialog(MainActivity.this);
				dialog.setFileListener(new IOnFileSelectListener() {
					@Override
					public void onSelect(final String name) {
						SimpleDialog dialog = new SimpleDialog(MainActivity.this);
						dialog.setMessage(R.string.mg_keep_old_or_read_new);
						dialog.setButtons(R.string.wd_old, R.string.wd_new);
						dialog.setClickListener(new IDialogClickListener() {
							@Override
							public void onClick(Dialog dialog, int index, ClickType type) {
								if (index == 0) {
									setFileName(name);
								} else {
									mIsPause = true;
									if (mPlayType == PlayType.Auto) {
										mIvButton.setImageResource(R.drawable.triangle_right);
									}
									
									setFileName(name);
									
									String content = FileUtil.readFile(name);
									mNoteList = NoteUtil.toNotes(content);
									
									if (mPlayType == PlayType.Follow) {
										mNoteIndex = 0;

										while (mNoteIndex < mNoteList.size()) {
											Note note = mNoteList.get(mNoteIndex);
											if (note.id > 0) {
												break;
											}
											
											mNoteIndex++;
										}
										
										if (mNoteIndex < mNoteList.size()) {
											Note note = mNoteList.get(mNoteIndex);
											setKeyImage(note.id - 1, KeyStatus.Highlight);
										} else {
											mNoteIndex = 0;
											setKeyImage(-1, KeyStatus.Normal);
										}
									} else {
										mNoteIndex = -1;
										setKeyImage(-1, KeyStatus.Normal);
									}
									
									displayNotes();
								}
								
								dialog.dismiss();
							}
						});
						dialog.show();
					}
					
					@Override
					public void onChange() {
					}
				});
				dialog.show();
			}
		});
		
		mIvButton = (ImageView) findViewById(R.id.iv_btn);
		mIvButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mPlayType == PlayType.Free) {

				} else if (mPlayType == PlayType.Auto) {
					if (mIsPause) {
						mIsPause = false;
						mIvButton.setImageResource(R.drawable.pause);
						
						if (mNoteIndex >= mNoteList.size() - 1) {
							mNoteIndex = -1;
						}
						displayNotes();
					} else {
						mIsPause = true;
						mIvButton.setImageResource(R.drawable.triangle_right);
						setKeyImage(-1, KeyStatus.Normal);
					}
				} else if (mPlayType == PlayType.Follow) {

				} else if (mPlayType == PlayType.Edit) {
					if (mFileName == null) {
						App.showToast(R.string.mg_select_file_hint);
						return;
					}
					
					SimpleDialog dialog = new SimpleDialog(MainActivity.this);
					dialog.setMessage(R.string.mg_save_hint);
					dialog.setButtons(R.string.wd_cancel, R.string.wd_ok);
					dialog.setClickListener(new IDialogClickListener() {
						@Override
						public void onClick(Dialog dialog, int index, ClickType type) {
							if (index == 1) {
								String content = NoteUtil.toString(mNoteList);
								String err = FileUtil.writeFile(mFileName, content);
								
								if (err == null) {
									App.showToast(R.string.err_write_file_success);
								} else {
									App.showToast(err);
								}
							}
							
							dialog.dismiss();
						}
					});
					dialog.show();
				}
			}
		});
		
		mIvButton2 = (ImageView) findViewById(R.id.iv_btn_2);
		mIvButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mPlayType == PlayType.Edit) {
					int index = mNoteIndex + 1;
					if (index > mNoteList.size()) {
						index = mNoteList.size();
					}
					
					mNoteList.add(index, NoteUtil.getNodeById(0));
					mNoteIndex = index;
					displayNotes();
				}
			}
		});
		
		// 播放类型
		mTvPlayType = (TextView) findViewById(R.id.tv_play_type);
		mTvPlayType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				SimpleDialog dialog = new SimpleDialog(MainActivity.this);
				dialog.setMessage(R.string.mg_select_play_type);
				dialog.setButtons(R.string.pt_free, R.string.pt_auto,
					R.string.pt_follow, R.string.pt_edit);
				dialog.setClickListener(new IDialogClickListener() {
					@Override
					public void onClick(Dialog dialog, int index, ClickType type) {
						mIsPause = true;
						
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
		
		// 乐谱
		final ImageView ivLeft = (ImageView) findViewById(R.id.iv_left);
		ivLeft.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getActionMasked();
				
				if (action == MotionEvent.ACTION_DOWN) {
					ivLeft.setBackgroundResource(R.drawable.bg_bar_pre);
					
					moveLeft();
					
					mNgRunnable = AppUtil.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							moveLeft();
						}
					}, 500, 50);
				} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
					ivLeft.setBackgroundResource(R.drawable.bg_bar_nor);
					AppUtil.removeUiThread(mNgRunnable);
				}
				
				return true;
			}
		});
		

		final ImageView ivRight = (ImageView) findViewById(R.id.iv_right);
		ivRight.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getActionMasked();
				
				if (action == MotionEvent.ACTION_DOWN) {
					ivRight.setBackgroundResource(R.drawable.bg_bar_pre);
					
					moveRight();
					
					mNgRunnable = AppUtil.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							moveRight();
						}
					}, 500, 50);
				} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
					ivRight.setBackgroundResource(R.drawable.bg_bar_nor);
					AppUtil.removeUiThread(mNgRunnable);
				}
				
				return true;
			}
		});
		
		ViewGroup vgOpern = (ViewGroup) findViewById(R.id.ll_opern);
		LayoutInflater li = getLayoutInflater();
		
		LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);
		lp.leftMargin = lp.rightMargin = AppUtil.getPixcel(1);
		
		for (int i = 0; i < mNgNoteGrids.length; i++) {
			View view = li.inflate(R.layout.grid_note, null);
			view.setLayoutParams(lp);
			vgOpern.addView(view);
			
			if (i == mNgCursor) {
				view.setBackgroundColor(0xaaaaaa);
			}
			
			NoteGrid ng = new NoteGrid();
			ng.top = (ImageView) view.findViewById(R.id.iv_top);
			ng.center = (TextView) view.findViewById(R.id.tv_center);
			ng.bottom = (ImageView) view.findViewById(R.id.iv_bottom);
			mNgNoteGrids[i] = ng;
			
			final int idx = i;
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mPlayType == PlayType.Edit) {
						int index = mNoteIndex - mNgCursor + idx;
						if (index >= 0 && index < mNoteList.size()) {
							mNoteList.remove(index);
							
							if (mNoteIndex >= mNoteList.size()) {
								mNoteIndex = mNoteList.size() - 1;
							}
							
							displayNotes();
						}
					}
				}
			});
		}

		// 琴键
		mIvKeys = new ImageView[NoteUtil.size()];
		
		final KeyScrollView ksvKey = (KeyScrollView) findViewById(R.id.ksv_key);
		AppUtil.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ksvKey.scrollTo(Setting.getInt(Setting.KEY_KEY_SCROLL_X, 0), 0);
			}
		}, 100);
		ksvKey.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollChanged(int x, int y, int oldx, int oldy) {
				Setting.putInt(Setting.KEY_KEY_SCROLL_X, x);
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
		
		for (int i = 0; i < mIvKeys.length; i++) {
			final Note note = NoteUtil.getNodeById(i + 1);
			ImageView view = new ImageView(this);
			view.setScaleType(ScaleType.FIT_XY);
			view.setTag(note);
			
			if (note.isWhite) {
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
			
			mIvKeys[i] = view;
			
			final int idx = i;
			view.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent event) {
					ImageView iv = (ImageView) view;
					int action = event.getActionMasked();
					
					if (action == MotionEvent.ACTION_DOWN) {
						setKeyImage(idx, KeyStatus.Pressed);
						AudioUtil.play(note.file);
						onKeyClick(iv, note);
					} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
						if (mPlayType == PlayType.Follow) {
							while (mNoteIndex < mNoteList.size()) {
								Note note = mNoteList.get(mNoteIndex);
								if (note.id > 0) {
									break;
								}
								
								mNoteIndex++;
							}
							
							if (mNoteIndex < mNoteList.size()) {
								Note note = mNoteList.get(mNoteIndex);
								setKeyImage(note.id - 1, KeyStatus.Highlight);
							} else {
								mNoteIndex = 0;
								while (mNoteIndex < mNoteList.size()) {
									Note note = mNoteList.get(mNoteIndex);
									if (note.id > 0) {
										break;
									}
									
									mNoteIndex++;
								}
								
								if (mNoteIndex < mNoteList.size()) {
									Note note = mNoteList.get(mNoteIndex);
									setKeyImage(note.id - 1, KeyStatus.Highlight);
								} else {
									mNoteIndex = 0;
									setKeyImage(-1, KeyStatus.Normal);
								}
							}
							
							displayNotes();
						} else {
							setKeyImage(idx, KeyStatus.Normal);
						}
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
		
		AppUtil.runOnUiThread(mPlayRunnable, 1000, 50);

		String filename = Setting.getString(Setting.KEY_FILE_NAME);
		setFileName(filename);
		String content = FileUtil.readFile(filename);
		mNoteList = NoteUtil.toNotes(content);

		int type = Setting.getInt(Setting.KEY_PLAY_TYPE, PlayType.Free.ordinal());
		PlayType pt = PlayType.values()[type];
		
		if (pt == PlayType.Follow) {
			mNoteIndex = 0;
		} else {
			mNoteIndex = -1;
		}
		
		setPlayType(pt);
		
		displayNotes();
	}
	
	private void setFileName(String filename) {
		if (filename == null) {
			mFileName = null;
		} else if (FileUtil.exists(filename) == false) {
			mFileName = null;
			App.showToast(R.string.err_read_file_error);
		} else {
			mFileName = filename;
		}
		
		Setting.putString(Setting.KEY_FILE_NAME, mFileName);
		mTvFileName.setText(mFileName);
	}
	
	private void setPlayType(PlayType pt) {
		mPlayType = pt;
		Setting.putInt(Setting.KEY_PLAY_TYPE, mPlayType.ordinal());
		
		if (mPlayType == PlayType.Free) {
			mIvButton.setVisibility(View.GONE);
			mIvButton2.setVisibility(View.GONE);
			mTvPlayType.setText(R.string.pt_free);
			setKeyImage(-1, KeyStatus.Normal);
		} else if (mPlayType == PlayType.Auto) {
			mIvButton.setImageResource(R.drawable.triangle_right);
			mIvButton.setVisibility(View.VISIBLE);
			mIvButton2.setVisibility(View.GONE);
			mTvPlayType.setText(R.string.pt_auto);
		} else if (mPlayType == PlayType.Follow) {
			mIvButton.setVisibility(View.GONE);
			mIvButton2.setVisibility(View.GONE);
			mTvPlayType.setText(R.string.pt_follow);
			
			if (mNoteIndex < 0) {
				mNoteIndex = 0;
			}
			
			while (mNoteIndex < mNoteList.size()) {
				Note note = mNoteList.get(mNoteIndex);
				if (note.id > 0) {
					break;
				}
				
				mNoteIndex++;
			}
			
			if (mNoteIndex >= 0 && mNoteIndex < mNoteList.size()) {
				Note note = mNoteList.get(mNoteIndex);
				setKeyImage(note.id - 1, KeyStatus.Highlight);
			} else {
				mNoteIndex = 0;
				while (mNoteIndex < mNoteList.size()) {
					Note note = mNoteList.get(mNoteIndex);
					if (note.id > 0) {
						break;
					}
					
					mNoteIndex++;
				}
				
				if (mNoteIndex >= 0 && mNoteIndex < mNoteList.size()) {
					Note note = mNoteList.get(mNoteIndex);
					setKeyImage(note.id - 1, KeyStatus.Highlight);
				} else {
					mNoteIndex = 0;
					setKeyImage(-1, KeyStatus.Normal);
				}
			}
			
			displayNotes();
		} else if (mPlayType == PlayType.Edit) {
			mIvButton.setImageResource(R.drawable.save);
			mIvButton.setVisibility(View.VISIBLE);
			mIvButton2.setVisibility(View.VISIBLE);
			mTvPlayType.setText(R.string.pt_edit);
			setKeyImage(-1, KeyStatus.Normal);
		}
	}
	
	private void displayNotes() {
		int delta = mNoteIndex - mNgCursor;
		
		for (int i = 0; i < mNgNoteGrids.length; i++) {
			NoteGrid ng = mNgNoteGrids[i];
			
			int index = i + delta;
			
			if (index < 0 || index >= mNoteList.size()) {
				ng.top.setImageDrawable(null);
				ng.center.setText("");
				ng.bottom.setImageDrawable(null);
			} else {
				Note note = mNoteList.get(index);
				
				ng.top.setImageResource(AppUtil.getId("drawable", "note_" + note.top));
				ng.center.setText(note.center);
				ng.bottom.setImageResource(AppUtil.getId("drawable", "note_" + note.bottom));
			}
		}
	}
	
	private void moveLeft() {
		if (mPlayType == PlayType.Follow) {
			if (mNoteIndex > 0) {
				mNoteIndex--;
				
				if (mNoteIndex < mNoteList.size()) {
					Note note = mNoteList.get(mNoteIndex);
					setKeyImage(note.id - 1, KeyStatus.Highlight);
				}
				
				displayNotes();
			}
		} else {
			if (mNoteIndex >= 0) {
				mNoteIndex--;
				displayNotes();
			}
		}
	}
	
	private void moveRight() {
		if (mNoteIndex + 1 < mNoteList.size()) {
			mNoteIndex++;
			
			if (mPlayType == PlayType.Follow) {
				Note note = mNoteList.get(mNoteIndex);
				setKeyImage(note.id - 1, KeyStatus.Highlight);
			} else if (mPlayType == PlayType.Edit) {
				Note note = mNoteList.get(mNoteIndex);
				AudioUtil.play(note.file);
			}
			
			displayNotes();
		}
	}
	
	private void setKeyImage(int index, KeyStatus ks) {
		for (int i = 0; i < mIvKeys.length; i++) {
			ImageView iv = mIvKeys[i];
			Note note = (Note) iv.getTag();
			
			if (note.isWhite) {
				if (i == index) {
					if (ks == KeyStatus.Normal) {
						iv.setImageResource(R.drawable.white_key_nor);
					} else if (ks == KeyStatus.Pressed) {
						iv.setImageResource(R.drawable.white_key_pre);
					} else if (ks == KeyStatus.Highlight) {
						iv.setImageResource(R.drawable.white_key_hig);
					}
				} else {
					iv.setImageResource(R.drawable.white_key_nor);
				}
			} else {
				if (i == index) {
					if (ks == KeyStatus.Normal) {
						iv.setImageResource(R.drawable.black_key_nor);
					} else if (ks == KeyStatus.Pressed) {
						iv.setImageResource(R.drawable.black_key_pre);
					} else if (ks == KeyStatus.Highlight) {
						iv.setImageResource(R.drawable.black_key_hig);
					}
				} else {
					iv.setImageResource(R.drawable.black_key_nor);
				}
			}
		}
	}
	
	private void onKeyClick(View view, Note note) {
		if (mPlayType == PlayType.Free) {

		} else if (mPlayType == PlayType.Auto) {

		} else if (mPlayType == PlayType.Follow) {
			if (mNoteIndex >= 0 && mNoteIndex < mNoteList.size()) {
				Note curr = mNoteList.get(mNoteIndex);
				
				if (curr.id == note.id) {
					if (mNoteIndex + 1 < mNoteList.size()) {
						mNoteIndex++;
						displayNotes();
					}
				}
			}
		} else if (mPlayType == PlayType.Edit) {
			int index = mNoteIndex + 1;
			if (index > mNoteList.size()) {
				index = mNoteList.size();
			}
			
			mNoteList.add(index, note);
			mNoteIndex = index;
			displayNotes();
		}
	}
}
