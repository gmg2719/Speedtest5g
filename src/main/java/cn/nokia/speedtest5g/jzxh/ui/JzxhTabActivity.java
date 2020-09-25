package cn.nokia.speedtest5g.jzxh.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.fjmcc.wangyoubao.app.signal.MyServiceSignal;
import com.fjmcc.wangyoubao.app.signal.NetWorkStateUtil;
import com.fjmcc.wangyoubao.app.signal.bean.GSMCell;
import com.fjmcc.wangyoubao.app.signal.bean.LTECell;
import com.fjmcc.wangyoubao.app.signal.bean.NrCell;

import java.util.ArrayList;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.activity.MyTabActivity;
import cn.nokia.speedtest5g.gis.activity.LayerDetailActivity;
import cn.nokia.speedtest5g.gis.activity.LayerDetailGsmActivity;
import cn.nokia.speedtest5g.gis.model.WqGisLayer;
import cn.nokia.speedtest5g.gis.model.WqGisLayerInfo;
import cn.nokia.speedtest5g.gis.util.WQ_GisDbHandler;

/**
 * 基站信号TAB页
 * @author zwq
 *
 */
public class JzxhTabActivity extends MyTabActivity {

	public int mPosition = 0; //当前卡槽位置 默认0
	private RadioButton mRbSim1,mRbSim2;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		init("基站信号", true);
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		RadioGroup mRadioGroup = (RadioGroup)findViewById(R.id.jzxhSuperHome_tab_rg);
		if (getClassName().length > 1) {
			showMyDialog();
			mRadioGroup.setOnCheckedChangeListener(checkedChangeListener);
			mRbSim1 = (RadioButton) findViewById(R.id.jzxhSuperHome_rb_sim1);
			mRbSim2	= (RadioButton) findViewById(R.id.jzxhSuperHome_rb_sim2);
			mRbSim2.setChecked(true);
			actionBar.setTitleViewClickListener();
			mRbSim1.postDelayed(new Runnable() {

				@Override
				public void run() {
					mRbSim1.setChecked(true);
					dismissMyDialog();
				}
			}, 500);
		}else {
			mRadioGroup.setVisibility(View.GONE);
		}
	}

	@Override
	public int getLayout() {
		return R.layout.jzxh_super_home;
	}

	@Override
	public String[] getClassName() {
		if (MyServiceSignal.isMoreSim(this)) {
			return new String[] {
					"cn.nokia.speedtest5g.jzxh.ui.JzxhSim1Activity",
					"cn.nokia.speedtest5g.jzxh.ui.JzxhSim2Activity"
			};
		}else {
			return new String[] {"cn.nokia.speedtest5g.jzxh.ui.JzxhSim1Activity"};
		}
	}

	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {
			actionBar.getTitileView().setText("基站信号");
            if (arg1 == R.id.jzxhSuperHome_rb_sim1) {//基站信号卡1
                mPosition = 0;
                setTag("JzxhSim1Activity", false);
                mRbSim1.setTextSize(TypedValue.COMPLEX_UNIT_PX, SpeedTest5g.getContext().getResources().getDimension(R.dimen.tv_half_size));
                mRbSim1.getPaint().setFakeBoldText(true);
                mRbSim1.setTextColor(getResources().getColor(R.color.white_edeeee));
                mRbSim1.setBackgroundResource(R.drawable.drawable_ftp_switch_model_btn_left_select);
                mRbSim2.setTextSize(TypedValue.COMPLEX_UNIT_PX, SpeedTest5g.getContext().getResources().getDimension(R.dimen.tv_small_size));
                mRbSim2.getPaint().setFakeBoldText(false);
                mRbSim2.setTextColor(getResources().getColor(R.color.gray_c0c0c3));
                mRbSim2.setBackgroundResource(R.drawable.drawable_ftp_switch_model_btn_right);
            } else if (arg1 == R.id.jzxhSuperHome_rb_sim2) {//基站信号卡2
                mPosition = 1;
                setTag("JzxhSim2Activity", false);
                mRbSim1.setTextSize(TypedValue.COMPLEX_UNIT_PX, SpeedTest5g.getContext().getResources().getDimension(R.dimen.tv_small_size));
                mRbSim1.getPaint().setFakeBoldText(false);
                mRbSim1.setTextColor(getResources().getColor(R.color.gray_c0c0c3));
                mRbSim1.setBackgroundResource(R.drawable.drawable_ftp_switch_model_btn_left);
                mRbSim2.setTextSize(TypedValue.COMPLEX_UNIT_PX, SpeedTest5g.getContext().getResources().getDimension(R.dimen.tv_half_size));
                mRbSim2.getPaint().setFakeBoldText(true);
                mRbSim2.setTextColor(getResources().getColor(R.color.white_edeeee));
                mRbSim2.setBackgroundResource(R.drawable.drawable_ftp_switch_model_btn_right_select);
            }
		}
	};

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		//标题事件
		if (type == R.id.actionbar_title) {
			if (actionBar.getTitileView().getText().toString().length() > 0 && !actionBar.getTitileView().getText().toString().equals("基站信号")) {
				Integer ids = (Integer)actionBar.getTitileView().getTag(R.id.idIds);
				if (ids > 0) {
					WqGisLayerInfo mWqGisLayerInfo = new WqGisLayerInfo();
					mWqGisLayerInfo.setList(new ArrayList<WqGisLayer>());
					WqGisLayer mWqGisLayer = new WqGisLayer();
					mWqGisLayer.set_id(ids);
					String strNet = actionBar.getTitileView().getTag(R.id.idType).toString();
					if ("LTE".equals(strNet)) {
						mWqGisLayer.setTabName(WQ_GisDbHandler.getIntances().getTABLE_LTE_CELL());
						mWqGisLayer.setType(2);
					}else if ("GSM".equals(strNet)) {
						mWqGisLayer.setTabName(WQ_GisDbHandler.getIntances().getTABLE_GSM_CELL());
						mWqGisLayer.setType(0);
					}else if ("NR".equals(strNet)) {
						mWqGisLayer.setTabName(WQ_GisDbHandler.getIntances().getTABLE_NR_CELL());
						mWqGisLayer.setType(26);
					}else {
						mWqGisLayer.setTabName(WQ_GisDbHandler.getIntances().getTABLE_TDS_CELL());
						mWqGisLayer.setType(1);
					}
					mWqGisLayerInfo.getList().add(mWqGisLayer);
					Intent intent = new Intent(this, "LTE".equals(strNet) ? LayerDetailActivity.class : LayerDetailGsmActivity.class);
					intent.putExtra("data", mWqGisLayerInfo);
					startActivity(intent);
				}
			}
		}
	};

	@Override
	public void initStatistics() {
		installStatistics(R.string.code_jzxh);
	}

	/**
	 * 设置基站名称
	 * @param obj
	 * @param positionSim
	 */
	public void setCellName(Object obj, int positionSim){
		if ((mRbSim1.isChecked() && positionSim == 0) || (mRbSim2.isChecked() && positionSim == 1)) {
			if (obj != null && obj instanceof GSMCell) {
				GSMCell mGSMCell = (GSMCell) obj;
				if (!actionBar.getTitileView().getText().toString().equals(mGSMCell.name)) {
					if (!TextUtils.isEmpty(mGSMCell.name)) {
						actionBar.getTitileView().setText(mGSMCell.name);
						actionBar.getTitileView().setTag(R.id.idIds, mGSMCell.asu);
						actionBar.getTitileView().setTag(R.id.idType, "GSM");
					}else {
						actionBar.getTitileView().setText("基站信号");
					}
				}
			}else if (obj != null && obj instanceof LTECell) {
				LTECell mLTECell = (LTECell) obj;
				if (!actionBar.getTitileView().getText().toString().equals(mLTECell.name)) {
					if (!TextUtils.isEmpty(mLTECell.name)) {
						actionBar.getTitileView().setText(mLTECell.name);
						actionBar.getTitileView().setTag(R.id.idIds, mLTECell.asu);
						actionBar.getTitileView().setTag(R.id.idType, "LTE");
					}else {
						actionBar.getTitileView().setText("基站信号");
					}
				}
			}else if (obj != null && obj instanceof NrCell) {
				NrCell mNrCell = (NrCell) obj;
				if (!actionBar.getTitileView().getText().toString().equals(mNrCell.name)) {
					if (!TextUtils.isEmpty(mNrCell.name)) {
						actionBar.getTitileView().setText(mNrCell.name);
						actionBar.getTitileView().setTag(R.id.idIds, mNrCell.db_id);
						actionBar.getTitileView().setTag(R.id.idType, "NR");
					}else {
						actionBar.getTitileView().setText("基站信号");
					}
				}
			}else {
				actionBar.getTitileView().setText("基站信号");
			}
		}
	}

	/**
	 * 设置接收信号运营商类型
	 */
	public synchronized void setCellType(String mnc,int positionSim) {
		if (!TextUtils.isEmpty(mnc)) {
			if (positionSim == 0) {
				if (mnc.equals(mRbSim1.getTag(R.id.idObj))) {
					return;
				}
			}else {
				if (mnc.equals(mRbSim2.getTag(R.id.idObj))) {
					return;
				}
			}
		}
		Drawable drawable;
		if (NetWorkStateUtil.isCmccNet(mnc) == 0) {
			drawable = getResources().getDrawable(R.drawable.icon_jzxh_yd);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		} else if (NetWorkStateUtil.isCmccNet(mnc) == 1) {
			drawable = getResources().getDrawable(R.drawable.icon_jzxh_dx);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		} else if (NetWorkStateUtil.isCmccNet(mnc) == 2) {
			drawable = getResources().getDrawable(R.drawable.icon_jzxh_lt);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		} else {
			drawable = getResources().getDrawable(R.drawable.icon_jzxh_other);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		}
		if (positionSim == 0) {
			mRbSim1.setTag(R.id.idObj, mnc);
			mRbSim1.setCompoundDrawables(null, null, drawable, null);
		}else {
			mRbSim2.setTag(R.id.idObj, mnc);
			mRbSim2.setCompoundDrawables(null, null, drawable, null);
		}
	}
}
