package kk.piano.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.List;

import com.stone.app.BasicAdapter;

import kk.piano.R;
import kk.piano.activity.App;
import kk.piano.util.FileUtil;
import kk.piano.view.IDialogClickListener;
import kk.piano.view.IOnFileSelectListener;
import kk.piano.view.InputDialog;

public class FileAdapter extends BasicAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	
	private List<String> mDataList;
	
	private IOnFileSelectListener mFileListener;
	
	public FileAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}
	
	public void setDataList(List<String> dataList) {
		mDataList = dataList;
	}
	
	public void setFileListener(IOnFileSelectListener listener) {
		mFileListener = listener;
	}

	@Override
	public int getCount() {
		return mDataList == null ? 0 : mDataList.size();
	}
	
	@Override
	public View createView(int position) {
		final ViewHolder vh = new ViewHolder();
		View view = mInflater.inflate(R.layout.grid_file, null);
		view.setTag(vh);
			
		vh.name = (TextView) view.findViewById(R.id.tv_name);
		
		vh.name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mFileListener != null) {
					mFileListener.onSelect(vh.file);
				}
			}
		});
		
		view.findViewById(R.id.iv_delete).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String err = FileUtil.deleteFile(vh.file);
				if (err == null) {
					App.showToast(R.string.err_delete_file_success);
				} else {
					App.showToast(err);
				}
				
				if (mFileListener != null) {
					mFileListener.onChange();
				}
			}
		});
		
		view.findViewById(R.id.iv_rename).setOnClickListener(new OnClickListener() {
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
									err = FileUtil.renameFile(vh.file, name);
									
									if (err == null) {
										App.showToast(R.string.err_rename_file_success);
										dialog.dismiss();
									} else {
										App.showToast(err);
									}
									
									if (mFileListener != null) {
										mFileListener.onChange();
									}
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
		
		return view;
	}

	@Override
	public void updateView(int position, View view) {
		ViewHolder vh = (ViewHolder) view.getTag();
		
		vh.file = mDataList.get(position);;
		
		vh.name.setText(vh.file);
	}
	
	private static class ViewHolder {
		public String file;
		
		public TextView name;
	}
}
