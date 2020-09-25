package cn.nokia.speedtest5g.app.activity2;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.util.InputMethodUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.dialog.WheelOtherStrPopuo;

/**
 * 频率频点换算
 * @author xujianjun
 *
 */
public class BandConversionActivity extends BaseActionBarActivity implements OnClickListener{
	
	private LinearLayout llBandLte = null;
	private TextView tvBandLte = null;
	private EditText edtLteFreUp = null;
	private EditText edtLteBandUp = null;
	private EditText edtLteFreDown = null;
	private EditText edtLteBandDown = null;

	private TextView tvLteFreUpRange = null;
	private TextView tvLteBandUpRange = null;
	private TextView tvLteFreDownRange = null;
	private TextView tvLteBandDownRange = null;

	private EditText edt5gBand = null;
	private TextView tv5gFre = null;

	private TextView tvBandGsm = null;
	private EditText edtGsmFreUp = null;
	private EditText edtGsmFreDown = null;
	private EditText edtGsmBand = null;

	private TextView tvLteBandSearch = null;
	private EditText edtLteBandSearch = null;
	private TextView tvLteFreSearch = null;

	private MyTextWatcher lteFreUpTextWatch = null;
	private MyTextWatcher lteBandUpTextWatch = null;
	private MyTextWatcher lteFreDownTextWatch = null;
	private MyTextWatcher lteBandDownTextWatch = null;

	private MyTextWatcher gsmFreUpTextWatch = null;
	private MyTextWatcher gsmBandUpTextWatch = null;
	private MyTextWatcher gsmFreDownTextWatch = null;

	private MyTextWatcher lteBandSearchTextWatch = null;
	private MyTextWatcher _5gBandSearchTextWatch = null;

	private String[] ban_arr = SpeedTest5g.getContext().getResources().getStringArray(R.array.band_frequency_con);
	private ArrayList<String> ban_name = new ArrayList<String>();
	private String bandName;
	private List<String[]> bandSearch;//频段数组
	//LTE频率频点阈值
	private double maxFreDl,minFreDl,maxBandDl,minBandDl,maxFreUp,minFreUp,maxBandUp,minBandUp;
	private int lteBandPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_band_conversion_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;
		
		init("频率频点换算", true);
		initData();
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);

		llBandLte = (LinearLayout) findViewById(R.id.ll_band_lte);
		tvBandLte = (TextView) findViewById(R.id.tv_band_lte);
		edtLteFreUp = (EditText) findViewById(R.id.edt_lte_fre_up);
		edtLteBandUp = (EditText) findViewById(R.id.edt_lte_band_up);
		edtLteFreDown = (EditText) findViewById(R.id.edt_lte_fre_down);
		edtLteBandDown = (EditText) findViewById(R.id.edt_lte_band_down);

		tvLteFreUpRange = (TextView) findViewById(R.id.tv_lte_fre_up_range);
		tvLteBandUpRange = (TextView) findViewById(R.id.tv_lte_band_up_range);
		tvLteFreDownRange = (TextView) findViewById(R.id.tv_lte_fre_down_range);
		tvLteBandDownRange = (TextView) findViewById(R.id.tv_lte_band_down_range);

		tvBandGsm = (TextView) findViewById(R.id.tv_band_gsm);
		edtGsmFreUp = (EditText) findViewById(R.id.edt_gsm_fre_up);
		edtGsmFreDown = (EditText) findViewById(R.id.edt_gsm_fre_down);
		edtGsmBand = (EditText) findViewById(R.id.edt_gsm_band);

		tvLteBandSearch = (TextView) findViewById(R.id.tv_band_lte_search);
		edtLteBandSearch =(EditText) findViewById(R.id.edt_lte_band_search);
		tvLteFreSearch = (TextView) findViewById(R.id.tv_lte_fre_search);
		
		edt5gBand =(EditText) findViewById(R.id.edt_5g_band_search);
		tv5gFre = (TextView) findViewById(R.id.tv_5g_fre_search);

		lteFreUpTextWatch = new MyTextWatcher(R.id.edt_lte_fre_up);
		lteBandUpTextWatch = new MyTextWatcher(R.id.edt_lte_band_up);
		lteFreDownTextWatch = new MyTextWatcher(R.id.edt_lte_fre_down);
		lteBandDownTextWatch = new MyTextWatcher(R.id.edt_lte_band_down);

		gsmFreUpTextWatch = new MyTextWatcher(R.id.edt_gsm_fre_up);
		gsmBandUpTextWatch = new MyTextWatcher(R.id.edt_gsm_fre_down);
		gsmFreDownTextWatch = new MyTextWatcher(R.id.edt_gsm_band);

		lteBandSearchTextWatch = new MyTextWatcher(R.id.edt_lte_band_search);
		_5gBandSearchTextWatch = new MyTextWatcher(R.id.edt_5g_band_search);

		llBandLte.setOnClickListener(this);

		edtLteFreUp.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					edtLteFreUp.addTextChangedListener(lteFreUpTextWatch);
					edtLteFreUp.setText("");
					edtLteFreDown.setText("");
					edtLteBandDown.setText("");
				}else{
					edtLteFreUp.removeTextChangedListener(lteFreUpTextWatch);
				}
			}
		});

		edtLteBandUp.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					edtLteBandUp.addTextChangedListener(lteBandUpTextWatch);
					edtLteBandUp.setText("");
					edtLteFreDown.setText("");
					edtLteBandDown.setText("");
				}else{
					edtLteBandUp.removeTextChangedListener(lteBandUpTextWatch);
				}
			}
		});

		edtLteFreDown.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					edtLteFreDown.addTextChangedListener(lteFreDownTextWatch);
					edtLteFreDown.setText("");
					edtLteFreUp.setText("");
					edtLteBandUp.setText("");
				}else{
					edtLteFreDown.removeTextChangedListener(lteFreDownTextWatch);
				}
			}
		});

		edtLteBandDown.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					edtLteBandDown.addTextChangedListener(lteBandDownTextWatch);
					edtLteBandDown.setText("");
					edtLteFreUp.setText("");
					edtLteBandUp.setText("");
				}else{
					edtLteBandDown.removeTextChangedListener(lteBandDownTextWatch);
				}
			}
		});

		edtGsmFreUp.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					edtGsmFreUp.addTextChangedListener(gsmFreUpTextWatch);
					edtGsmFreUp.setText("");
				}else{
					edtGsmFreUp.removeTextChangedListener(gsmFreUpTextWatch);
				}
			}
		});

		edtGsmFreDown.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					edtGsmFreDown.addTextChangedListener(gsmBandUpTextWatch);
					edtGsmFreDown.setText("");
				}else{
					edtGsmFreDown.removeTextChangedListener(gsmBandUpTextWatch);
				}
			}
		});

		edtGsmBand.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					edtGsmBand.addTextChangedListener(gsmFreDownTextWatch);
					edtGsmBand.setText("");
				}else{
					edtGsmBand.removeTextChangedListener(gsmFreDownTextWatch);
				}
			}
		});

		edtLteBandSearch.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					edtLteBandSearch.addTextChangedListener(lteBandSearchTextWatch);
					edtLteBandSearch.setText("");
				}else{
					edtLteBandSearch.removeTextChangedListener(lteBandSearchTextWatch);
				}
			}
		});
		
		edt5gBand.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					edt5gBand.addTextChangedListener(_5gBandSearchTextWatch);
					edt5gBand.setText("");
				}else{
					edt5gBand.removeTextChangedListener(_5gBandSearchTextWatch);
				}
			}
		});
	}

	private void initData() {
		// LTE部分
		String[] initString = ban_arr[lteBandPosition].split(",");
		bandName = initString[0];
		minFreDl = Double.parseDouble(initString[1]);
		minBandDl = Double.parseDouble(initString[2]);
		maxBandDl = Double.parseDouble(initString[3]);
		maxFreDl = minFreDl + 0.1 * (maxBandDl - minBandDl);
		minFreUp = Double.parseDouble(initString[4]);
		minBandUp = Double.parseDouble(initString[5]);
		maxBandUp = Double.parseDouble(initString[6]);
		maxFreUp = minFreUp + 0.1 * (maxBandUp - minBandUp);

		bandSearch = new ArrayList<>();
		for (String temp : ban_arr) {
			String[] arr_temp = temp.split(",");
			ban_name.add(arr_temp[0]);
			bandSearch.add(arr_temp);
		}

		tvBandLte.setText(bandName);
		// LTE部分
//		edtLteFreUp.setText(String.valueOf(minFreUp));
//		edtLteFreDown.setText(String.valueOf(minFreDl));
//		double upband = 10 * (minFreUp - minFreUp) + minBandUp;
//		double dlband = 10 * (minFreDl - minFreDl) + minBandDl;
//		edtLteBandUp.setText(String.valueOf(Math.round(upband)));
//		edtLteBandDown.setText(String.valueOf(Math.round(dlband)));

		String rangeFreUp = minFreUp + "~" + maxFreUp;
		String rangeFreDl = minFreDl + "~" + maxFreDl;
		String rangeBandUp = Math.round(minBandUp) + "~" + Math.round(maxBandUp);
		String rangeBandDl = Math.round(minBandDl) + "~" + Math.round(maxBandDl);
		tvLteFreUpRange.setText(rangeFreUp);
		tvLteFreDownRange.setText(rangeFreDl);
		tvLteBandUpRange.setText(rangeBandUp);
		tvLteBandDownRange.setText(rangeBandDl);
	}

	private class MyTextWatcher implements TextWatcher{
		private int id;

		public MyTextWatcher(int id) {
			this.id = id;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (id == R.id.edt_lte_fre_up) {
                double strFreUp = UtilHandler.getInstance().toDouble(s.toString(), 0);
                if (strFreUp >= minFreUp && strFreUp <= maxFreUp) {
                    double strBandUp = 10 * (strFreUp - minFreUp) + minBandUp;
                    edtLteBandUp.setText(String.valueOf(Math.round(strBandUp)));
                } else if (strFreUp > maxFreUp) {
//					double strBandUp = 10 * (minFreUp - minFreUp) + minBandUp;
//					edtLteBandUp.setText(String.valueOf(Math.round(strBandUp)));
                    edtLteBandUp.setText("--");
                } else if (strFreUp < minFreUp) {
                    edtLteBandUp.setText("--");
                }
            } else if (id == R.id.edt_lte_band_up) {
                double strBandUp = UtilHandler.getInstance().toDouble(s.toString(), 0);
                if (strBandUp >= minBandUp && strBandUp <= maxBandUp) {
                    double freUp = minFreUp + 0.1 * (strBandUp - minBandUp);
                    edtLteFreUp.setText(UtilHandler.getInstance().toDfSum(freUp, "0") + "");
                } else if (strBandUp > maxBandUp) {
//					double freUp = minFreUp + 0.1 * (minBandUp - minBandUp);
//					edtLteFreUp.setText(UtilHandler.getInstance().toDfSum(freUp, "0")+"");
                    edtLteFreUp.setText("--");
                } else if (strBandUp < minBandUp) {
                    edtLteFreUp.setText("--");
                }
            } else if (id == R.id.edt_lte_fre_down) {
                double strFreDl = UtilHandler.getInstance().toDouble(s.toString(), 0);
                if (strFreDl >= minFreDl && strFreDl <= maxFreDl) {
                    double strBandDl = 10 * (strFreDl - minFreDl) + minBandDl;
                    edtLteBandDown.setText(String.valueOf(Math.round(strBandDl)));
                } else if (strFreDl > maxFreDl) {
//					double strBandDl = 10 * (minFreDl - minFreDl) + minBandDl;
//					edtLteBandDown.setText(String.valueOf(Math.round(strBandDl)));
                    edtLteBandDown.setText("--");
                } else if (strFreDl < minFreDl) {
                    edtLteBandDown.setText("--");
                }
            } else if (id == R.id.edt_lte_band_down) {
                double strBandDl = UtilHandler.getInstance().toDouble(s.toString(), 0);
                if (strBandDl >= minBandDl && strBandDl <= maxBandDl) {
                    double freDl = minFreDl + 0.1 * (strBandDl - minBandDl);
                    edtLteFreDown.setText(UtilHandler.getInstance().toDfSum(freDl, "0") + "");
                } else if (strBandDl > maxBandDl) {
//					double freDl = minFreDl + 0.1 * (minBandDl - minBandDl);
//					edtLteFreDown.setText(UtilHandler.getInstance().toDfSum(freDl, "0")+"");
                    edtLteFreDown.setText("--");
                } else if (strBandDl < minBandDl) {
                    edtLteFreDown.setText("--");
                }
            } else if (id == R.id.edt_gsm_fre_up) {
                setGsmData(id, UtilHandler.getInstance().toDouble(s.toString(), -1));
            } else if (id == R.id.edt_gsm_fre_down) {
                setGsmData(id, UtilHandler.getInstance().toDouble(s.toString(), -1));
            } else if (id == R.id.edt_gsm_band) {
                setGsmData(id, UtilHandler.getInstance().toInt(s.toString(), -1));
            } else if (id == R.id.edt_lte_band_search) {
                getBandSearch(s.toString());
            } else if (id == R.id.edt_5g_band_search) {
                get5gBandSearch(s.toString());
            }
		}

		@Override
		public void afterTextChanged(Editable s) {

		}

	}

	@Override
	public void onClick(View v) {
        if (v.getId() == R.id.ll_band_lte) {
            InputMethodUtil.getInstances().inputMethod(BandConversionActivity.this, v.getWindowToken());
            WheelOtherStrPopuo.getInstances().show(this, v, ban_name.toArray(new String[ban_name.size()]), "LTE频段", v.getId(), this, false).isPostion();
        }
	}

	private void setGsmData(int id,Object data){
        if (id == R.id.edt_gsm_fre_up) {
            double freUpData = (Double) data;
            if (freUpData >= 890.2 && freUpData <= 914.8) {
                //PGSM

                int band = (int) UtilHandler.getInstance().toDfSum((freUpData - 890) / 0.2, "");
                double freDown = UtilHandler.getInstance().toDfSum(935 + 0.2 * band, "0");
                edtGsmBand.setText(band + "");
                edtGsmFreDown.setText(freDown + "");
                tvBandGsm.setText("PGSM");
            } else if (freUpData >= 880.2 && freUpData <= 889.8) {
                //EGSM
                int band = (int) UtilHandler.getInstance().toDfSum(((freUpData - 880) / 0.2) + 974, "");
                double freDown = UtilHandler.getInstance().toDfSum(925 + 0.2 * (band - 974), "0");
                edtGsmBand.setText(band + "");
                edtGsmFreDown.setText(freDown + "");
                tvBandGsm.setText("EGSM");
            } else if (freUpData >= 1710.2 && freUpData <= 1784.8) {
                //DCS1800
                int band = (int) UtilHandler.getInstance().toDfSum(((freUpData - 1710) / 0.2) + 511, "");
                double freDown = UtilHandler.getInstance().toDfSum(1805 + 0.2 * (band - 511), "0");
                edtGsmBand.setText(band + "");
                edtGsmFreDown.setText(freDown + "");
                tvBandGsm.setText("DCS1800");
            } else {
                edtGsmBand.setText("--");
                edtGsmFreDown.setText("--");
                if (freUpData >= 0) {
                    tvBandGsm.setText("未使用");
                } else {
                    tvBandGsm.setText("--");
                }

            }
        } else if (id == R.id.edt_gsm_fre_down) {
            double freDownData = (Double) data;
            if (freDownData >= 935.2 && freDownData <= 959.8) {
                //PGSM
                int band = (int) UtilHandler.getInstance().toDfSum((freDownData - 935) / 0.2, "");
                double freUp = UtilHandler.getInstance().toDfSum(890 + 0.2 * band, "0");
                edtGsmBand.setText(band + "");
                edtGsmFreUp.setText(freUp + "");
                tvBandGsm.setText("PGSM");
            } else if (freDownData >= 925.2 && freDownData <= 934.8) {
                //EGSM
                int band = (int) UtilHandler.getInstance().toDfSum(((freDownData - 925) / 0.2) + 974, "");
                double freUp = UtilHandler.getInstance().toDfSum(880 + 0.2 * (band - 974), "0");
                edtGsmBand.setText(band + "");
                edtGsmFreUp.setText(freUp + "");
                tvBandGsm.setText("EGSM");
            } else if (freDownData >= 1805.2 && freDownData <= 1879.8) {
                //DCS1800
                int band = (int) UtilHandler.getInstance().toDfSum(((freDownData - 1805) / 0.2) + 511, "");
                ;
                double freUp = UtilHandler.getInstance().toDfSum(1710 + 0.2 * (band - 511), "0");
                edtGsmBand.setText(band + "");
                edtGsmFreUp.setText(freUp + "");
                tvBandGsm.setText("DCS1800");
            } else {
                edtGsmBand.setText("--");
                edtGsmFreUp.setText("--");
                if (freDownData >= 0) {
                    tvBandGsm.setText("未使用");
                } else {
                    tvBandGsm.setText("--");
                }
            }
        } else if (id == R.id.edt_gsm_band) {
            int bandData = (Integer) data;
            if (bandData >= 1 && bandData <= 124) {
                //PGSM
                double freUp = UtilHandler.getInstance().toDfSum(890 + 0.2 * bandData, "0");
                double freDown = UtilHandler.getInstance().toDfSum(935 + 0.2 * bandData, "0");
                edtGsmFreUp.setText(freUp + "");
                edtGsmFreDown.setText(freDown + "");
                tvBandGsm.setText("PGSM");
            } else if (bandData >= 975 && bandData <= 1023) {
                //EGSM
                double freUp = UtilHandler.getInstance().toDfSum(880 + 0.2 * (bandData - 974), "0");
                double freDown = UtilHandler.getInstance().toDfSum(925 + 0.2 * (bandData - 974), "0");
                edtGsmFreUp.setText(freUp + "");
                edtGsmFreDown.setText(freDown + "");
                tvBandGsm.setText("EGSM");
            } else if (bandData >= 512 && bandData <= 885) {
                //DCS1800
                double freUp = UtilHandler.getInstance().toDfSum(1710 + 0.2 * (bandData - 511), "0");
                double freDown = UtilHandler.getInstance().toDfSum(1805 + 0.2 * (bandData - 511), "0");
                edtGsmFreUp.setText(freUp + "");
                edtGsmFreDown.setText(freDown + "");
                tvBandGsm.setText("DCS1800");
            } else {
                edtGsmFreUp.setText("--");
                edtGsmFreDown.setText("--");
                if (bandData >= 0) {
                    tvBandGsm.setText("未使用");
                } else {
                    tvBandGsm.setText("--");
                }
            }
        }
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
        if (type == R.id.ll_band_lte) {
            lteBandPosition = (Integer) object;
            changeBand();
        }
	}

	private void changeBand() {
		String[] initString = ban_arr[lteBandPosition].split(",");
		bandName = initString[0];
		minFreDl = Double.parseDouble(initString[1]);
		minBandDl = Double.parseDouble(initString[2]);
		maxBandDl = Double.parseDouble(initString[3]);
		maxFreDl = minFreDl + 0.1 * (maxBandDl - minBandDl);
		DecimalFormat df = new DecimalFormat("#.0");
		maxFreDl = Double.parseDouble(df.format(maxFreDl));

		tvBandLte.setText(bandName);

//		edtLteFreDown.setText(String.valueOf(minFreDl));
//		double dlband = 10 * (minFreDl - minFreDl) + minBandDl;
//		edtLteBandDown.setText(String.valueOf(Math.round(dlband)));

		String rangeFreDl = minFreDl + "~" + maxFreDl;
		String rangeBandDl = Math.round(minBandDl) + "~" + Math.round(maxBandDl);
		tvLteFreDownRange.setText(rangeFreDl);
		tvLteBandDownRange.setText(rangeBandDl);
		// Band29上行显示为NA
		if (!bandName.equals("Band29")) {
			minFreUp = Double.parseDouble(initString[4]);
			minBandUp = Double.parseDouble(initString[5]);
			maxBandUp = Double.parseDouble(initString[6]);
			maxFreUp = minFreUp + 0.1 * (maxBandUp - minBandUp);
			maxFreUp = Double.parseDouble(df.format(maxFreUp));

//			double upband = 10 * (minFreUp - minFreUp) + minBandUp;
			String rangeFreUp = minFreUp + "~" + maxFreUp;
			String rangeBandUp = Math.round(minBandUp) + "~" + Math.round(maxBandUp);

			edtLteFreUp.setEnabled(true);
			edtLteBandUp.setEnabled(true);
//			edtLteFreUp.setText(String.valueOf(minFreUp));
//			edtLteBandUp.setText(String.valueOf(Math.round(upband)));
			tvLteFreUpRange.setText(rangeFreUp);
			tvLteBandUpRange.setText(rangeBandUp);
		} else {
			edtLteFreUp.setEnabled(false);
			edtLteBandUp.setEnabled(false);
			edtLteFreUp.setText("");
			edtLteBandUp.setText("");
			tvLteFreUpRange.setText("NA");
			tvLteBandUpRange.setText("NA");
		}
	}
	
	/**
	 * 5g频率速查
	 * @param data
	 */
	private void get5gBandSearch(String data){
		if(TextUtils.isEmpty(data)){
			tv5gFre.setText("--");
			return;
		}
		double value = 0;
		double _5gBandSearch = UtilHandler.getInstance().toDouble(data, -1);
		if(_5gBandSearch >= 0 && _5gBandSearch <= 599999){
			value = (_5gBandSearch - 0)*5/1000D + 0;
		}else if(_5gBandSearch >= 600000 && _5gBandSearch <= 2016666){
			value = (_5gBandSearch - 600000)*15/1000D + 3000;
		}else if(_5gBandSearch >= 2016667 && _5gBandSearch <= 3279167){
			value = (_5gBandSearch - 2016667)*60/1000D + 24250;
		}
		//取两位
		if(value != 0){
			value = UtilHandler.getInstance().toDfSum(value, "000");
			tv5gFre.setText(String.valueOf(value));
		}else{
			tv5gFre.setText("--");
		}
	}

	private void getBandSearch(String data){
		if(TextUtils.isEmpty(data)){
			tvLteBandSearch.setText("--");
			tvLteFreSearch.setText("--");
			return;
		}
		double lteBandSearch = UtilHandler.getInstance().toDouble(data, -1);
		for(String[] band:bandSearch){
			double Noffs_DL = UtilHandler.getInstance().toDfSum(UtilHandler.getInstance().toDouble(band[2], -1), "0"); 
			double Noffs_DL_MAX = UtilHandler.getInstance().toDfSum(UtilHandler.getInstance().toDouble(band[3], -1), "0"); 

			double Noffs_UL = UtilHandler.getInstance().toDfSum(UtilHandler.getInstance().toDouble(band[5], -1), "0"); 
			double Noffs_UL_MAX = UtilHandler.getInstance().toDfSum(UtilHandler.getInstance().toDouble(band[6], -1), "0"); 

			if(Noffs_DL<=lteBandSearch &&lteBandSearch<=Noffs_DL_MAX){
				double DL_low = UtilHandler.getInstance().toDfSum(UtilHandler.getInstance().toDouble(band[1], -1), "0");
				double freData = UtilHandler.getInstance().toDfSum((lteBandSearch-Noffs_DL)/10.0+DL_low, "0");
				tvLteFreSearch.setText(freData+"");
				tvLteBandSearch.setText(band[0]);
				if(band[0].equals("Band38 (D频)") ||band[0].equals("Band39 (F频)") ||band[0].equals("Band40 (E频)")){
//					getFreType(freData);
				}else 
					if(band[0].equals("Band33") ||band[0].equals("Band34 (A频)") ||band[0].equals("Band35")||
						band[0].equals("Band36") ||band[0].equals("Band37") ||band[0].equals("Band41 (D频)")||
						band[0].equals("Band42") ||band[0].equals("Band43") ||band[0].equals("Band44")
						||band[0].equals("D1 (Band38)")||band[0].equals("D2 (Band38)")||band[0].equals("D3 (Band41)")
						||band[0].equals("E1 (Band40)")||band[0].equals("E2 (Band40)")||band[0].equals("E3 (Band40)")
						||band[0].equals("F1 (Band39)")||band[0].equals("F2 (Band39)")||band[0].equals("F3 (Band39)")){
					//					tvLteBandSearch.setText(tvLteBandSearch.getText().toString());
				}else{
					tvLteBandSearch.setText(tvLteBandSearch.getText().toString()+"  下行");
				}

				return;
			}else if(Noffs_UL<=lteBandSearch &&lteBandSearch<=Noffs_UL_MAX){
				double UL_low = UtilHandler.getInstance().toDfSum(UtilHandler.getInstance().toDouble(band[4], -1), "0");
				double freData = UtilHandler.getInstance().toDfSum((lteBandSearch-Noffs_UL)/10.0+UL_low, "0");
				tvLteFreSearch.setText(freData+"");
				tvLteBandSearch.setText(band[0]);
				if(band[0].equals("Band38 (D频)") ||band[0].equals("Band39 (F频)") ||band[0].equals("Band40 (E频)")){
//					getFreType(freData);
				}else 
					if(band[0].equals("Band33") ||band[0].equals("Band34 (A频)") ||band[0].equals("Band35")||
						band[0].equals("Band36") ||band[0].equals("Band37") ||band[0].equals("Band41 (D频)")||
						band[0].equals("Band42") ||band[0].equals("Band43") ||band[0].equals("Band44")
						||band[0].equals("D1 (Band38)")||band[0].equals("D2 (Band38)")||band[0].equals("D3 (Band41)")
						||band[0].equals("E1 (Band40)")||band[0].equals("E2 (Band40)")||band[0].equals("E3 (Band40)")
						||band[0].equals("F1 (Band39)")||band[0].equals("F2 (Band39)")||band[0].equals("F3 (Band39)")){
					//					tvLteBandSearch.setText(tvLteBandSearch.getText().toString());
				}else{
					tvLteBandSearch.setText(tvLteBandSearch.getText().toString()+"  上行");
				}
				return ;
			}
		}

		tvLteBandSearch.setText("非规范频段");
		tvLteFreSearch.setText("--");
	}

//	private void getFreType(double data){
//		if(data>=1885 &&data<1905){
//			tvLteBandSearch.setText(tvLteBandSearch.getText().toString() +"  F1");
//		}else if(data>=1905 &&data<1915){
//			tvLteBandSearch.setText(tvLteBandSearch.getText().toString() +"  F2");
//		}else if(data>=2575 &&data<2595){
//			tvLteBandSearch.setText(tvLteBandSearch.getText().toString() +"  D1");
//		}else if(data>=2595 &&data<2615){
//			tvLteBandSearch.setText(tvLteBandSearch.getText().toString() +"  D2");
//		}else if(data>=2615 &&data<2635){
//			tvLteBandSearch.setText(tvLteBandSearch.getText().toString() +"  D3");
//		}else if(data>=2320 &&data<2340){
//			tvLteBandSearch.setText(tvLteBandSearch.getText().toString() +"  E1");
//		}else if(data>=2340 &&data<2360){
//			tvLteBandSearch.setText(tvLteBandSearch.getText().toString() +"  E2");
//		}else if(data>=2360 &&data<2370){
//			tvLteBandSearch.setText(tvLteBandSearch.getText().toString() +"  E3");
//		}
//	}


}
