package cn.nokia.speedtest5g.jzxh.ui.nsa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.util.JsonHandler;
import com.android.volley.util.SharedPreHandler;

import java.util.ArrayList;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity.BaseHandlerFragment;
import cn.nokia.speedtest5g.app.bean.LogInfo;
import cn.nokia.speedtest5g.app.bean.Signal;
import cn.nokia.speedtest5g.app.db.DbHandler;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.respon.BaseRespon;
import cn.nokia.speedtest5g.app.uitl.PathUtil;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.dialog.MyEdgingDialog;
import cn.nokia.speedtest5g.gis.util.CsvUtil;
import cn.nokia.speedtest5g.jzxh.adapter.JzxhNsaListAdapter;
import cn.nokia.speedtest5g.jzxh.util.JzxhNetRunnable;
import cn.nokia.speedtest5g.util.Base64Utils;
import cn.nokia.speedtest5g.util.TimeUtil;

/**
 * 基站信号nsa最近数据列表
 * @author JQJ
 *
 */
@SuppressLint("InflateParams") 
public class JzxhNsaListFrament extends BaseHandlerFragment implements OnClickListener,ListenerBack{

	private final String ACTION_CLEAR_ALL = "cn.nokia.speedtest5g.jzxh.clear.all";

	private ListView mListView = null;

	//当前LTE条数
	private TextView mTvCount = null;
	//可上传数据，最大存储1800条
	private List<Signal> mListDataSave = new ArrayList<>(); 
	//最大测试数量
	private final int MAX_TEST_SIGNAL = 1800;
	//当前上传的数据
	private StringBuffer mSbNetContent = null;
	//当前保存对象
	private LogInfo mLogInfo = null;
	//超限提示
	private CommonDialog mToastOverflow = null;
	private MyEdgingDialog mMyEdgingDialog = null;

	//当前标签
	private String[] mLqTagArr = null;
	//邻区列表适配器
	private JzxhNsaListAdapter mNrListAdapter = null;

	//数据标签-Time, CI, PCI, RSRP, SINR, BAND, PCI_NR, RSRP_NR, SINR_NR, BAND_NR
	private TextView mTvLqTime,mTvLqCi,mTvLqPci,mTvLqRsrp,mTvLqSinr,mTvLqBand,mTvLqPciNr,mTvLqRsrpNr,mTvLqSinrNr,mTvLqBandNr;
	//无邻区数据，选择标签布局2
	private View mViewLqNodata,mViewLqTag2;
	//数据标签选择-Time, CI, PCI, RSRP, SINR, BAND, PCI_NR, RSRP_NR, SINR_NR, BAND_NR
	private CheckBox mCkLqTime,mCkLqCi,mCkLqPci,mCkLqRsrp,mCkLqSinr,mCkLqBand,mCkLqPciNr,mCkLqRsrpNr,mCkLqSinrNr,mCkLqBandNr;

	private ImageButton mIbUpload = null;
	private JzxhNetRunnable mJzxhNetRunnable = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.jqj_jzxh_nsa_list_frament, container, false);
		mTvCount  = (TextView) mView.findViewById(R.id.jzxh_nsa_list_tv_count);
		mListView = (ListView) mView.findViewById(R.id.jzxh_nsa_list_listview);
		mNrListAdapter  = new JzxhNsaListAdapter(inflater, getActivity());
		mListView.setAdapter(mNrListAdapter);
		mIbUpload = (ImageButton) mView.findViewById(R.id.jzxh_nsa_list_ibtn_upload);
		mIbUpload.setOnClickListener(this);

		mTvLqTime     = (TextView) mView.findViewById(R.id.jzxh_nsa_list_tv_lq_time);
		mTvLqCi       = (TextView) mView.findViewById(R.id.jzxh_nsa_list_tv_lq_ci);
		mTvLqPci      = (TextView) mView.findViewById(R.id.jzxh_nsa_list_tv_lq_pci);
		mTvLqRsrp     = (TextView) mView.findViewById(R.id.jzxh_nsa_list_tv_lq_rsrp);
		mTvLqSinr     = (TextView) mView.findViewById(R.id.jzxh_nsa_list_tv_lq_sinr);
		mTvLqBand     = (TextView) mView.findViewById(R.id.jzxh_nsa_list_tv_lq_band);
		mTvLqPciNr    = (TextView) mView.findViewById(R.id.jzxh_nsa_list_tv_lq_pci_nr);
		mTvLqRsrpNr   = (TextView) mView.findViewById(R.id.jzxh_nsa_list_tv_lq_rsrp_nr);
		mTvLqSinrNr   = (TextView) mView.findViewById(R.id.jzxh_nsa_list_tv_lq_sinr_nr);
		mTvLqBandNr   = (TextView) mView.findViewById(R.id.jzxh_nsa_list_tv_lq_band_nr);

		mViewLqNodata = mView.findViewById(R.id.jzxh_nsa_list_tv_lq_nodata);
		mListView.setEmptyView(mViewLqNodata);
		mViewLqTag2   = mView.findViewById(R.id.jzxh_nsa_list_layout_lq_tag);

		mCkLqTime     = (CheckBox) mView.findViewById(R.id.jzxh_nsa_list_ck_lq_time);
		mCkLqCi       = (CheckBox) mView.findViewById(R.id.jzxh_nsa_list_ck_lq_ci);
		mCkLqPci      = (CheckBox) mView.findViewById(R.id.jzxh_nsa_list_ck_lq_pci);
		mCkLqRsrp     = (CheckBox) mView.findViewById(R.id.jzxh_nsa_list_ck_lq_rsrp);
		mCkLqSinr     = (CheckBox) mView.findViewById(R.id.jzxh_nsa_list_ck_lq_sinr);
		mCkLqBand     = (CheckBox) mView.findViewById(R.id.jzxh_nsa_list_ck_lq_band);
		mCkLqPciNr    = (CheckBox) mView.findViewById(R.id.jzxh_nsa_list_ck_lq_pci_nr);
		mCkLqRsrpNr   = (CheckBox) mView.findViewById(R.id.jzxh_nsa_list_ck_lq_rsrp_nr);
		mCkLqSinrNr   = (CheckBox) mView.findViewById(R.id.jzxh_nsa_list_ck_lq_sinr_nr);
		mCkLqBandNr   = (CheckBox) mView.findViewById(R.id.jzxh_nsa_list_ck_lq_band_nr);

		mView.findViewById(R.id.jzxh_nsa_list_btn_lq_tagAdd).setOnClickListener(this);
		mView.findViewById(R.id.jzxh_nsa_list_btn_lq_tagOk).setOnClickListener(this);

		String strTagTo = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().KEY_NSA_DATA_TAG, "1,1,1,1,1,0,0,0,0,0");
		mLqTagArr = strTagTo.split(",");
		updateLqTag(mLqTagArr,true);

		return mView;
	}

	/**
	 * 添加信号
	 * @param signal
	 */
	public void addSignalItem(Signal signal){
		if (signal != null) {
			if ("NR".equals(signal.getTypeNet()) || "LTE".equals(signal.getTypeNet())) {
				mNrListAdapter.addNrItem(signal);
				mTvCount.setText("最近" + mNrListAdapter.getCount() + "条数据");
				mListDataSave.add(signal);
//				isListDataOverflow();
			}
		}
	}

	//判断列表是否溢出
	private void isListDataOverflow(){
		if (mListDataSave.size() > MAX_TEST_SIGNAL) {
			if ((mMyEdgingDialog == null || !mMyEdgingDialog.isShowing()) && (mLoadingDialog == null || !mLoadingDialog.isShowing())) {
				if (mToastOverflow == null || !mToastOverflow.isShowing()) {
					mToastOverflow = new CommonDialog(getActivity());
					mToastOverflow.setListener(this);
					mToastOverflow.setButtonText("保存", "清除");
					mToastOverflow.show("当前测试数量已经超限,是否保存？", EnumRequest.DIALOG_TOAST_BTN_ONE.toInt());
				}
			}
			mListDataSave.remove(0);
		}
	}

	@Override
	public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.jzxh_nsa_list_ibtn_upload) {//保存上传按钮
            if (mListDataSave.size() <= 0) {
                showCommon("暂无信号数据，无法上传");
            } else {
                savaDataUpload();
            }
        } else if (id == R.id.jzxh_nsa_list_btn_lq_tagAdd) {//标签选择
            mViewLqTag2.setVisibility(View.VISIBLE);
            mIbUpload.setVisibility(View.INVISIBLE);
        } else if (id == R.id.jzxh_nsa_list_btn_lq_tagOk) {//邻区标签确定
            String[] arrStrSelect = new String[10];
            int isSelectCount = 0;
            //Time
            if (mCkLqTime.isChecked()) {
                arrStrSelect[0] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[0] = "0";
            }
            //CI
            if (mCkLqCi.isChecked()) {
                arrStrSelect[1] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[1] = "0";
            }
            //PCI
            if (mCkLqPci.isChecked()) {
                arrStrSelect[2] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[2] = "0";
            }
            //RSRP
            if (mCkLqRsrp.isChecked()) {
                arrStrSelect[3] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[3] = "0";
            }
            //SINR
            if (mCkLqSinr.isChecked()) {
                arrStrSelect[4] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[4] = "0";
            }
            //BAND
            if (mCkLqBand.isChecked()) {
                arrStrSelect[5] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[5] = "0";
            }
            //PCI_NR
            if (mCkLqPciNr.isChecked()) {
                arrStrSelect[6] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[6] = "0";
            }
            //RSRP_NR
            if (mCkLqRsrpNr.isChecked()) {
                arrStrSelect[7] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[7] = "0";
            }
            //SINR_NR
            if (mCkLqSinrNr.isChecked()) {
                arrStrSelect[8] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[8] = "0";
            }
            //BAND_NR
            if (mCkLqBandNr.isChecked()) {
                arrStrSelect[9] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[9] = "0";
            }

            if (isSelectCount < 3) {
                showCommon("选中个数不能小于3个");
                return;
            }

            if (isSelectCount > 5) {
                showCommon("选中个数不能大于5个");
                return;
            }

            String strTagTo = "";
            for (int i = 0; i < arrStrSelect.length; i++) {
                if (i != 0) {
                    strTagTo += "," + arrStrSelect[i];
                } else {
                    strTagTo += arrStrSelect[i];
                }
            }
            SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().KEY_NSA_DATA_TAG, strTagTo);
            mLqTagArr = arrStrSelect;
            updateLqTag(mLqTagArr, true);
            mViewLqTag2.setVisibility(View.GONE);
            mIbUpload.setVisibility(View.VISIBLE);
        }
	}

	/**
	 * 0false 1true
	 * @param arrTag Time, CI, PCI, RSRP, SINR, BAND, PCI_NR, RSRP_NR, SINR_NR, BAND_NR
	 */
	private void updateLqTag(String[] arrTag, boolean isUpdate){
		if (arrTag == null || arrTag.length < 10) {
			return;
		}
		if (isUpdate) {
			mNrListAdapter.updateTag(arrTag);
		}
		//Time
		mCkLqTime.setChecked("1".equals(arrTag[0]));
		if (mCkLqTime.isChecked()) {
			mTvLqTime.setVisibility(View.VISIBLE);
		}else {
			mTvLqTime.setVisibility(View.GONE);
		}
		//CI
		mCkLqCi.setChecked("1".equals(arrTag[1]));
		if (mCkLqCi.isChecked()) {
			mTvLqCi.setVisibility(View.VISIBLE);
		}else {
			mTvLqCi.setVisibility(View.GONE);
		}
		//PCI
		mCkLqPci.setChecked("1".equals(arrTag[2]));
		if (mCkLqPci.isChecked()) {
			mTvLqPci.setVisibility(View.VISIBLE);
		}else {
			mTvLqPci.setVisibility(View.GONE);
		}
		//RSRP
		mCkLqRsrp.setChecked("1".equals(arrTag[3]));
		if (mCkLqRsrp.isChecked()) {
			mTvLqRsrp.setVisibility(View.VISIBLE);
		}else {
			mTvLqRsrp.setVisibility(View.GONE);
		}
		//SINR
		mCkLqSinr.setChecked("1".equals(arrTag[4]));
		if (mCkLqSinr.isChecked()) {
			mTvLqSinr.setVisibility(View.VISIBLE);
		}else {
			mTvLqSinr.setVisibility(View.GONE);
		}
		//BAND
		mCkLqBand.setChecked("1".equals(arrTag[5]));
		if (mCkLqBand.isChecked()) {
			mTvLqBand.setVisibility(View.VISIBLE);
		}else {
			mTvLqBand.setVisibility(View.GONE);
		}
		//PCI_NR
		mCkLqPciNr.setChecked("1".equals(arrTag[6]));
		if (mCkLqPciNr.isChecked()) {
			mTvLqPciNr.setVisibility(View.VISIBLE);
		}else {
			mTvLqPciNr.setVisibility(View.GONE);
		}
		//RSRP_NR
		mCkLqRsrpNr.setChecked("1".equals(arrTag[7]));
		if (mCkLqRsrpNr.isChecked()) {
			mTvLqRsrpNr.setVisibility(View.VISIBLE);
		}else {
			mTvLqRsrpNr.setVisibility(View.GONE);
		}
		//SINR_NR
		mCkLqSinrNr.setChecked("1".equals(arrTag[8]));
		if (mCkLqSinrNr.isChecked()) {
			mTvLqSinrNr.setVisibility(View.VISIBLE);
		}else {
			mTvLqSinrNr.setVisibility(View.GONE);
		}
		//BAND_NR
		mCkLqBandNr.setChecked("1".equals(arrTag[9]));
		if (mCkLqBandNr.isChecked()) {
			mTvLqBandNr.setVisibility(View.VISIBLE);
		}else {
			mTvLqBandNr.setVisibility(View.GONE);
		}
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		//测试数量超限提示
		if (type == EnumRequest.DIALOG_TOAST_BTN_ONE.toInt()) {
			if (isTrue) {
				savaDataUpload();
			}else {
				mListDataSave.clear();
			}
			//清空基站信号列表数据
			Intent intent = new Intent(ACTION_CLEAR_ALL);
			getActivity().sendBroadcast(intent);
			//保存数据对话框选择返回	
		}else if (type == EnumRequest.DIALOG_TOAST_BTN_TWO.toInt()) {
			if (isTrue) {
				showDialog();
				final String logStart = ((EditText) object).getText().toString();
				new Thread(new Runnable() {

					@Override
					public void run() {
						String testNetTypy = mListDataSave.get(mListDataSave.size() - 1).getTypeNet();
						String logName = logStart + "_" + testNetTypy + "_" + TimeUtil.getInstance().getNowTimeYear() + ".csv";
						String logPath = PathUtil.getInstances().getCurrentPath() + PathUtil.getInstances().SIGNAL_PATH;

						// 保存数据库
						mLogInfo = new LogInfo();
						mLogInfo.setTimes(TimeUtil.getInstance().getNowTimeGis());
						mLogInfo.setUserId(getUserID());
						mLogInfo.setUserPhone(Base64Utils.decryptorDes3(SharedPreHandler.getShared(SpeedTest5g.getContext())
								.getStringShared(TypeKey.getInstance().USER_PHONE(), "")));
						//0LET 1TD 2GSM
						mLogInfo.setNetType(("NR".equals(testNetTypy) || "LTE".equals(testNetTypy)) ? 0 : "TD".equals(testNetTypy) ? 1 : 2);
						mLogInfo.setTestType(1);
						// 0LTE 1TD 2GSM
						mLogInfo.setLogName(logName);
						DbHandler.getInstance().insert(mLogInfo);
						// 保存本地文件
						mSbNetContent = CsvUtil.getInstances().ExportSignalToCSV(mListDataSave, logName, logPath, true);
						// 已经写入cvs文件后清除收集的数据
						mListDataSave.clear();
						JzxhNsaListFrament.this.getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// 这里做上传功能
								if (!TextUtils.isEmpty(mSbNetContent)) {
									showUploadErr("本地保存成功,是否上传至服务器?");
								}
								dismissDialog();
							}
						});
					}
				}).start();
			}
			//上传选择对话框	
		}else if (type == EnumRequest.DIALOG_TOAST_BTN_THREE.toInt()) {
			if (isTrue) {
				showDialog();
				if (mJzxhNetRunnable != null) {
					mJzxhNetRunnable.close();
				}
				mJzxhNetRunnable = new JzxhNetRunnable(mHandler, EnumRequest.NET_GIS_SIGNAL.toInt(), mSbNetContent.toString());
				new Thread(mJzxhNetRunnable).start();
			}else {
				mSbNetContent.setLength(0);
				mSbNetContent = null;
			}
		}
	}

	//上传失败提示
	private void showUploadErr(String msg) {
		mToastOverflow = new CommonDialog(getActivity(),false);
		mToastOverflow.setListener(JzxhNsaListFrament.this);
		mToastOverflow.setButtonText("立即上传", "稍后上传");
		mToastOverflow.show(msg, EnumRequest.DIALOG_TOAST_BTN_THREE.toInt());
	}

	//保存并上传数据
	private void savaDataUpload(){
		EditText et = (EditText) getActivity().getLayoutInflater().inflate(R.layout.wq_dialog_edittext,null);
		et.setInputType(InputType.TYPE_CLASS_TEXT);
		et.setHint("请输入日志名称");
		et.setText("signalLog");
		et.setSelection(et.getText().length());
		mMyEdgingDialog = new MyEdgingDialog(getActivity()).setListener(this);
		mMyEdgingDialog.setButtonText("保存", "取消");
		mMyEdgingDialog.show(et, EnumRequest.DIALOG_TOAST_BTN_TWO.toInt());
	}

	@Override
	public void onHandleMessage(MyEvents events) {
		switch (events.getMode()) {
		case NETWORK:
			//数据上传返回	
			if (events.getType() == EnumRequest.NET_GIS_SIGNAL.toInt()) {
				// 上传成功.
				if (events.isOK()) {
					BaseRespon info = JsonHandler.getHandler().getTarget(events.getObject().toString(), BaseRespon.class);
					if (info != null && info.isRs()) {
						showCommon("上传成功");
						new Thread(new Runnable() {

							@SuppressWarnings("unchecked")
							@Override
							public void run() {
								// 上传成功，更新数据库状态
								ArrayList<LogInfo> queryObjLogInfo = (ArrayList<LogInfo>) DbHandler.getInstance().queryObj(LogInfo.class, "logName=? AND times=?", new String[] {mLogInfo.getLogName(),mLogInfo.getTimes()});
								if (queryObjLogInfo != null && queryObjLogInfo.size() > 0) {
									mLogInfo = queryObjLogInfo.get(0);
									mLogInfo.setState(1);
									DbHandler.getInstance().updateObj(mLogInfo);
								}
								// 上传成功后清除要上传的缓存
								mSbNetContent.setLength(0);
								mSbNetContent = null;
							}
						}).start();
					} else {
						showUploadErr("上传失败,是否重新上传?");
					}
					// 上传失败
				} else {
					showUploadErr("上传失败,是否重新上传?");
				}
				dismissDialog();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		if (mJzxhNetRunnable != null) {
			mJzxhNetRunnable.close();
		}
		super.onDestroy();
	}
}
