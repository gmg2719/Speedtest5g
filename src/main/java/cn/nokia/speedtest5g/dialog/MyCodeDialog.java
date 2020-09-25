package cn.nokia.speedtest5g.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.util.MyToast;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.uitl.CopyUtil;
import cn.nokia.speedtest5g.app.uitl.ImageOptionsUtil;

/**
 * 二维码显示
 * @author zwq
 *
 */
public class MyCodeDialog extends BaseAlertDialog {

	private ImageView mIvCode = null;
	private TextView mTvTitel = null;
	private TextView mTvUrl = null;
	private int mType; //0福建版 1全国版 
	private HashMap<String, String> mPersonalCenterMap = null;

	public MyCodeDialog(Context context) {
		super(context);
	}

	public MyCodeDialog setData(HashMap<String, String> map){
		this.mPersonalCenterMap = map;
		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_code);
		mIvCode = (ImageView) findViewById(R.id.codeDialog_iv_code);
		mTvTitel = (TextView) findViewById(R.id.codeDialog_tv_title);
		mTvUrl = (TextView) findViewById(R.id.codeDialog_tv_url);
		findViewById(R.id.codeDialog_copy).setOnClickListener(clickListener);

		String picturePath = "";
		String apkPath = "";
		if(mPersonalCenterMap != null){
			if (mType == 0) {
				//				mTvTitel.setText("福建版");
				picturePath = mPersonalCenterMap.get("55");
				apkPath = mPersonalCenterMap.get("56");
			}else if(mType == 1){
				mTvTitel.setText("5G网速测试");
				picturePath = mPersonalCenterMap.get("50");
				apkPath = mPersonalCenterMap.get("51");
			}
			mTvUrl.setText(apkPath);
			ImageLoader.getInstance().displayImage(picturePath, mIvCode, ImageOptionsUtil.getInstances().getOptionsDisk());
		}
	}

	/**
	 * @param type 0福建 1全国
	 */
	public void show(int type) {
		this.mType = type;
		super.show();
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
            if (v.getId() == R.id.codeDialog_copy) {
                CopyUtil.getInstance().copyContent(mTvUrl.getText().toString());
                MyToast.getInstance(SpeedTest5g.getContext()).showCommon("复制成功");
            }
		}
	};
}
