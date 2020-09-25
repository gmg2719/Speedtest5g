package com.android.volley.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

public class ToastTextView extends TextView {

	public ToastTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(Color.argb(200, 60, 60, 60));
	}

	public ToastTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(Color.argb(200, 60, 60, 60));
	}

	public ToastTextView(Context context) {
		super(context);
		init(Color.argb(200, 60, 60, 60));
	}
	
	public ToastTextView(Context context,int color) {
		super(context);
		init(color);
	}
	
	@SuppressLint("NewApi")
	private void init(int color){
		GradientDrawable drawable = new GradientDrawable();
	    drawable.setColor(color);
	    drawable.setCornerRadius(8);
	    setBackground(drawable);
	    setPadding(15, 15, 15, 15);
		setTextColor(Color.WHITE);
	}
}
