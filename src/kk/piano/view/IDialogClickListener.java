package kk.piano.view;

import android.app.Dialog;

public interface IDialogClickListener {
	public static enum ClickType {
		Click, LongClick,
	}

	public void onClick(Dialog dialog, int index, ClickType type);
}
