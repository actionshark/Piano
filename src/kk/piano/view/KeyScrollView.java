package kk.piano.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class KeyScrollView extends HorizontalScrollView {
	public static interface OnScrollListener {
		public void onScrollChanged(int x, int y, int oldx, int oldy);
	}

	private OnScrollListener mListener;

	public KeyScrollView(Context context) {
		super(context);
	}

	public KeyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KeyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setOnScrollListener(OnScrollListener listener) {
		mListener = listener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		
		if (mListener != null) {
			mListener.onScrollChanged(x, y, oldx, oldy);
		}
	}
}
