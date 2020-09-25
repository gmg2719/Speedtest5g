package cn.nokia.speedtest5g.app.uitl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;

public class WaterMarkUtil {

	/**
	 * 显示水印布局
	 *
	 * @param activity
	 */
	@SuppressLint("InflateParams")
	public static void showWatermarkView(final Activity activity,String str) {
		if (TextUtils.isEmpty(str)) {
			return;
		}
		//		final ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
		//		WatermarkTiltText framView = null;
		//		if(rootView != null){
		//			framView = (WatermarkTiltText)rootView.findViewById(R.id.mdContent);
		//			if(framView == null){
		//				framView = new WatermarkTiltText(activity);
		//				framView.setId(R.id.mdContent);
		//				if(UtilHandler.getInstance().isMobileNO(str)){
		//					framView.setText(UtilHandler.getInstance().hidePhoneNoMid4(str));
		//				}else{
		//					framView.setText(str);
		//				}
		//				//可对水印布局进行初始化操作
		//				rootView.addView(framView);
		//			}else{
		//				if(UtilHandler.getInstance().isMobileNO(str)){
		//					framView.setText(UtilHandler.getInstance().hidePhoneNoMid4(str));
		//				}else{
		//					framView.setText(str);
		//				}
		//			}
		//		}
	}
}
