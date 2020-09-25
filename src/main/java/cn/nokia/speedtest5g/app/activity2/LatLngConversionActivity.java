package cn.nokia.speedtest5g.app.activity2;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;
import java.util.Map;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.uitl.CopyUtil;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.gis.model.JJ_LatLng;
import cn.nokia.speedtest5g.speedtest.SpeedTestMapActivity;
import cn.nokia.speedtest5g.util.gps.GPSUtil;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar;
/**
 * 经纬度换算
 * @author xujianjun
 *
 */
public class LatLngConversionActivity extends BaseActionBarActivity implements OnClickListener{
	
	private EditText edtLng84 = null;
	private EditText edtLat84 = null;
	private TextView tvCopy84 = null;

	private EditText edtLngSpark = null;
	private EditText edtLatSpark = null;
	private TextView tvCopySpark = null;

	private EditText edtLngBaidu = null;
	private EditText edtLatBaidu = null;
	private TextView tvCopyBaidu = null;

	private TextView tvInstruction84 = null;
	private TextView tvInstructionSpark = null;
	private TextView tvInstructionBaidu = null;

	private MyTextWatcher mTextWatcherLng84 = null;
	private MyTextWatcher mTextWatcherLat84 = null;

	private MyTextWatcher mTextWatcherLngSpark = null;
	private MyTextWatcher mTextWatcherLatSpark = null;

	private MyTextWatcher mTextWatcherLngBaidu = null;
	private MyTextWatcher mTextWatcherLatBaidu = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_latlng_conversion_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		init("经纬度转换", true);
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		actionBar.addAction(new MyActionBar.MenuAction(R.drawable.icon_latlng_conversion_location, EnumRequest.MENU_SELECT_ONE.toInt(), ""));// 标题栏添加图标
		edtLng84 = (EditText) findViewById(R.id.edt_conversion_lon_84);
		edtLat84 = (EditText) findViewById(R.id.edt_conversion_lat_84);
		tvCopy84 = (TextView) findViewById(R.id.tv_copy_84);

		edtLngSpark = (EditText) findViewById(R.id.edt_conversion_lon_spark);
		edtLatSpark = (EditText) findViewById(R.id.edt_conversion_lat_spark);
		tvCopySpark = (TextView) findViewById(R.id.tv_copy_spark);

		edtLngBaidu = (EditText) findViewById(R.id.edt_conversion_lon_baidu);
		edtLatBaidu = (EditText) findViewById(R.id.edt_conversion_lat_baidu);
		tvCopyBaidu = (TextView) findViewById(R.id.tv_copy_baidu);

		tvInstruction84 = (TextView) findViewById(R.id.tv_instruction_84);
		tvInstructionSpark = (TextView) findViewById(R.id.tv_instruction_spark);
		tvInstructionBaidu = (TextView) findViewById(R.id.tv_instruction_baidu);

		tvInstruction84.setText(Html.fromHtml("<Font><tt>84坐标说明:</tt>国际坐标系，为一种地图坐标，也是目前广泛使用的GPS全球卫星定位系统(包含北斗定位系统)所使用的坐标系。</Font>"));
		tvInstructionSpark.setText(Html.fromHtml("<Font><tt>火星坐标系:</tt>由中国国家测绘局制订的地理信息系统的坐标系统。由84坐标系经加密后的坐标系，高德、腾讯和Google中国地图采用。</Font>"));
		tvInstructionBaidu.setText(Html.fromHtml("<Font><tt>百度坐标系:</tt>即火星坐标系经加密后的坐标系，百度地图采用</Font>"));


		tvCopy84.setOnClickListener(this);
		tvCopySpark.setOnClickListener(this);
		tvCopyBaidu.setOnClickListener(this);

		mTextWatcherLng84 = new MyTextWatcher(1);
		mTextWatcherLat84 = new MyTextWatcher(1);

		mTextWatcherLngSpark = new MyTextWatcher(2);
		mTextWatcherLatSpark = new MyTextWatcher(2);

		mTextWatcherLngBaidu = new MyTextWatcher(3);
		mTextWatcherLatBaidu = new MyTextWatcher(3);


		edtLng84.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					edtLng84.removeTextChangedListener(mTextWatcherLng84);
				}else{
					edtLng84.addTextChangedListener(mTextWatcherLng84);
				}
			}
		});
		edtLat84.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					edtLat84.removeTextChangedListener(mTextWatcherLat84);
				}else{
					edtLat84.addTextChangedListener(mTextWatcherLat84);
				}
			}
		});

		edtLngSpark.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					edtLngSpark.removeTextChangedListener(mTextWatcherLngSpark);
				}else{
					edtLngSpark.addTextChangedListener(mTextWatcherLngSpark);
				}
			}
		});
		edtLatSpark.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					edtLatSpark.removeTextChangedListener(mTextWatcherLatSpark);
				}else{
					edtLatSpark.addTextChangedListener(mTextWatcherLatSpark);
				}
			}
		});

		edtLngBaidu.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					edtLngBaidu.removeTextChangedListener(mTextWatcherLngBaidu);
				}else{
					edtLngBaidu.addTextChangedListener(mTextWatcherLngBaidu);
				}
			}
		});
		edtLatBaidu.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					edtLatBaidu.removeTextChangedListener(mTextWatcherLatBaidu);
				}else{
					edtLatBaidu.addTextChangedListener(mTextWatcherLatBaidu);
				}
			}
		});



	}

	@Override
	public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_copy_84) {
            if (!TextUtils.isEmpty(edtLng84.getText().toString()) && !TextUtils.isEmpty(edtLat84.getText().toString())) {
                CopyUtil.getInstance().copyContent("经度： " + edtLng84.getText().toString() + "纬度：" + edtLat84.getText().toString());
                showCommon("成功复制经纬度");
            } else {
                showCommon("请填写完整经纬度");
            }
        } else if (id == R.id.tv_copy_spark) {
            if (!TextUtils.isEmpty(edtLngSpark.getText().toString()) && !TextUtils.isEmpty(edtLatSpark.getText().toString())) {
                CopyUtil.getInstance().copyContent("经度： " + edtLngSpark.getText().toString() + "纬度：" + edtLatSpark.getText().toString());
                showCommon("成功复制经纬度");
            } else {
                showCommon("请填写完整经纬度");
            }
        } else if (id == R.id.tv_copy_baidu) {
            if (!TextUtils.isEmpty(edtLngBaidu.getText().toString()) && !TextUtils.isEmpty(edtLatBaidu.getText().toString())) {
                CopyUtil.getInstance().copyContent("经度： " + edtLngBaidu.getText().toString() + "纬度：" + edtLatBaidu.getText().toString());
                showCommon("成功复制经纬度");
            } else {
                showCommon("请填写完整经纬度");
            }
        }
	}

	private class MyTextWatcher implements TextWatcher{
		private int mType;

		public MyTextWatcher(int type) {
			this.mType = type;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(mType ==1){
				//84坐标系
				if(!TextUtils.isEmpty(edtLng84.getText().toString()) &&!TextUtils.isEmpty(edtLat84.getText().toString()) ){
					latlngConversion(new LatLng(UtilHandler.getInstance().toDouble(edtLat84.getText().toString(), 0),
							UtilHandler.getInstance().toDouble(edtLng84.getText().toString(), 0)), mType);
				}else{
					edtLngSpark.setText("");
					edtLatSpark.setText("");
					edtLngBaidu.setText("");
					edtLatBaidu.setText("");
				}
			}else if(mType ==2){
				//火星坐标系
				if(!TextUtils.isEmpty(edtLngSpark.getText().toString()) &&!TextUtils.isEmpty(edtLatSpark.getText().toString()) ){
					latlngConversion(new LatLng(UtilHandler.getInstance().toDouble(edtLatSpark.getText().toString(), 0),
							UtilHandler.getInstance().toDouble(edtLngSpark.getText().toString(), 0)), mType);
				}else{
					edtLng84.setText("");
					edtLat84.setText("");
					edtLngBaidu.setText("");
					edtLatBaidu.setText("");
				}

			}else if(mType==3){
				//百度坐标系
				if(!TextUtils.isEmpty(edtLngBaidu.getText().toString()) &&!TextUtils.isEmpty(edtLatBaidu.getText().toString()) ){
					latlngConversion(new LatLng(UtilHandler.getInstance().toDouble(edtLatBaidu.getText().toString(), 0),
							UtilHandler.getInstance().toDouble(edtLngBaidu.getText().toString(), 0)), mType);
				}else{
					edtLngSpark.setText("");
					edtLatSpark.setText("");
					edtLng84.setText("");
					edtLat84.setText("");
				}
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	}

	private void latlngConversion(LatLng latlng,int type){
		if(type == 1){
			//GPS ->火星
			Map<String, Double> latlngSpark = GPSUtil.getInstances().transform(latlng.longitude, latlng.latitude);
			edtLngSpark.setText(UtilHandler.getInstance().toDfSum(latlngSpark.put("lon", 0.0), "000000")+"");
			edtLatSpark.setText(UtilHandler.getInstance().toDfSum(latlngSpark.put("lat", 0.0), "000000")+"");

			//GPS ->百度
			LatLng latlngBaidu = GPSUtil.getInstances().toBdLatlng(latlng);
			edtLngBaidu.setText(UtilHandler.getInstance().toDfSum(latlngBaidu.longitude, "000000")+"");
			edtLatBaidu.setText(UtilHandler.getInstance().toDfSum(latlngBaidu.latitude, "000000")+"");

		}else if(type == 2){
			//火星 ->GPS 
			Map<String, Double> latlngSpark = GPSUtil.getInstances().gcj2wgs(latlng.longitude, latlng.latitude);
			edtLng84.setText(UtilHandler.getInstance().toDfSum(latlngSpark.put("lon", 0.0), "000000")+"");
			edtLat84.setText(UtilHandler.getInstance().toDfSum(latlngSpark.put("lat", 0.0), "000000")+"");
			//火星 ->百度
			Map<String, Double> latlngBaidu = GPSUtil.getInstances().gcj2bd(latlng.latitude, latlng.longitude);
			edtLngBaidu.setText(UtilHandler.getInstance().toDfSum(latlngBaidu.put("lon", 0.0), "000000")+"");
			edtLatBaidu.setText(UtilHandler.getInstance().toDfSum(latlngBaidu.put("lat", 0.0), "000000")+"");

		}else if(type == 3){
			//百度 ->GPS
			LatLng latlng84 = GPSUtil.getInstances().toGpsLatLng(latlng);
			edtLng84.setText(UtilHandler.getInstance().toDfSum(latlng84.longitude, "000000")+"");
			edtLat84.setText(UtilHandler.getInstance().toDfSum(latlng84.latitude, "000000")+"");

			//百度 ->火星 bd2gcj
			Map<String, Double> latlngSpark = GPSUtil.getInstances().bd2gcj(latlng.latitude, latlng.longitude);
			edtLngSpark.setText(UtilHandler.getInstance().toDfSum(latlngSpark.put("lon", 0.0), "000000")+"");
			edtLatSpark.setText(UtilHandler.getInstance().toDfSum(latlngSpark.put("lat", 0.0), "000000")+"");
		}
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);

		if(type ==EnumRequest.MENU_SELECT_ONE.toInt()){
			if(!TextUtils.isEmpty(edtLat84.getText().toString())&&!TextUtils.isEmpty(edtLng84.getText().toString())){
				JJ_LatLng jjLatlng = new JJ_LatLng();
				jjLatlng.setLat(UtilHandler.getInstance().toDfSum(UtilHandler.getInstance().toDouble(edtLat84.getText().toString(), 0), "000000"));
				jjLatlng.setLng(UtilHandler.getInstance().toDfSum(UtilHandler.getInstance().toDouble(edtLng84.getText().toString(), 0), "000000"));
				Bundle bundle = new Bundle();
				bundle.putSerializable("data", (Serializable)jjLatlng);
				goIntent(SpeedTestMapActivity.class, bundle, false);
			}else{
				showCommon("请填写完整经纬度");
			}
		}
	}

	@Override
	public void initStatistics() {
		installStatistics(R.string.code_jwdzh);
	}

}
