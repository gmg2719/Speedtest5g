package cn.nokia.speedtest5g.jzxh.ui;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.volley.util.JsonHandler;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.emergent.adapter.JJ_EmergentQuotaDetailAdapter;
import cn.nokia.speedtest5g.emergent.request.JJ_RequestGetEmergencyCiParam;
import cn.nokia.speedtest5g.emergent.response.JJ_EmergentQuotaCellDetailInfo;
import cn.nokia.speedtest5g.emergent.response.JJ_EmergentQuotaCellInfo;
import cn.nokia.speedtest5g.emergent.response.JJ_EmergentQuotaResponse;
import cn.nokia.speedtest5g.jzxh.response.JzxhXqzbData;
import cn.nokia.speedtest5g.util.TimeUtil;
import cn.nokia.speedtest5g.view.JJ_LineCharView;
import cn.nokia.speedtest5g.view.MyScrollyListView;

/**
 * 基站信号-指标-分时指标
 * @author zwq
 *
 */
public class JzxhIndexDetailsActivity extends BaseActionBarActivity {

	private TextView mTvTitle,mTvName;
	
	private FrameLayout mLayoutChart;
	
	private MyScrollyListView mListView;
	//当前指标集合数据
	private JzxhXqzbData mJzxhXqzbData;
	
	private JJ_EmergentQuotaCellDetailInfo mClickItem;
	
	private int mType;
	
	private String mTimeRead;
	
	private List<JJ_EmergentQuotaCellInfo> mList;
	private JJ_EmergentQuotaDetailAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jzxh_activity_index_details);
		init("分时指标", true);
	}
	
	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		mTvTitle 	 = (TextView) findViewById(R.id.jzxhIndexDetails_tv_title);
		mTvName  	 = (TextView) findViewById(R.id.jzxhIndexDetails_tv_item_name);
		mListView 	 = (MyScrollyListView) findViewById(R.id.jzxhIndexDetails_listview);
		mLayoutChart = (FrameLayout) findViewById(R.id.jzxhIndexDetails_layout_char);
		
		Bundle extras = getIntent().getExtras();
		mType 		  = extras.getInt("type", 0);
		mClickItem 	  = (JJ_EmergentQuotaCellDetailInfo) extras.getSerializable("dataItem");
		mJzxhXqzbData = (JzxhXqzbData) extras.getSerializable("data");
		mTimeRead = TimeUtil.getInstance().getToTime(TimeUtil.getInstance().stringToLong(mClickItem.getPmCellTime()),"yyyy-MM-dd");
		switch (mType) {
		case 0:
			mTvName.setText("RRC建立成功数指标值");
			mTvTitle.setText(mTimeRead + "各时段RRC建立成功数");
			break;
		case 1:
			mTvName.setText("RRC连接建立成功率（%）指标值");
			mTvTitle.setText(mTimeRead + "各时段RRC连接建立成功率（%）");
			break;
		case 2:
			mTvName.setText("E-RAB建立请求数指标值");
			mTvTitle.setText(mTimeRead + "各时段E-RAB建立请求数");
			break;
		case 3:
			mTvName.setText("E-RAB建立成功率（%）指标值");
			mTvTitle.setText(mTimeRead + "各时段E-RAB建立成功率（%）");
			break;
		case 4:
			mTvName.setText("无线掉线率（%）指标值");
			mTvTitle.setText(mTimeRead + "各时段无线掉线率（%）");
			break;
		case 5:
			mTvName.setText("E-RAB掉线率（%）指标值");
			mTvTitle.setText(mTimeRead + "各时段E-RAB掉线率（%）");
			break;
		case 6:
			mTvName.setText("切换成功率（%）指标值");
			mTvTitle.setText(mTimeRead + "各时段切换成功率（%）");
			break;
		case 7:
			mTvName.setText("小区用户面上行字节数（KByte）指标值");
			mTvTitle.setText(mTimeRead + "各时段小区用户面上行字节数（KByte）");
			break;
		case 8:
			mTvName.setText("上行用户平均速率（Mbps）指标值");
			mTvTitle.setText(mTimeRead + "各时段上行用户平均速率（Mbps）");
			break;
		case 9:
			mTvName.setText("小区用户面下行字节数（KByte）指标值");
			mTvTitle.setText(mTimeRead + "各时段小区用户面下行字节数（KByte）");
			break;
		case 10:
			mTvName.setText("下行用户平均速率（Mbps）指标值");
			mTvTitle.setText(mTimeRead + "各时段下行用户平均速率（Mbps）");
			break;
		case 11:
			mTvName.setText("VOLTE上行丢包率（%）指标值");
			mTvTitle.setText(mTimeRead + "各时段VOLTE上行丢包率（%）");
			break;
		case 12:
			mTvName.setText("上行RTP丢包数（个）指标值");
			mTvTitle.setText(mTimeRead + "各时段上行RTP丢包数（个）");
			break;
		case 13:
			mTvName.setText("最大RRC连接数指标值");
			mTvTitle.setText(mTimeRead + "各时段最大RRC连接数");
			break;
		case 14:
			mTvName.setText("上行PRB利用率（%）指标值");
			mTvTitle.setText(mTimeRead + "各时段上行PRB利用率（%）");
			break;
		case 15:
			mTvName.setText("下行PRB利用率（%）指标值");
			mTvTitle.setText(mTimeRead + "各时段下行PRB利用率（%）");
			break;
		default:
			break;
		}
		readHourDetails();
	}
	
	private void readHourDetails(){
		showMyDialog();
		JJ_RequestGetEmergencyCiParam request = new JJ_RequestGetEmergencyCiParam();
		request.setUserid(UtilHandler.getInstance().toInt(getUserID(), 0));
		request.setCellTime(mTimeRead);
		request.setCi(mJzxhXqzbData.ci);
		NetWorkUtilNow.getInstances().readNetworkPostJsonObject(
				NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_JZXH_FSZB),
				JsonHandler.getHandler().toJson(request), EnumRequest.NET_EMERGENT_CELL_QUOTA_CI_PARAM.toInt(),
				JzxhIndexDetailsActivity.this);
	}
	
	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		//分时指标网络返回
		if (type == EnumRequest.NET_EMERGENT_CELL_QUOTA_CI_PARAM.toInt()) {
			dismissMyDialog();
			if (isTrue) {
				JJ_EmergentQuotaResponse mResponse = JsonHandler.getHandler().getTarget(object.toString(),JJ_EmergentQuotaResponse.class);
				if (mResponse != null && mResponse.isRs()) {
					if (mResponse.getDatas() != null && mResponse.getDatas().size() > 0) {
						mList = mResponse.getDatas();
						int max = setData(mType);
						mAdapter = new JJ_EmergentQuotaDetailAdapter(JzxhIndexDetailsActivity.this,
								mList.get(0).getPmList(), mType, max);
						mListView.setAdapter(mAdapter);
					} else {
						showCommon("暂无分时指标数据");
						finish();
					}
				}else if (mResponse != null) {
					showCommon(mResponse.getMsg());
					finish();
				}else {
					showCommon("获取失败");
					finish();
				}
			}else {
				showCommon(object.toString());
				finish();
			}
		}
	}
	
	private JJ_EmergentQuotaCellInfo mCellInfo;
	private int setData(int type) {
		mCellInfo = mList.get(0);
		int max0 = 0;
		if (type == 1 || type == 3 || type == 4 || type == 5 || type == 6 || type == 11 || type == 14 || type == 15) {
			double maxD = getMaxD(type);
			JJ_LineCharView mLineView = new JJ_LineCharView(JzxhIndexDetailsActivity.this, 0,
					getMaxValueD(maxD), getStepValueD(maxD), JzxhIndexDetailsActivity.this, type);
			mLayoutChart.addView(mLineView);
			mLineView.setDataset(getDataList(type, mList.get(0)), getTimeList(mList.get(0)));
			max0 = (int) maxD;
		} else {
			max0 = getMax(type);
			JJ_LineCharView mLineView = new JJ_LineCharView(JzxhIndexDetailsActivity.this, 0,
					getMaxValue(max0), getStepValue(max0), JzxhIndexDetailsActivity.this, type);
			mLayoutChart.addView(mLineView);
			mLineView.setDataset(getDataList(type, mList.get(0)), getTimeList(mList.get(0)));
		}

		return max0;
	}
	
	private int getMax(int type) {
		// 0 RRC建立成功数 1RRC连接建立成功率（%）2E-RAB建立请求数 3E-RAB建立成功率（%）
		// 4 无限掉线率（%）5 E-RAB掉线率（%） 6切换成功率(含ENB内和ENB间）
		// 7小区用户面上行字节数（Kbyte）8上行用户平均速率（Mbytes） 9小区用户面下行字节数（Kbyte）
		// 10下行用户平均速率（Mbytes）11VOLTE上行丢包率（%） 12上行RTP丢包数（万个）
		// 13最大RRC连接数 14上行PRB利用率（%）15下行PRB利用率（%）
		int max = 0;

		if (mCellInfo.getPmList() == null || mCellInfo.getPmList().size() == 0) {
			return max;
		}
		switch (type) {
		case 0:
			// RRC建立成功数
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEnbha06(), 0);
				if (max < value) {
					max = (int) value + 1;
				}
			}
			break;
		case 1:
			// RRC连接建立成功率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0101(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (value < 1) {
							max = 1;
						} else {
							max = (int) (value + 1);
						}

					}
				}
			}
			break;
		case 2:
			// E-RAB建立请求数
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEnbhb05(), 0);
				if (max < value) {
					max = (int) value + 1;
				}
			}
			break;
		case 3:
			// E-RAB建立成功率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0102(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (value < 1) {
							max = 1;
						} else {
							max = (int) (value + 1);
						}
					}
				}
			}
			break;
		case 4:
			// 无限掉线率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0223(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (value < 1) {
							max = 1;
						} else {
							max = (int) (value + 1);
						}
					}
				}
			}
			break;
		case 5:
			// E-RAB掉线率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0202(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (value < 1) {
							max = 1;
						} else {
							max = (int) (value + 1);
						}
					}
				}
			}
			break;
		case 6:
			// 切换成功率(含ENB内和ENB间）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0306(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (value < 1) {
							max = 1;
						} else {
							max = (int) (value + 1);
						}
					}
				}
			}
			break;
		case 7:
			// 小区用户面上行字节数（Kbyte）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0505(), 0);
				if (max < value) {
					max = (int) value + 1;
				}
			}
			break;
		case 8:
			// 上行用户平均速率（Mbytes）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0535(), 0);
				if (max < value) {
					max = (int) value + 1;
				}
			}
			break;
		case 9:
			// 小区用户面下行字节数（Kbyte）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0506(), 0);
				if (max < value) {
					max = (int) value + 1;
				}
			}
			break;
		case 10:
			// 下行用户平均速率（Mbytes）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0536(), 0);
				if (max < value) {
					max = (int) value + 1;
				}
			}
			break;
		case 11:
			// VOLTE上行丢包率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0416(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (value < 1) {
							max = 1;
						} else {
							max = (int) (value + 1);
						}
					}
				}
			}
			break;
		case 12:
			// 上行RTP丢包数（万个）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEnbhh061(), 0);
				if (max < value) {
					max = (int) value + 1;
				}
			}
			break;
		case 13:
			// 最大RRC连接数
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEnbha04(), 0);
				if (max < value) {
					max = (int) value + 1;
				}
			}
			break;
		case 14:
			// 上行PRB利用率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0529(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (value < 1) {
							max = 1;
						} else {
							max = (int) (value + 1);
						}
					}
				}
			}
			break;
		case 15:
			// 下行PRB利用率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0530(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (value < 1) {
							max = 1;
						} else {
							max = (int) (value + 1);
						}
					}

				}
			}
			break;

		default:
			break;
		}

		return max;
	}

	private double getMaxD(int type) {
		// 0 RRC建立成功数 1RRC连接建立成功率（%）2E-RAB建立请求数 3E-RAB建立成功率（%）
		// 4 无限掉线率（%）5 E-RAB掉线率（%） 6切换成功率(含ENB内和ENB间）
		// 7小区用户面上行字节数（Kbyte）8上行用户平均速率（Mbytes） 9小区用户面下行字节数（Kbyte）
		// 10下行用户平均速率（Mbytes）11VOLTE上行丢包率（%） 12上行RTP丢包数（万个）
		// 13最大RRC连接数 14上行PRB利用率（%）15下行PRB利用率（%）
		double max = 0;

		if (mCellInfo.getPmList() == null || mCellInfo.getPmList().size() == 0) {
			return -1;
		}
		switch (type) {
		case 0:
			// RRC建立成功数
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEnbha06(), 0);
				if (max < value) {
					max = value;
				}
			}
			break;
		case 1:
			// RRC连接建立成功率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0101(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (((int) value / 10) > 0) {
							max = (((int) value / 10) + 1) * 10;
						} else {
							if (value >= 1) {
								max = ((int) value / 1) + 1;
							} else {
								max = 1;
							}
						}
					}
				}
			}
			break;
		case 2:
			// E-RAB建立请求数
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEnbhb05(), 0);
				if (max < value) {
					max = value;
				}
			}
			break;
		case 3:
			// E-RAB建立成功率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0102(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (((int) value / 10) > 0) {
							max = (((int) value / 10) + 1) * 10;
						} else {
							if (value >= 1) {
								max = ((int) value / 1) + 1;
							} else {
								max = 1;
							}
						}
					}
				}
			}
			break;
		case 4:
			// 无限掉线率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0223(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (((int) value / 10) > 0) {
							max = (((int) value / 10) + 1) * 10;
						} else {
							if (value >= 1) {
								max = ((int) value / 1) + 1;
							} else {
								max = 1;
							}
						}
					}
				}
			}
			break;
		case 5:
			// E-RAB掉线率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0202(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (((int) value / 10) > 0) {
							max = (((int) value / 10) + 1) * 10;
						} else {
							if (value >= 1) {
								max = ((int) value / 1) + 1;
							} else {
								max = 1;
							}
						}
					}
				}
			}
			break;
		case 6:
			// 切换成功率(含ENB内和ENB间）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0306(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (((int) value / 10) > 0) {
							max = (((int) value / 10) + 1) * 10;
						} else {
							if (value >= 1) {
								max = ((int) value / 1) + 1;
							} else {
								max = 1;
							}
						}
					}
				}
			}
			break;
		case 7:
			// 小区用户面上行字节数（Kbyte）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0505(), 0);
				if (max < value) {
					max = value;
				}
			}
			break;
		case 8:
			// 上行用户平均速率（Mbytes）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0535(), 0);
				if (max < value) {
					max = value;
				}
			}
			break;
		case 9:
			// 小区用户面下行字节数（Kbyte）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0506(), 0);
				if (max < value) {
					max = value;
				}
			}
			break;
		case 10:
			// 下行用户平均速率（Mbytes）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0536(), 0);
				if (max < value) {
					max = value;
				}
			}
			break;
		case 11:
			// VOLTE上行丢包率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0416(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (((int) value / 10) > 0) {
							max = (((int) value / 10) + 1) * 10;
						} else {
							if (value >= 1) {
								max = ((int) value / 1) + 1;
							} else {
								max = 1;
							}
						}
					}
				}
			}
			break;
		case 12:
			// 上行RTP丢包数（万个）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEnbhh061(), 0);
				if (max < value) {
					max = value;
				}
			}
			break;
		case 13:
			// 最大RRC连接数
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEnbha04(), 0);
				if (max < value) {
					max = value;
				}
			}
			break;
		case 14:
			// 上行PRB利用率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0529(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (((int) value / 10) > 0) {
							max = (((int) value / 10) + 1) * 10;
						} else {
							if (value >= 1) {
								max = ((int) value / 1) + 1;
							} else {
								max = 1;
							}
						}
					}
				}
			}
			break;
		case 15:
			// 下行PRB利用率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0530(), 0);
				if (max < value) {
					if (value >= 100) {
						max = 100;
					} else {
						if (((int) value / 10) > 0) {
							max = (((int) value / 10) + 1) * 10;
						} else {
							if (value >= 1) {
								max = ((int) value / 1) + 1;
							} else {
								max = 1;
							}
						}
					}

				}
			}
			break;

		default:
			break;
		}

		return max;
	}

	// 获取最大值
	private int getMaxValue(int data) {
		int max = 0;
		if (data > 0) {
			int divider = 1;
			while (true) {
				if (data / divider >= 10) {
					divider *= 10;
				} else {
					if (data % divider > 0) {
						max = (data / divider + 1) * divider;
					} else {
						max = data;
					}
					break;
				}
			}
		}
		if (max < 5) {
			max = 5;
		}
		return max;
	}

	private int getStepValue(int data) {
		int max = getMaxValue(data);
		int step = max / 5;
		return step;
	}

	private double getMaxValueD(double data) {
		double max = 0;
		if (data > 0) {
			double divider = 0.01;
			while (true) {
				if (data / divider >= 10) {
					divider *= 10;
				} else {
					if (data % divider > 0) {
						max = (data / divider + 1) * divider;
					} else {
						max = data;
					}
					break;
				}
			}
		}
		return max;
	}

	private double getStepValueD(double data) {
		double max = getMaxValueD(data);
		double step = max / 5.0;
		return step;
	}
	
	private List<Double> getDataList(int type, JJ_EmergentQuotaCellInfo mCellInfo) {
		// 0 RRC建立成功数 1RRC连接建立成功率（%）2E-RAB建立请求数 3E-RAB建立成功率（%）
		// 4 无限掉线率（%）5 E-RAB掉线率（%） 6切换成功率(含ENB内和ENB间）
		// 7小区用户面上行字节数（Kbyte）8上行用户平均速率（Mbytes） 9小区用户面下行字节数（Kbyte）
		// 10下行用户平均速率（Mbytes）11VOLTE上行丢包率（%） 12上行RTP丢包数（万个）
		// 13最大RRC连接数 14上行PRB利用率（%）15下行PRB利用率（%）
		List<Double> list = new ArrayList<>();

		if (mCellInfo.getPmList() == null || mCellInfo.getPmList().size() == 0) {
			return list;
		}

		switch (type) {
		case 0:
			// RRC建立成功数
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEnbha06(), 0);
				addNullData(list, UtilHandler.getInstance().toDfSum(value, "00"), i);
			}
			break;
		case 1:
			// RRC连接建立成功率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0101(), 0);
				addNullData(list, UtilHandler.getInstance().toDfSum(value, "00"), i);
			}
			break;
		case 2:
			// E-RAB建立请求数
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEnbhb05(), 0);
				addNullData(list, UtilHandler.getInstance().toDfSum(value, "00"), i);
			}
			break;
		case 3:
			// E-RAB建立成功率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0102(), 0);
				addNullData(list, UtilHandler.getInstance().toDfSum(value, "00"), i);
			}
			break;
		case 4:
			// 无线掉线率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0223(), 0);
				addNullData(list, UtilHandler.getInstance().toDfSum(value, "00"), i);
			}
			break;
		case 5:
			// E-RAB掉线率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0202(), 0);
				addNullData(list, UtilHandler.getInstance().toDfSum(value, "00"), i);
			}
			break;
		case 6:
			// 切换成功率(含ENB内和ENB间）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0306(), 0);
				addNullData(list, UtilHandler.getInstance().toDfSum(value, "00"), i);
			}
			break;
		case 7:
			// 小区用户面上行字节数（Kbyte）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0505(), 0);
				addNullData(list, UtilHandler.getInstance().toDfSum(value, "00"), i);
			}
			break;

		case 8:
			// 上行用户平均速率（Mbytes）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0535(), 0);
				addNullData(list, UtilHandler.getInstance().toDfSum(value, "00"), i);
			}

			break;
		case 9:
			// 小区用户面下行字节数（Kbyte）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0506(), 0);
				addNullData(list, UtilHandler.getInstance().toDfSum(value, "00"), i);
			}
			break;
		case 10:
			// 下行用户平均速率（Mbytes）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0536(), 0);
				addNullData(list, UtilHandler.getInstance().toDfSum(value, "00"), i);
			}
			break;
		case 11:
			// VOLTE上行丢包率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0416(), 0);
				addNullData(list, UtilHandler.getInstance().toDfSum(value, "00"), i);
			}
			break;
		case 12:
			// 上行RTP丢包数（万个）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEnbhh061(), 0);
				addNullData(list, UtilHandler.getInstance().toDfSum(value, "00"), i);
			}
			break;
		case 13:
			// 最大RRC连接数
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEnbha04(), 0);
				addNullData(list, value, i);
			}
			break;
		case 14:
			// 上行PRB利用率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0529(), 0);
				addNullData(list, UtilHandler.getInstance().toDfSum(value, "00"), i);
			}
			break;
		case 15:
			// 下行PRB利用率（%）
			for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
				double value = UtilHandler.getInstance().toDouble(mCellInfo.getPmList().get(i).getEu0530(), 0);
				addNullData(list, UtilHandler.getInstance().toDfSum(value, "00"), i);
			}
			break;

		default:
			break;
		}

		return list;
	}

	private List<String> getTimeList(JJ_EmergentQuotaCellInfo mCellInfo) {
		List<String> list = new ArrayList<>();

		if (mCellInfo.getPmList() == null || mCellInfo.getPmList().size() == 0) {
			return list;
		}
		for (int i = 0; i < mCellInfo.getPmList().size(); i++) {
			list.add(TimeUtil.getInstance().getToTime(
					TimeUtil.getInstance().stringToLong(mCellInfo.getPmList().get(i).getPmCellTime()), "HH"));
		}

		return list;
	}

	// 填充空数据
	private void addNullData(List<Double> list, double data, int i) {
		if (i == 0) {
			String time = TimeUtil.getInstance()
					.getToTime(TimeUtil.getInstance().stringToLong(mCellInfo.getPmList().get(i).getPmCellTime()), "H");
			int hour = UtilHandler.getInstance().toInt(time, 0);
			if (hour >= 0) {
				for (int j = 0; j <= hour; j++) {
					list.add(0.0);
				}
			}
			list.add(data);

		} else {
			String timeNow = TimeUtil.getInstance()
					.getToTime(TimeUtil.getInstance().stringToLong(mCellInfo.getPmList().get(i).getPmCellTime()), "H");
			String timeLast = TimeUtil.getInstance().getToTime(
					TimeUtil.getInstance().stringToLong(mCellInfo.getPmList().get(i - 1).getPmCellTime()), "H");
			int hourNow = UtilHandler.getInstance().toInt(timeNow, 0);
			int hourLast = UtilHandler.getInstance().toInt(timeLast, 0);
			if (hourNow - hourLast > 1) {
				for (int j = 0; j < (hourNow - hourLast - 1); j++) {
					list.add(0.0);
				}
			}
			list.add(data);
		}
	}
}
