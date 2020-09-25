package cn.nokia.speedtest5g.view;

import cn.nokia.speedtest5g.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/**
 * 防止GridView与scrolly冲突
 * 
 * @author zwq
 *
 */
public class MyScrollyGridView extends GridView {

	private int dividerColor; // 自定义熟悉：网格分割线颜色

	public MyScrollyGridView(Context context) {
		this(context, null);
	}

	public MyScrollyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyScrollyGridView);
		dividerColor = typedArray.getColor(R.styleable.MyScrollyGridView_dividerColor, -1);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		View localView = getChildAt(0);
		if (localView == null) {
			return;
		}
		if (dividerColor == -1) {
			return;
		}
		int column = getWidth() / localView.getWidth();// 列数
		int childCount = getChildCount();
		Paint localPaint;
		localPaint = new Paint();
		localPaint.setStyle(Paint.Style.STROKE);
		localPaint.setColor(dividerColor);// 设置分割线的颜色
		// localPaint.setColor(getResources().getColor(R.color.blue));//
		// 设置分割线的颜色
		for (int i = 0; i < childCount; i++) {
			View itemView = getChildAt(i);
			int itemTop = itemView.getTop();
			int itemBottom = itemView.getBottom();
			int itemLeft = itemView.getLeft();
			int itemRight = itemView.getRight();
			// 以下为全边线
			// canvas.drawLine(itemLeft, itemBottom, itemRight, itemBottom,
			// localPaint);// 下边线
			// canvas.drawLine(itemRight, itemTop, itemRight, itemBottom,
			// localPaint);// 右边线
			// // 第一行
			// if (i + 1 <= column) {
			// canvas.drawLine(itemLeft, itemTop, itemRight, itemTop,
			// localPaint);// 上边线
			// }
			// // 第一列
			// if ((i + 1) % column == 1) {
			// canvas.drawLine(itemLeft, itemTop, itemLeft, itemBottom,
			// localPaint);// 左边线
			// }
			// // 最后一行空白项补齐边线
			// if (childCount % column != 0) {
			// for (int j = 0; j <= (column - childCount % column); j++) {
			// itemView = getChildAt(childCount - 1);
			// int itemWidth = itemView.getWidth();
			// itemTop = itemView.getTop();
			// itemBottom = itemView.getBottom();
			// itemLeft = itemView.getLeft();
			// itemRight = itemView.getRight();
			// canvas.drawLine(itemRight + itemWidth * (j + 1), itemTop,
			// itemRight + itemWidth * (j + 1), itemBottom, localPaint);// 右边线
			// canvas.drawLine(itemRight + itemWidth * (j + 1), itemBottom,
			// itemRight + itemWidth * (j), itemBottom, localPaint);// 下边线
			// if (i + 1 <= column) {
			// canvas.drawLine(itemRight + itemWidth * (j + 1), itemTop,
			// itemRight + itemWidth * (j), itemTop, localPaint);// 上边线
			// }
			// }
			// }
			// 以下为四周无边线
			if ((i + 1) % column == 0) {// 每一行最后一个
				canvas.drawLine(itemLeft, itemBottom, itemRight, itemBottom,
				        localPaint);// 下边线

			} else if ((i + 1) > (childCount - (childCount % column))) {// 最后一行的item
				canvas.drawLine(itemRight, itemTop, itemRight, itemBottom,
				        localPaint);// 右边线

			} else {
				canvas.drawLine(itemRight, itemTop, itemRight, itemBottom,
				        localPaint); // 右边线
				canvas.drawLine(itemLeft, itemBottom, itemRight, itemBottom,
				        localPaint);// 下边线
			}
		}
	}
}
