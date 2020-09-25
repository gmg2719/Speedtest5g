package com.android.volley.util;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 不用焦点可实现跑马灯效果
 * @author Administrator
 *
 */
public class MarqueesTextView extends TextView {

	public MarqueesTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MarqueesTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MarqueesTextView(Context context) {
		super(context);
		init();
	}

	@Override
	public boolean isFocused() {
		return true;
	}
	
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
	}
	
	
	private void init(){
		setSingleLine(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setHorizontallyScrolling(true);
		setEllipsize(TruncateAt.MARQUEE);
		//滚动回数，默认是无数回-1
		setMarqueeRepeatLimit(-1);
	}
}
