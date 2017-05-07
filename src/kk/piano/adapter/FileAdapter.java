package kk.piano.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import kk.piano.R;
import kk.piano.activity.App;
import kk.piano.util.FileUtil;
import kk.piano.view.IDialogClickListener;
import kk.piano.view.IOnFileSelectListener;
import kk.piano.view.InputDialog;

public class FileAdapter extends BaseAdapter {
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
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder vh;
		String file = mDataList.get(position);
		
		if (view == null) {
			view = mInflater.inflate(R.layout.grid_file, null);
			vh = new ViewHolder();
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
		} else {
			vh = (ViewHolder) view.getTag();
		}
		
		vh.file = file;
		
		vh.name.setText(file);
		
		return view;
	}
	
	private static class ViewHolder {
		public String file;
		
		public TextView name;
	}
}
