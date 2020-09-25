package cn.nokia.speedtest5g.dialog;

import com.android.volley.util.SharedPreHandler;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.WybLog;

import android.content.Context;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 通用提示对话框-UI改） --- 样式：白底+蓝色确定按钮
 *
 * @author jinhaizheng
 */
public class CommonDialog extends BaseAlertDialog {
	private TextView tvTitle, tvContent;

	private LinearLayout llContentLayout;
	private View customContentView;

	private int mType;

	private Object mObject;

	private Button btnOk, btnCancel;

	private String title;
	private String strOk = "", strCancel = "";

	private View viewScorlly;
	private boolean onlyShowOkBtn = false;
	private boolean noTitle = false;
	private boolean isCenter = false;
	private boolean isShowSoftInput = false;// 是否显示输入法
	private boolean isHideBtnLayout = false;// 是否隐藏-底部按钮视图
	
	private float textSize = 0;

	public CommonDialog(Context context) {
		super(context, true);
	}

	/**
	 * @param context
	 * @param cancelable
	 */
	public CommonDialog(Context context, boolean cancelable) {
		super(context, true);
		setCancelable(cancelable);
	}

	/**
	 * @param context
	 * @param customViewlayoutResId
	 *            自定义内容视图
	 * @param isShowSoftInput
	 *            是否显输入法
	 */
	public CommonDialog(Context context, int customViewlayoutResId, boolean isShowSoftInput) {
		super(context, true);
		this.isShowSoftInput = isShowSoftInput;
		customContentView = LayoutInflater.from(context).inflate(customViewlayoutResId, null);
	}

	private boolean hasMeasured;
	private ListenerBack mListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_common);
		tvTitle = (TextView) findViewById(R.id.tv_common_dialog_title);
		llContentLayout = (LinearLayout) findViewById(R.id.ll_common_dialog_content);
		tvContent = (TextView) findViewById(R.id.tv_common_dialog_content);
		viewScorlly = findViewById(R.id.sv_common_dialog_content);
		
		if(textSize != 0){
			tvContent.setTextSize(textSize);
		}

		btnOk = (Button) findViewById(R.id.btn_common_dialog_ok);
		btnCancel = (Button) findViewById(R.id.btn_common_dialog_cancel);

		if (isHideBtnLayout) {
			findViewById(R.id.ll_common_dialog_btn_layout).setVisibility(View.GONE);
		}
		if (!noTitle && !TextUtils.isEmpty(title)) {
			tvTitle.setVisibility(View.VISIBLE);
			tvTitle.setText(title);
		}
		if (noTitle) {
			tvTitle.setVisibility(View.GONE);
		}
		btnOk.setOnClickListener(listener);
		if (!TextUtils.isEmpty(strOk)) {
			btnOk.setText(strOk);
		}
		if (!TextUtils.isEmpty(strCancel) && !onlyShowOkBtn) {
			btnCancel.setText(strCancel);
		}
		if (onlyShowOkBtn) {
			btnOk.setBackgroundResource(R.drawable.dialog_common_bottom_middle_btn);
			btnCancel.setVisibility(View.GONE);

		} else {
			btnCancel.setOnClickListener(listener);
			btnCancel.setVisibility(View.VISIBLE);
		}

		if (isCenter) {
			tvContent.setGravity(Gravity.CENTER);
		}
		if (customContentView != null) {
			tvContent.setVisibility(View.GONE);
			llContentLayout.addView(customContentView);
		}
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);// 这里xu
		if (isShowSoftInput) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
		viewScorlly.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {
				if (hasMeasured == false) {
					int heightGroup = SharedPreHandler.getShared(SpeedTest5g.getContext()).getIntShared(TypeKey.getInstance().HEIGHT(), 0);
					if (heightGroup > 100 && viewScorlly.getMeasuredHeight() > heightGroup / 2) {
						LayoutParams layoutParams = viewScorlly.getLayoutParams();
						layoutParams.height = heightGroup / 2;
						viewScorlly.setLayoutParams(layoutParams);
					}
					// 获取到宽度和高度后，可用于计算
					hasMeasured = true;
				}
				return true;
			}
		});
	}

	public CommonDialog setListener(ListenerBack listerne) {
		this.mListener = listerne;
		return this;
	}

	/**
	 * 自定义提示窗标题文本
	 * 
	 * @param title
	 * @return
	 * @return
	 */
	public CommonDialog setTitle(String title) {
		this.title = title;
		return this;
	}
	
	/**
	 * 设置文本字体大小
	 * @return
	 */
	public CommonDialog setTextSize(float textSize){
		this.textSize = textSize;
		return this;
	}

	/**
	 * 设置无标题
	 * 
	 * @return
	 * @return
	 */
	public CommonDialog setNoTitle(boolean noTitle) {
		this.noTitle = noTitle;
		return this;
	}

	/**
	 * 自定义确定取消按钮文本（统一确定按钮在右边）
	 * 
	 * @param ok
	 * @return
	 */
	public CommonDialog setButtonText(String ok, String cancel) {
		this.strOk = ok;
		this.strCancel = cancel;
		return this;
	}

	/**
	 * 是否文字居中
	 * 
	 * @return
	 */
	public CommonDialog setCenter(boolean isCenter) {
		this.isCenter = isCenter;
		return this;
	}

	/**
	 * 只显示ok按钮
	 * 
	 * @param onlyShowOkBtn
	 * @return
	 */
	public CommonDialog setButtomOnlyShowOk(boolean onlyShowOkBtn) {
		this.onlyShowOkBtn = onlyShowOkBtn;
		return this;
	}

	/**
	 * 只显示ok按钮
	 * 
	 * @return
	 */
	public CommonDialog setButtomOnlyShowOk(String ok) {
		this.strOk = ok;
		this.onlyShowOkBtn = true;
		return this;
	}

	/**
	 * 隐藏底部按钮
	 * 
	 * @return
	 */
	public CommonDialog setBtnLayoutHide(boolean isHideBtnLayout) {
		this.isHideBtnLayout = isHideBtnLayout;
		return this;
	}

	/**
	 * @param content
	 *            提示内容 ,支持Html.fromHtml(source)
	 */
	public void show(Object content) {
		try {
			super.show();
			this.mObject = content;
			if (content instanceof String) {
				tvContent.setText(content.toString());

			} else if (content instanceof Spanned) {
				tvContent.setText((Spanned) content);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isClickCancel;

	@Override
	public void dismiss() {
		if (mListener != null && !isClickCancel) {
			mListener.onListener(EnumRequest.OTHER_POPUP_CLOSE.toInt(), null, false);
		}
		super.dismiss();
	}

	/**
	 * @param content
	 *            提示内容 ,支持Html.fromHtml(source)
	 * @param type
	 *            提示类型
	 */
	public void show(Object content, int type) {
		try {
			super.show();
			this.mType = type;
			this.mObject = content;
			if (content instanceof String) {
				tvContent.setText(content.toString());
			} else if (content instanceof Spanned) {
				tvContent.setText((Spanned) content);
			}
		} catch (Exception e) {
			WybLog.i("错误：" + e.getMessage());
		}
	}

	/**
	 * 点击确定或取消按钮时 是否自动关闭弹窗，false则需要自行调用dismiss()关闭窗口
	 */
	private boolean isAotoDismiss = true;

	public CommonDialog setAotoDismiss(boolean isAotoDismiss) {
		this.isAotoDismiss = isAotoDismiss;
		return this;
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			isClickCancel = true;
			if (mListener != null) {
				mListener.onListener(mType, mObject, v.getId() == R.id.btn_common_dialog_ok ? true : false);
			}
			if (v.getId() == R.id.btn_common_dialog_cancel) {
				isAotoDismiss = true;
			}
			if (isShowing() && isAotoDismiss) {
				dismiss();
			}
		}
	};

	/**
	 * 自定义内容视图
	 * 
	 * @return
	 */
	public View getCustomContentView() {
		return customContentView;
	}
}
