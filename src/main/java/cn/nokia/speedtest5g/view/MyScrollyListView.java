package cn.nokia.speedtest5g.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 防止listview与scrolly冲突
 * @author zwq
 *
 */
public class MyScrollyListView extends ListView {
	 public MyScrollyListView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	    }

	    public MyScrollyListView(Context context) {
	        super(context);
	    }

	    public MyScrollyListView(Context context, AttributeSet attrs, int defStyleAttr) {
	        super(context, attrs, defStyleAttr);
	    }

	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        // TODO 自动生成的方法存根
	        int expandSpec = MeasureSpec.makeMeasureSpec(
	                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
	        super.onMeasure(widthMeasureSpec, expandSpec);
	    }
}
