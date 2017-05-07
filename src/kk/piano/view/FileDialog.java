package kk.piano.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ListView;

import java.util.List;

import kk.piano.R;
import kk.piano.activity.App;
import kk.piano.adapter.FileAdapter;
import kk.piano.util.AppUtil;
import kk.piano.util.FileUtil;

public class FileDialog extends Dialog {
	private Context mContext;
	private FileAdapter mAdapter;
	
	private IOnFileSelectListener mFileListener;
	
	public FileDialog(Context context) {
		super(context, R.style.simple_dialog);
		init(context);
	}

	protected void init(Context context) {
		mContext = context;
		setContentView(R.layout.dialog_file);
		
		Window window = getWindow();
		LayoutParams lp = window.getAttributes();
		lp.width = Math.min(AppUtil.getPixcel(300), AppUtil.getScreenWidth() * 9 / 10);
		lp.height = Math.min(AppUtil.getPixcel(330), AppUtil.getScreenHeight() * 9 / 10);
		window.setAttributes(lp);
		
		setCanceledOnTouchOutside(true);
		
		ListView lvList = (ListView) findViewById(R.id.lv_list);
		mAdapter = new FileAdapter(context);
		mAdapter.setFileListener(new IOnFileSelectListener() {
			@Override
			public void onSelect(String name) {
				if (mFileListener != null) {
					mFileListener.onSelect(name);
				}
				
				dismiss();
			}
			
			@Override
			public void onChange() {
				refresh();
			}
		});
		lvList.setAdapter(mAdapter);
		
		findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		
		findViewById(R.id.iv_add).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				InputDialog dialog = new InputDialog(mContext);
				dialog.setClickListener(new IDialogClickListener() {
					@Override
					public void onClick(Dialog dialog, int index, ClickType type) {
						if (index == 1) {
							InputDialog id = (InputDialog) dialog;
							String name = id.getInput();
							String err = FileUtil.checkName(name);
							
							if (err == null) {
								boolean exists = FileUtil.exists(name);
								
								if (exists) {
									App.showToast(R.string.err_file_exists);
								} else {
									err = FileUtil.createFile(name);
									
									if (err == null) {
										App.showToast(R.string.err_create_file_success);
										dialog.dismiss();
									} else {
										App.showToast(err);
									}
									
									refresh();
								}
							} else {
								App.showToast(err);
							}
						} else {
							dialog.dismiss();
						}
					}
				});
				dialog.show();
			}
		});
		
		refresh();
	}
	
	private void refresh() {
		List<String> dataList = FileUtil.getList();
		mAdapter.setDataList(dataList);
		mAdapter.notifyDataSetChanged();
	}
	
	public void setFileListener(IOnFileSelectListener listener) {
		mFileListener = listener;
	}
}
