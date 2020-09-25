package cn.nokia.speedtest5g.dialog;

import com.android.volley.util.SharedPreHandler;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.view.time.WheelHandler;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 时间选择器---年月日
 * @author zwq
 *
 */
@SuppressLint("InflateParams")
public class WheelTimeDialog extends AlertDialog{

	public WheelTimeDialog(Context context) {
		super(context);
		Window mWindow = getWindow();
		mWindow.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
		mWindow.setWindowAnimations(R.style.PopupBottomAnimation);  //添加动画
	}

	private ListenerBack mListenerBack;
	private int mType;
	//标题
	private TextView tvTitle;
	//清除按钮
	private ImageButton ivBtnClear;
	//当前布局
	private View mViewContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mViewContent = getLayoutInflater().inflate(R.layout.wheel_time_ui, null);
		setContentView(mViewContent);
		tvTitle = (TextView) findViewById(R.id.wheel_time_tv);
		ivBtnClear = (ImageButton) findViewById(R.id.wheel_time_btn_clean);
		ivBtnClear.setOnClickListener(clickListener);
		findViewById(R.id.wheel_time_btn).setOnClickListener(clickListener);
		Window mWindow = getWindow();
		WindowManager.LayoutParams p = mWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = SharedPreHandler.getShared(getContext()).getIntShared(TypeKey.getInstance().WIDTH(), 0);
		mWindow.setAttributes(p);
	}
	
	public void show(String title,int type,ListenerBack listener,boolean isShowClear){
		super.show();
		this.mListenerBack = listener;
		this.mType = type;
		tvTitle.setText(title);
		ivBtnClear.setVisibility(isShowClear ? View.VISIBLE : View.GONE);
		WheelHandler.getHandler().initTimeA(mViewContent);
	}

	public void showTomorrow(String title, int type, ListenerBack listener, boolean isShowClear) {
		super.show();
		this.mListenerBack = listener;
		this.mType = type;
		tvTitle.setText(title);
		ivBtnClear.setVisibility(isShowClear ? View.VISIBLE : View.GONE);
		WheelHandler.getHandler().initTimeStartTomorrow(mViewContent);
	}
	
	/**
	 * @param title 标题
	 * @param type 回调标识
	 * @param listener 回调监听
	 * @param isShowClear 是否可清除内容
	 * @param startDay 起始天数
	 * @param maxDay 最大日期
	 */
	public void showMaxDay(String title,int type,ListenerBack listener,boolean isShowClear,int startDay,int maxDay){
		super.show();
		this.mType 		  = type;
		this.mListenerBack = listener;
		tvTitle.setText(title);
		ivBtnClear.setVisibility(isShowClear ? View.VISIBLE : View.GONE);
		WheelHandler.getHandler().initTimeLately(mViewContent,startDay,maxDay);
	}
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mListenerBack != null) {
				String times = "";
				if (v.getId() != R.id.wheel_time_btn_clean) {
					times =  String.format("%s-%s-%s", WheelHandler.getHandler().getWheelValue(R.id.wheel_year),
							  WheelHandler.getHandler().getWheelValue(R.id.wheel_month),
							  WheelHandler.getHandler().getWheelValue(R.id.wheel_day));
				}
				mListenerBack.onListener(mType, times,true);
			}
			if (isShowing()) {
				dismiss();
			}
		}
	};
}
