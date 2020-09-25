package cn.nokia.speedtest5g.app.activity2;

import org.xclcharts.common.MathHelper;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 楼间对打工参估算
 * @author xiongzk
 *
 */
public class ParamEstimateAcitivity extends BaseActionBarActivity{
	
	//楼宽、楼高、间距
	private EditText etWid,etHei,etLon;
	//下倾角、水平波瓣、垂直波瓣
	private TextView tvAlpha,tvBeta,tvGama;
	//天线增益、墙体损耗、中心店覆盖强度、频率
	private EditText etTxzy,etQtsh,etZxdfgqd,etPl;
	//天线口功率
	private TextView tvTxkgl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_param_estimate_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;
		
		initView();
		initViewTextDownangle();
		initViewTextPower();
		
		init("楼间对打工参估算",true);
	}
	
	private void initView() {
		//---------------------------下倾角计算
		etWid = (EditText) findViewById(R.id.build_width);
		etHei = (EditText) findViewById(R.id.build_height);
		etLon = (EditText) findViewById(R.id.build_dis);
		etWid.addTextChangedListener(new MyTextWatcher(etWid));
		etHei.addTextChangedListener(new MyTextWatcher(etHei));
		etLon.addTextChangedListener(new MyTextWatcher(etLon));
		
		tvAlpha = (TextView) findViewById(R.id.downangle_alpha);
		tvBeta = (TextView) findViewById(R.id.horizont_lobe);
		tvGama = (TextView) findViewById(R.id.vertic_lobe);
		
		//---------------------------天线口功率计算
		etTxzy = (EditText) findViewById(R.id.et_txzy);
		etQtsh = (EditText) findViewById(R.id.et_qtsh);
		etZxdfgqd = (EditText) findViewById(R.id.et_zxdfgqd);
		etPl = (EditText) findViewById(R.id.et_pl);
		etTxzy.addTextChangedListener(new MyTextWatcher(etTxzy));
		etQtsh.addTextChangedListener(new MyTextWatcher(etQtsh));
		etZxdfgqd.addTextChangedListener(new MyTextWatcher(etZxdfgqd));
		etPl.addTextChangedListener(new MyTextWatcher(etPl));
		
		tvTxkgl = (TextView) findViewById(R.id.tv_txkgl);
	}
	
	private void initViewTextDownangle() {
		etWid.setText("50");
		etHei.setText("60");
		etLon.setText("50");
		
		double alpha = Math.atan(60.0 / (2 * 50.0)) * 180.0 / Math.PI;
		double beta = 2 * Math.atan(50.0 / (2 * 50.0) * Math.cos((alpha / 180) * Math.PI)) * 180.0 / Math.PI;
		double gama = 2 * (Math.atan((60.0 / 2 + 50.0 * Math.tan((alpha / 180) * Math.PI)) / 50.0) * 180.0 / Math.PI - alpha);
		tvAlpha.setText(String.valueOf(Math.round(alpha)));
		tvBeta.setText(String.valueOf(Math.round(beta)));
		tvGama.setText(String.valueOf(Math.round(gama)));
		
	}
	
	private void initViewTextPower() {
		etTxzy.setText("15");
		etQtsh.setText("20");
		etZxdfgqd.setText("-102");
		etPl.setText("1900");
		
		double zxd = Double.parseDouble(etZxdfgqd.getText().toString());
		double f = Double.parseDouble(etPl.getText().toString());
		double qtsh = Double.parseDouble(etQtsh.getText().toString());
		double txzy = Double.parseDouble(etTxzy.getText().toString());
		double h = Double.parseDouble(etHei.getText().toString());
		double l = Double.parseDouble(etLon.getText().toString());
		double d = (Math.sqrt(h * h + l * l) * 0.001);
		double txkgl = zxd + 32.4 +20 * (Math.log(f) / Math.log(10)) + 20 * (Math.log(d) / Math.log(10)) + qtsh + 10 * (Math.log(1200) / Math.log(10)) - txzy;
		tvTxkgl.setText(String.valueOf(MathHelper.getInstance().round(txkgl, 2)));
		
	}
	
	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
	}

	private class MyTextWatcher implements TextWatcher {
		
		private EditText v;
		
		public MyTextWatcher(EditText v) {
			this.v = v;
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().startsWith("0") && s.toString().length() > 1) {
				String thisStr = s.toString().substring(1, s.toString().length());
				v.setText(thisStr);
				v.setSelection(thisStr.length());
			}
			if (v.getId() == R.id.build_width || v.getId() == R.id.build_height || v.getId() == R.id.build_dis) {
				if ((etWid.getText() != null && !etWid.getText().toString().equals(""))
						&& (etHei.getText() != null && !etHei.getText().toString().equals(""))
						&& (etLon.getText() != null && !etLon.getText().toString().equals(""))) {
					double w = Double.parseDouble(etWid.getText().toString());
					double h = Double.parseDouble(etHei.getText().toString());
					double l = Double.parseDouble(etLon.getText().toString());
					
					double alpha = Math.atan(h / (2 * l)) * 180.0 / Math.PI;
					double beta = 2 * Math.atan(w / (2 * l) * Math.cos((alpha / 180) * Math.PI)) * 180.0 / Math.PI;
					double gama = 2 * (Math.atan((h / 2 + l * Math.tan((alpha / 180) * Math.PI)) / l) * 180.0 / Math.PI - alpha);
					tvAlpha.setText(String.valueOf(Math.round(alpha)));
					tvBeta.setText(String.valueOf(Math.round(beta)));
					tvGama.setText(String.valueOf(Math.round(gama)));
					
					if ((etTxzy.getText() != null && !etTxzy.getText().toString().equals("") && !etTxzy.getText().toString().equals("+") && !etTxzy.getText().toString().equals("-"))
							&& (etQtsh.getText() != null && !etQtsh.getText().toString().equals("") && !etQtsh.getText().toString().equals("+") && !etQtsh.getText().toString().equals("-"))
							&& (etZxdfgqd.getText() != null && !etZxdfgqd.getText().toString().equals("") && !etZxdfgqd.getText().toString().equals("+") && !etZxdfgqd.getText().toString().equals("-"))
							&& (etPl.getText() != null && !etPl.getText().toString().equals("") && !etPl.getText().toString().equals("+") && !etPl.getText().toString().equals("-"))
							&& l != 0 && h != 0) {
						double zxd = Double.parseDouble(etZxdfgqd.getText().toString());
						double f = Double.parseDouble(etPl.getText().toString());
						double qtsh = Double.parseDouble(etQtsh.getText().toString());
						double txzy = Double.parseDouble(etTxzy.getText().toString());
						double d = (Math.sqrt(h * h + l * l) * 0.001);
						if (f == 0.0 || d == 0.0) {
							tvTxkgl.setText("");
							return;
						}
						double txkgl = zxd + 32.4 +20 * (Math.log(f) / Math.log(10)) + 20 * (Math.log(d) / Math.log(10)) + qtsh + 10 * (Math.log(1200) / Math.log(10)) - txzy;
						tvTxkgl.setText(String.valueOf(MathHelper.getInstance().round(txkgl, 2)));
					} else {
						tvTxkgl.setText("");
					}
					
				} else {
					if (etLon.getText() == null || etLon.getText().toString().equals("") || etHei.getText() == null || etHei.getText().toString().equals("")) {
						tvTxkgl.setText("");
					}
					tvAlpha.setText("");
					tvBeta.setText("");
					tvGama.setText("");
				}
			} else if (v.getId() == R.id.et_txzy || v.getId() == R.id.et_qtsh 
					|| v.getId() == R.id.et_zxdfgqd || v.getId() == R.id.et_pl) {
				
				//超出数值还原
//				if (v.getId() == R.id.et_txzy && etTxzy.getText() != null) {
//					double txzy = Double.parseDouble(etTxzy.getText().toString());
//					if (txzy > 100 || txzy < 0) {
//						
//						return;
//					}
//				}
				
				if ((etTxzy.getText() != null && !etTxzy.getText().toString().equals("") && !etTxzy.getText().toString().equals("+") && !etTxzy.getText().toString().equals("-"))
						&& (etQtsh.getText() != null && !etQtsh.getText().toString().equals("") && !etQtsh.getText().toString().equals("+") && !etQtsh.getText().toString().equals("-"))
						&& (etZxdfgqd.getText() != null && !etZxdfgqd.getText().toString().equals("") && !etZxdfgqd.getText().toString().equals("+") && !etZxdfgqd.getText().toString().equals("-"))
						&& (etPl.getText() != null && !etPl.getText().toString().equals("") && !etPl.getText().toString().equals("+") && !etPl.getText().toString().equals("-"))
						&& (etHei.getText() != null && !etHei.getText().toString().equals(""))
						&& (etLon.getText() != null && !etLon.getText().toString().equals(""))
						&& Double.parseDouble(etLon.getText().toString()) != 0
						&& Double.parseDouble(etHei.getText().toString()) != 0) {
					double zxd = Double.parseDouble(etZxdfgqd.getText().toString());
					double f = Double.parseDouble(etPl.getText().toString());
					double qtsh = Double.parseDouble(etQtsh.getText().toString());
					double txzy = Double.parseDouble(etTxzy.getText().toString());
					double h = Double.parseDouble(etHei.getText().toString());
					double l = Double.parseDouble(etLon.getText().toString());
					double d = (Math.sqrt(h * h + l * l) * 0.001);
					if (f == 0.0 || d == 0.0) {
						tvTxkgl.setText("");
						return;
					}
					double txkgl = zxd + 32.4 +20 * (Math.log(f) / Math.log(10)) + 20 * (Math.log(d) / Math.log(10)) + qtsh + 10 * (Math.log(1200) / Math.log(10)) - txzy;
					tvTxkgl.setText(String.valueOf(MathHelper.getInstance().round(txkgl, 2)));
				} else {
					tvTxkgl.setText("");
				}
			}
		}
	}
	
}
