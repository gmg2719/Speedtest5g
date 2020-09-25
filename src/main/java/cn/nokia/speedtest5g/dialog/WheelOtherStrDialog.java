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
 * -SPINNER类型适配器
 * @author zwq
 *
 */
public class WheelOtherStrDialog  extends AlertDialog{

	public WheelOtherStrDialog(Context context) {
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
	private ImageButton mBtnClear;
	//当前布局
	private View mViewContent;
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mViewContent = getLayoutInflater().inflate(R.layout.wheel_view_other, null);
		setContentView(mViewContent);
		tvTitle = (TextView) findViewById(R.id.wheel_title_tv);
		findViewById(R.id.wheel_title_btn).setOnClickListener(clickListener);
		mBtnClear = (ImageButton) findViewById(R.id.wheel_title_clean);
		mBtnClear.setOnClickListener(clickListener);
		Window mWindow = getWindow();
		WindowManager.LayoutParams p = mWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = SharedPreHandler.getShared(getContext()).getIntShared(TypeKey.getInstance().WIDTH(), 0);
		mWindow.setAttributes(p);
	}
	
	public WheelOtherStrDialog show(String title,String[] arr,int type,ListenerBack listener,boolean isShowClear){
		super.show();
		this.mListenerBack = listener;
		this.mType = type;
		
		tvTitle.setText(title);
		mBtnClear.setVisibility(isShowClear ? View.VISIBLE : View.GONE);
		WheelHandler.getHandler().initOther(mViewContent, arr,false);
		return this;
	}
	
	//是否获取游标数据
	private boolean isPostion;
	public void isPostion(){
		isPostion = true;
	}
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mListenerBack != null) {
				if (v.getId() == R.id.wheel_title_clean) {
					mListenerBack.onListener(mType, isPostion ? -1 : "", false);
				}else {
					mListenerBack.onListener(mType,isPostion ? WheelHandler.getHandler().getWheelPosition(R.id.wheel_other) : WheelHandler.getHandler().getWheelValue(R.id.wheel_other), true);
				}
			}
			if (isShowing()) {
				dismiss();
			}
		}
	};
}
