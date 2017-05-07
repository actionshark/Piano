package kk.piano.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import kk.piano.R;
import kk.piano.util.AppUtil;
import kk.piano.view.IDialogClickListener.ClickType;

public class SimpleDialog extends Dialog {
	private TextView mTvMessage;
	private final TextView[] mTvButtons = new TextView[5];

	private IDialogClickListener mClickListener;

	public SimpleDialog(Context context) {
		super(context, R.style.simple_dialog);
		init();
	}

	protected void init() {
		setContentView(R.layout.dialog_simple);

		Window window = getWindow();
		LayoutParams lp = window.getAttributes();
		lp.width = Math.min(AppUtil.getPixcel(260), AppUtil.getScreenWidth() * 9 / 10);
		lp.height = Math.min(AppUtil.getPixcel(200), AppUtil.getScreenHeight() * 9 / 10);
		window.setAttributes(lp);

		mTvMessage = (TextView) findViewById(R.id.tv_message);

		for (int i = 0; i < mTvButtons.length; i++) {
			int id = AppUtil.getId("id", "tv_btn_" + i);
			mTvButtons[i] = (TextView) findViewById(id);

			final int index = i;
			mTvButtons[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mClickListener != null) {
						mClickListener.onClick(SimpleDialog.this, index, ClickType.Click);
					}
				}
			});
		}

		setCanceledOnTouchOutside(true);
		setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface di) {
				if (mClickListener != null) {
					mClickListener.onClick(SimpleDialog.this, -1, ClickType.Click);
				}
			}
		});
	}

	public void setMessage(int resId) {
		mTvMessage.setText(resId);
	}

	public void setMessage(String text) {
		mTvMessage.setText(text);
	}

	public void setButtons(Object... btns) {
		for (int i = 0; i < mTvButtons.length; i++) {
			if (i < btns.length) {
				Object btn = btns[i];
				if (btn instanceof Integer) {
					btn = AppUtil.getString((Integer) btn);
				}

				mTvButtons[i].setText(String.valueOf(btn));
				mTvButtons[i].setVisibility(View.VISIBLE);
			} else {
				mTvButtons[i].setVisibility(View.GONE);
			}
		}
	}

	public void setClickListener(IDialogClickListener listener) {
		mClickListener = listener;
	}
}
