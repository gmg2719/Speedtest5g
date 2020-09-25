package cn.nokia.speedtest5g.jzxh.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import cn.nokia.speedtest5g.jzxh.adapter.JzxhLteListAdapter;
import cn.nokia.speedtest5g.jzxh.ui.JzxhActivity;
import cn.nokia.speedtest5g.jzxh.ui.JzxhTabActivity;
import cn.nokia.speedtest5g.jzxh.util.JzxhNetRunnable;
import cn.nokia.speedtest5g.util.Base64Utils;
import cn.nokia.speedtest5g.util.TimeUtil;

/**
 * 基站信号最近数据列表
 * @author zwq
 *
 */
@SuppressLint("InflateParams") 
public class JzxhListFrament extends BaseHandlerFragment implements OnClickListener,ListenerBack{

	private final String ACTION_CLEAR_ONE = "cn.nokia.speedtest5g.jzxh.clear.one"; //清空卡槽1
	private final String ACTION_CLEAR_TWO = "cn.nokia.speedtest5g.jzxh.clear.two"; //清空卡槽2

	private ListView mListView;

	private JzxhLteListAdapter mAdapter;
	//当前LTE条数
	private TextView mTvCount;
	//可上传数据，最大存储1800条
	private List<Signal> mListDataSave = new ArrayList<>(); 
	//最大测试数量
	private final int MAX_TEST_SIGNAL = 1800;
	//当前上传的数据
	private StringBuffer mSbNetContent;
	//当前保存对象
	private LogInfo mLogInfo;
	//当前标题头配置
	private String[] mTagArr;

	private TextView mTvTime,mTvCi,mTvRsrp,mTvSinr,mTvPci,mTvRsrp2,mTvSinr2,mTvBand;
	private CheckBox mCkTime,mCkCi,mCkRsrp,mCkSinr,mCkPci,mCkRsrp2,mCkSinr2,mCkBand;

	private LinearLayout mLlContent = null;

	private ImageView mIvSettings = null;
	private ImageButton mIbUpload = null;

	private JzxhNetRunnable mJzxhNetRunnable;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.jzxh_frament_list, container, false);
		mTvCount  = (TextView) mView.findViewById(R.id.jzxhList_tv_count);
		mListView = (ListView) mView.findViewById(R.id.jzxhList_listview);
		mIbUpload = (ImageButton) mView.findViewById(R.id.jzxhList_ibtn_upload);
		mIvSettings = (ImageView) mView.findViewById(R.id.jzxhList_iv_settings);

		mAdapter  = new JzxhLteListAdapter(inflater);
		mListView.setAdapter(mAdapter);
		mIbUpload.setOnClickListener(this);
		mIvSettings.setOnClickListener(this);

		mLlContent = (LinearLayout) mView.findViewById(R.id.jzxhList_layout_tag);

		mCkTime = (CheckBox) mView.findViewById(R.id.jzxhList_ck_time);
		mCkCi = (CheckBox) mView.findViewById(R.id.jzxhList_ck_ci);
		mCkRsrp	= (CheckBox) mView.findViewById(R.id.jzxhList_ck_rsrp);
		mCkSinr	= (CheckBox) mView.findViewById(R.id.jzxhList_ck_sinr);
		mCkPci = (CheckBox) mView.findViewById(R.id.jzxhList_ck_pci);
		mCkRsrp2 = (CheckBox) mView.findViewById(R.id.jzxhList_ck_rsrp2);
		mCkSinr2 = (CheckBox) mView.findViewById(R.id.jzxhList_ck_sinr2);
		mCkBand	= (CheckBox) mView.findViewById(R.id.jzxhList_ck_band);

		mTvTime = (TextView) mView.findViewById(R.id.jzxhList_tv_time);
		mTvCi = (TextView) mView.findViewById(R.id.jzxhList_tv_ci);
		mTvRsrp = (TextView) mView.findViewById(R.id.jzxhList_tv_rsrp);
		mTvSinr = (TextView) mView.findViewById(R.id.jzxhList_tv_sinr);
		mTvPci = (TextView) mView.findViewById(R.id.jzxhList_tv_pci);
		mTvRsrp2 = (TextView) mView.findViewById(R.id.jzxhList_tv_rsrp2);
		mTvSinr2 = (TextView) mView.findViewById(R.id.jzxhList_tv_sinr2);
		mTvBand = (TextView) mView.findViewById(R.id.jzxhList_tv_band);

		mView.findViewById(R.id.jzxhList_btn_tagOk).setOnClickListener(this);

		String strTagTo = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().KEY_JZXH_LIST_TITLE_TAG, "1,1,1,1,1,0,0,0");
		mTagArr = strTagTo.split(",");
		updateTitleTag(mTagArr,true);
		return mView;
	}

	/**
	 * 
	 * @param arrTag
	 * @param isUpdate
	 */
	private void updateTitleTag(String[] arrTag,boolean isUpdate){
		if (arrTag == null || arrTag.length < 8) {
			return;
		}
		if (isUpdate) {
			mAdapter.updateTag(arrTag);
		}
		//time
		mCkTime.setChecked("1".equals(arrTag[0]));
		if (mCkTime.isChecked()) {
			mTvTime.setVisibility(View.VISIBLE);
		}else {
			mTvTime.setVisibility(View.GONE);
		}
		//CI
		mCkCi.setChecked("1".equals(arrTag[1]));
		if (mCkCi.isChecked()) {
			mTvCi.setVisibility(View.VISIBLE);
		}else {
			mTvCi.setVisibility(View.GONE);
		}
		//RSRP
		mCkRsrp.setChecked("1".equals(arrTag[2]));
		if (mCkRsrp.isChecked()) {
			mTvRsrp.setVisibility(View.VISIBLE);
		}else {
			mTvRsrp.setVisibility(View.GONE);
		}
		//SINR
		mCkSinr.setChecked("1".equals(arrTag[3]));
		if (mCkSinr.isChecked()) {
			mTvSinr.setVisibility(View.VISIBLE);
		}else {
			mTvSinr.setVisibility(View.GONE);
		}
		//PCI
		mCkPci.setChecked("1".equals(arrTag[4]));
		if (mCkPci.isChecked()) {
			mTvPci.setVisibility(View.VISIBLE);
		}else {
			mTvPci.setVisibility(View.GONE);
		}
		//RSRP2
		mCkRsrp2.setChecked("1".equals(arrTag[5]));
		if (mCkRsrp2.isChecked()) {
			mTvRsrp2.setVisibility(View.VISIBLE);
		}else {
			mTvRsrp2.setVisibility(View.GONE);
		}
		//SINR2
		mCkSinr2.setChecked("1".equals(arrTag[6]));
		if (mCkSinr2.isChecked()) {
			mTvSinr2.setVisibility(View.VISIBLE);
		}else {
			mTvSinr2.setVisibility(View.GONE);
		}
		//BAND
		mCkBand.setChecked("1".equals(arrTag[7]));
		if (mCkBand.isChecked()) {
			mTvBand.setVisibility(View.VISIBLE);
		}else {
			mTvBand.setVisibility(View.GONE);
		}
	}

	/**
	 * 添加信号
	 * @param signal
	 */
	public void addSignalItem(Signal signal){
		try{
			if (signal != null) {
				if (signal.getTypeNet().contains("LTE") || "NR".equals(signal.getTypeNet())) {
					mAdapter.addLteItem(signal);
					mTvCount.setText("NR/LTE最近" + mAdapter.getCount() + "条数据");
					mListDataSave.add(signal);
//					isListDataOverflow();
				}else if (signal.getTypeNet().contains("TD") || signal.getTypeNet().contains("GSM")) {
					mListDataSave.add(signal);
//					isListDataOverflow();
				}
			}
		}catch(Exception e){
		}
	}

	//超限提示
	private CommonDialog mToastOverflow = null;
	private MyEdgingDialog mMyEdgingDialog = null;
	//判断列表是否溢出
	private void isListDataOverflow(){
		//当前卡槽才判断是否超限
		if((((JzxhTabActivity)((JzxhActivity)getActivity()).getParent()).mPosition) ==
				((JzxhActivity)getActivity()).mSimPosition){
			if (mListDataSave.size() > MAX_TEST_SIGNAL) {
				if ((mMyEdgingDialog == null || !mMyEdgingDialog.isShowing()) && (mLoadingDialog == null || !mLoadingDialog.isShowing())) {
					if (mToastOverflow == null || !mToastOverflow.isShowing()) {
						mToastOverflow = new CommonDialog(getActivity());
						mToastOverflow.setListener(this);
						mToastOverflow.setButtonText("保存", "清除");
						String tip = ((JzxhActivity)getActivity()).mSimPosition == 0?"卡槽1":"卡槽2";
						mToastOverflow.show(tip + "当前测试数量已经超限,是否保存？", EnumRequest.DIALOG_TOAST_BTN_ONE.toInt());
					}
				}
				mListDataSave.remove(0);
			}
		}
	}

	@Override
	public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.jzxhList_ibtn_upload) {//保存上传按钮
            if (mListDataSave.size() <= 0) {
                showCommon("暂无信号数据，无法上传");
            } else {
                savaDataUpload();
            }
        } else if (id == R.id.jzxhList_iv_settings) { //设置
            if (mLlContent.getVisibility() == View.GONE) {
                mIvSettings.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_jzxh_nsa_quit));
                mIbUpload.setVisibility(View.GONE);
                mLlContent.setVisibility(View.VISIBLE);
            } else {
                mIvSettings.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_jzxh_nsa_setting));
                mIbUpload.setVisibility(View.VISIBLE);
                mLlContent.setVisibility(View.GONE);
            }
        } else if (id == R.id.jzxhList_btn_tagOk) {//邻区标签确定
            String[] arrStrSelect = new String[8];
            int isSelectCount = 0;
            //Time
            if (mCkTime.isChecked()) {
                arrStrSelect[0] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[0] = "0";
            }
            //CI
            if (mCkCi.isChecked()) {
                arrStrSelect[1] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[1] = "0";
            }
            //RSRP
            if (mCkRsrp.isChecked()) {
                arrStrSelect[2] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[2] = "0";
            }
            //Sinr
            if (mCkSinr.isChecked()) {
                arrStrSelect[3] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[3] = "0";
            }
            //PCI
            if (mCkPci.isChecked()) {
                arrStrSelect[4] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[4] = "0";
            }
            //Rsrp2
            if (mCkRsrp2.isChecked()) {
                arrStrSelect[5] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[5] = "0";
            }
            //Sinr2
            if (mCkSinr2.isChecked()) {
                arrStrSelect[6] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[6] = "0";
            }
            //BAND
            if (mCkBand.isChecked()) {
                arrStrSelect[7] = "1";
                isSelectCount += 1;
            } else {
                arrStrSelect[7] = "0";
            }
            if (isSelectCount < 4) {
                showCommon("选中个数不能小于4个");
                return;
            } else if (isSelectCount > 5) {
                showCommon("最多只支持5个参数同屏显示");
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
            SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().KEY_JZXH_LIST_TITLE_TAG, strTagTo);
            mTagArr = arrStrSelect;
            updateTitleTag(mTagArr, true);

			mIbUpload.setVisibility(View.VISIBLE);
			mLlContent.setVisibility(View.GONE);
			mIvSettings.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_jzxh_nsa_setting));
		}
	}

	/**
	 * 清空容器数据
	 */
	public void clearListData(){
		if(mListDataSave != null){
			mListDataSave.clear();
			if(mToastOverflow != null && mToastOverflow.isShowing()){
				mToastOverflow.dismiss();
				mToastOverflow = null;
			}
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
			//清空另外张卡数据
			Intent intent = null;
			if(((JzxhTabActivity)((JzxhActivity)getActivity()).getParent()).mPosition == 0){ //当前卡槽1
				intent = new Intent(ACTION_CLEAR_TWO);
			}else if(((JzxhTabActivity)((JzxhActivity)getActivity()).getParent()).mPosition == 1){ //当前卡槽2
				intent = new Intent(ACTION_CLEAR_ONE);
			}
			if(intent != null){
				getActivity().sendBroadcast(intent);
			}
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
						mSbNetContent = CsvUtil.getInstances().ExportSignalToCSV(mListDataSave, logName, logPath, false);
						// 已经写入cvs文件后清除收集的数据
						mListDataSave.clear();
						JzxhListFrament.this.getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// 这里做上传功能
								if (mSbNetContent != null && !mSbNetContent.toString().isEmpty()) {
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
		mToastOverflow.setListener(JzxhListFrament.this);
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
		// TODO Auto-generated method stub
		if (mJzxhNetRunnable != null) {
			mJzxhNetRunnable.close();
		}
		super.onDestroy();
	}
}
