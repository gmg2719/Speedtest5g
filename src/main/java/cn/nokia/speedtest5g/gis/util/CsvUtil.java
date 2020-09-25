package cn.nokia.speedtest5g.gis.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.text.TextUtils;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.bean.Signal;
import cn.nokia.speedtest5g.app.bean.UserLogin;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WybLog;

import com.android.volley.util.SharedPreHandler;

public class CsvUtil {

	private static CsvUtil cu = null;

	public static synchronized CsvUtil getInstances() {
		if (cu == null) {
			cu = new CsvUtil();
		}
		return cu;
	}

	/**
	 * 保存
	 * 
	 * @param list
	 * @param fileName
	 *            文件名称
	 * @param filePath
	 *            文件路径
	 * @param require_id
	 *            需求单号
	 * @return
	 */
	public StringBuffer ExportToCSV(List<Signal> list, String fileName, String filePath, String require_id) {
		// 需要上传的数据
		StringBuffer sbNet = new StringBuffer();
		if (TextUtils.isEmpty(filePath)) {
			return sbNet;
		}
		FileWriter fw = null;
		BufferedWriter bfw = null;
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		File saveFile = new File(file.getPath() + "/" + fileName);
		try {
			fw = new FileWriter(saveFile);
			bfw = new BufferedWriter(fw);
			if (list.size() > 0) {
				// 写入表头
				String[] arrTitle = { "USER_ID", "IMEI", "PHONE", "MODEL", "RELEASE", "MCC", "MNC", "IMSI", "NET_TYPE",
						"TIME", "LON", "LAT", "LTE_TAC", "LTE_PCI", "LTE_CGI", "LTE_RSRP", "LTE_SINR", "LTE_ENB",
						"LTE_CID", "GSM_LAC", "GSM_CID", "GSM_RXL", "TD_LAC", "TD_CI", "TD_PCCPCH", "NUMBERCODE",
						"GPSTYPE", "LTE_RSRQ", "RSRPSINR", "REQUIRE_ID", "SIGNAL_TYPE", "HO_EVENTS", "UL", "DL",
						"SPEED", "CALL_DURATION" ,"XYTYPE", "BAND"};
				for (int i = 0; i < arrTitle.length; i++) {
					if (i != arrTitle.length - 1)
						bfw.write(arrTitle[i] + ',');
					else
						bfw.write(arrTitle[i]);
				}
				// 写好表头后换行
				bfw.newLine();
				// 写入数据
				Signal mSignal;
				UserLogin login = new UserLogin(SpeedTest5g.getContext());
				// 设置上传参数头内容
				sbNet.append(fileName.replace(".csv", ""));// 文件名
				sbNet.append(",");
				sbNet.append(login.getUser_id());// 公共信息
				sbNet.append(",");
				sbNet.append(login.getPhone());
				sbNet.append(",");
				sbNet.append(login.getModel());
				sbNet.append(",");
				sbNet.append(login.getRelease());
				sbNet.append(",460,00,");
				sbNet.append(login.getImei());
				sbNet.append(",");
				sbNet.append(login.getImsi());
				sbNet.append("@");
				for (int i = 0; i < list.size(); i++) {
					// Toast.makeText(mContext, "正在导出第"+(i+1)+"条",
					// Toast.LENGTH_SHORT).show();
					StringBuffer sb = new StringBuffer();
					mSignal = list.get(i);
					// 设置头数据
					sb.append(login.getUser_id());
					sb.append(",");
					sb.append(login.getImei());
					sb.append(",");
					sb.append(login.getPhone());
					sb.append(",");
					sb.append(login.getModel());
					sb.append(",");
					sb.append(login.getRelease());
					sb.append(",");
					// sb.append("460,00,");
					sb.append(mSignal.getMcc());
					sb.append(",");
					sb.append(mSignal.getMnc());
					sb.append(",");
					sb.append(login.getImsi());
					sb.append(",");
					sb.append(mSignal.getTypeNet());
					sb.append(",");
					sb.append(mSignal.getTime());// 第10个
					sb.append(",");
					sb.append(mSignal.getLon());
					sb.append(",");
					sb.append(mSignal.getLat());
					sb.append(",");
					sb.append(mSignal.getLte_tac());
					sb.append(",");
					sb.append(mSignal.getLte_pci());
					sb.append(",");
					sb.append(mSignal.getLte_cgi());
					sb.append(",");
					sb.append(mSignal.getLte_rsrp());
					sb.append(",");
					sb.append(mSignal.getLte_sinr());
					sb.append(",");
					sb.append(mSignal.getLte_enb());
					sb.append(",");
					sb.append(mSignal.getLte_cid());
					sb.append(",");
					sb.append(mSignal.getGsm_lac());// 第20个
					sb.append(",");
					sb.append(mSignal.getGsm_cid());
					sb.append(",");
					sb.append(mSignal.getGsm_rxl());
					sb.append(",");
					sb.append(mSignal.getTd_lac());
					sb.append(",");
					sb.append(mSignal.getTd_ci());
					sb.append(",");
					sb.append(mSignal.getTd_pccpch());
					sb.append(",");
					sb.append(mSignal.getNumberCode());
					sb.append(",");
					sb.append(mSignal.getTypeGps());
					sb.append(",");
					sb.append(mSignal.getLte_rsrq());
					sb.append(",");
					sb.append(mSignal.getRsrp_sinr());// 第29个
					sb.append(",");
					sb.append(require_id);// 第30个
					sb.append(",");
					sb.append(mSignal.getSignal_type());// 第31个
					sb.append(",");
					sb.append(mSignal.getHoState());// 第32个-ho_events
					sb.append(",");
					sb.append(mSignal.getUl());// 33 UL
					sb.append(",");
					sb.append(mSignal.getDl());// 34 DL
					sb.append(",");
					sb.append(mSignal.getSpeed());// 35 SPEED
					sb.append(",");
					sb.append(mSignal.getCall_duration());// 36 CALL_DURATION
					sb.append(",");
					sb.append(mSignal.getXyType());// 37 XYTYPE---室分or宏站
					sb.append(",");
					sb.append(mSignal.getBand()); //38 BAND

					bfw.write(sb.toString());
					// 写好每条记录后换行
					bfw.newLine();
					/*-------------上传网络的数据格式------------*/
					if (require_id.isEmpty()) {
						if (i != 0) {
							// 多条数据用 | 分割
							sbNet.append("|");
						}
						sbNet.append(mSignal.getTypeNet());
						sbNet.append(",");
						sbNet.append(mSignal.getTime());
						sbNet.append(",");
						sbNet.append(mSignal.getLon());
						sbNet.append(",");
						sbNet.append(mSignal.getLat());
						sbNet.append(",");

						sbNet.append(mSignal.getLte_tac());
						sbNet.append(",");
						sbNet.append(mSignal.getLte_pci());
						sbNet.append(",");
						sbNet.append(mSignal.getLte_cgi());
						sbNet.append(",");
						sbNet.append(mSignal.getLte_rsrp());
						sbNet.append(",");
						sbNet.append(mSignal.getLte_sinr());
						sbNet.append(",");
						sbNet.append(mSignal.getLte_enb());
						sbNet.append(",");
						sbNet.append(mSignal.getLte_cid());
						sbNet.append(",");
						sbNet.append(mSignal.getGsm_lac());
						sbNet.append(",");
						sbNet.append(mSignal.getGsm_cid());
						sbNet.append(",");
						sbNet.append(mSignal.getGsm_rxl());
						sbNet.append(",");
						sbNet.append(mSignal.getTd_lac());
						sbNet.append(",");
						sbNet.append(mSignal.getTd_ci());
						sbNet.append(",");
						sbNet.append(mSignal.getTd_pccpch());
						sbNet.append(",");
						sbNet.append(mSignal.getNumberCode());
						sbNet.append(",");
						sbNet.append(mSignal.getTypeGps());
						// 新增UL，DL,SPEED,CALL_DURATION
						sbNet.append(",");
						sbNet.append(mSignal.getUl());
						sbNet.append(",");
						sbNet.append(mSignal.getDl());
						sbNet.append(",");
						sbNet.append(mSignal.getSpeed());
						sbNet.append(",");
						sbNet.append(mSignal.getCall_duration());
						sbNet.append(",");
						// 新增xyType
						sbNet.append(mSignal.getXyType());
						//新增band
						sbNet.append(",");
						sbNet.append(mSignal.getBand());
					}
				}
			}
			// 将缓存数据写入文件
			bfw.flush();
			// Toast.makeText(mContext, "导出完毕！", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			WybLog.syso("csv:出现异常");
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
				if (bfw != null) {
					bfw.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return sbNet;
	}


	public List<Signal> readCsv(String path) {
		List<Signal> list = new ArrayList<Signal>();
		File inFile = new File(path); // 读取的CSV文件
		WybLog.syso("地址" + path);
		String inString = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inFile));
			String[] split;
			reader.readLine();
			Signal mSignal;
			while ((inString = reader.readLine()) != null) {
				split = inString.split(",");
				// 621,866926021297949,15959018065,NX510J,5.0.2,460,00,460110260332925,LTE,2015-11-05
				// 11:20:19.245,119.194855,26.076241,24065,104,98706738,-113,-18,385573,50,-1,-1,99,,,,Beta
				// 2.1.6,network

				if (split.length >= 26) {
					// "USER_ID","IMEI","PHONE","MODEL","RELEASE","MCC","MNC","IMSI","NET_TYPE","TIME","LON","LAT",
					// "LTE_TAC","LTE_PCI","LTE_CGI","LTE_RSRP","LTE_SINR","LTE_ENB","LTE_CID",
					// "GSM_LAC","GSM_CID","GSM_RXL","TD_LAC","TD_CI","TD_PCCPCH"
					mSignal = new Signal();
					mSignal.setMcc(split[5]);
					mSignal.setMnc(split[6]);
					mSignal.setTypeNet(split[8]);
					mSignal.setTime(split[9]);
					mSignal.setLon(split[10]);
					mSignal.setLat(split[11]);
					mSignal.setLte_tac(split[12]);
					mSignal.setLte_pci(split[13]);
					mSignal.setLte_cgi(split[14]);
					mSignal.setLte_rsrp(split[15]);
					mSignal.setLte_sinr(split[16]);
					mSignal.setLte_enb(split[17]);
					mSignal.setLte_cid(split[18]);
					mSignal.setGsm_lac(split[19]);
					mSignal.setGsm_cid(split[20]);
					mSignal.setGsm_rxl(split[21]);
					mSignal.setTd_lac(split[22]);
					mSignal.setTd_ci(split[23]);
					mSignal.setTd_pccpch(split[24]);
					if (split.length >= 27) {
						mSignal.setTypeGps(split[26]);
						if (split.length >= 29) {
							mSignal.setLte_rsrq(split[27]);
							mSignal.setRsrp_sinr(UtilHandler.getInstance().toInt(split[28],0));
							// 29为require_id
							if (split.length >= 31) {
								mSignal.setSignal_type(UtilHandler.getInstance().toInt(split[30],0));
							}
							if (split.length >= 32) {
								mSignal.setHoState(UtilHandler.getInstance().toInt(split[31],0));
							}
							if (split.length >= 33) {
								mSignal.setUl(split[32]);
							}
							if (split.length >= 34) {
								mSignal.setDl(split[33]);
							}
							if (split.length >= 35) {
								mSignal.setSpeed(split[34]);
							}
							if (split.length >= 36) {
								mSignal.setCall_duration(split[35]);
							}
							if (split.length >= 37) {
								mSignal.setXyType(UtilHandler.getInstance().toInt(split[36], 0));
							}
							if (split.length >= 38){
								mSignal.setBand(split[37]);
							}
						}
					}
					list.add(mSignal);
				}
			}
			reader.close();
		} catch (FileNotFoundException ex) {
			WybLog.syso("没找到文件！");
		} catch (Exception ex) {
			WybLog.syso("读写文件出错！");
		}
		return list;
	}

	/**
	 * 保存基站信号测试数据
	 * @param list
	 * @param fileName
	 * @param filePath
	 * @param isFrom false 基站信号 true nsa信号
	 * @return
	 */
	public StringBuffer ExportSignalToCSV(List<Signal> list, String fileName, String filePath, boolean isFrom) {
		// 需要上传的数据
		StringBuffer sbNet = new StringBuffer();
		FileWriter fw = null;
		BufferedWriter bfw = null;
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		File saveFile = new File(file.getPath() + "/" + fileName);
		try {
			fw = new FileWriter(saveFile);
			bfw = new BufferedWriter(fw);
			if (list.size() > 0) {
				// 写入表头
				String[] arrTitle = null;
				if(isFrom){
					arrTitle = new String[]{"FILE_NAME","USER_ID", "MOBILE","MOBILE_TYPE","OS","MCC","MNC","IMEI", "IMSI", "TYPE", "COLLECT_TIME", "LON", "LAT", "LTE_TAC",
							"LTE_PCI", "LTE_CI", "LTE_RSRP", "LTE_SINR", "LTE_ENB", "LTE_CID", "GSM_LAC", "GSM_CID", "GSM_SIGN",
							"TD_LAC", "TD_CID", "TD_SIGN", "VERSIONCODE", "COLLECT_type","SPEED_PER_HOUR","AITITUDE","UL","DL","BAND","FREQUENCY","RSRQ","SITE_NAME",
							"RSRP_NR", "SINR_NR", "RSRQ_NR", "PCI_NR", "EARFCN_NR", "BAND_NR"};
				}else{
					arrTitle = new String[]{"FILE_NAME","USER_ID", "MOBILE","MOBILE_TYPE","OS","MCC","MNC","IMEI", "IMSI", "TYPE", "COLLECT_TIME", "LON", "LAT", "LTE_TAC",
							"LTE_PCI", "LTE_CI", "LTE_RSRP", "LTE_SINR", "LTE_ENB", "LTE_CID", "GSM_LAC", "GSM_CID", "GSM_SIGN",
							"TD_LAC", "TD_CID", "TD_SIGN", "VERSIONCODE", "COLLECT_type","SPEED_PER_HOUR","AITITUDE","UL","DL","BAND","FREQUENCY","RSRQ","SITE_NAME",
							"RSRP_NR", "SINR_NR", "RSRQ_NR", "PCI_NR", "EARFCN_NR", "BAND_NR", "RSRP2", "SINR2"};
				}
				for (int i = 0; i < arrTitle.length; i++) {
					if (i != arrTitle.length - 1)
						bfw.write(arrTitle[i] + ',');
					else
						bfw.write(arrTitle[i]);
				}
				// 写好表头后换行
				bfw.newLine();
				// 写入数据
				Signal mSignal;
				UserLogin login = new UserLogin(SpeedTest5g.getContext());
				// 设置上传参数头内容
				// 文件名
				sbNet.append(fileName.replace(".csv", ""));
				sbNet.append(",");
				//用户id
				sbNet.append(login.getUser_id());
				sbNet.append(",");
				sbNet.append(login.getPhone());
				sbNet.append(",");
				sbNet.append(login.getModel());
				sbNet.append(",");
				sbNet.append(login.getRelease());
				sbNet.append(",");

				mSignal = list.get(0);
				sbNet.append(mSignal.getMcc());
				sbNet.append(",");
				sbNet.append(mSignal.getMnc());
				sbNet.append(",");
				//				sbNet.append(",460,00,");

				sbNet.append(login.getImei());
				sbNet.append(",");
				sbNet.append(login.getImsi());
				sbNet.append("@");
				//手机版本号
				int version_code = UtilHandler.getInstance().toInt(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().VERSION_CODE, ""), 0);;
				StringBuffer sbTem,sb;
				//进入循环
				for (int i = 0; i < list.size(); i++) {
					mSignal = list.get(i);
					sbTem = new StringBuffer();
					sbTem.append(mSignal.getTypeNet());
					sbTem.append(",");
					sbTem.append(mSignal.getTime());// 第10个
					sbTem.append(",");
					sbTem.append(mSignal.getLon());
					sbTem.append(",");
					sbTem.append(mSignal.getLat());
					sbTem.append(",");
					sbTem.append(mSignal.getLte_tac());
					sbTem.append(",");
					sbTem.append(mSignal.getLte_pci());
					sbTem.append(",");
					sbTem.append(mSignal.getLte_cid());
					sbTem.append(",");
					sbTem.append(mSignal.getLte_rsrp());
					sbTem.append(",");
					sbTem.append(mSignal.getLte_sinr());
					sbTem.append(",");
					sbTem.append(mSignal.getLte_enb());
					sbTem.append(",");
					sbTem.append(mSignal.getLte_cid());
					sbTem.append(",");
					sbTem.append(mSignal.getGsm_lac());// 第20个
					sbTem.append(",");
					sbTem.append(mSignal.getGsm_cid());
					sbTem.append(",");
					sbTem.append(mSignal.getGsm_rxl());
					sbTem.append(",");
					sbTem.append(mSignal.getTd_lac());
					sbTem.append(",");
					sbTem.append(mSignal.getTd_ci());
					sbTem.append(",");
					sbTem.append(mSignal.getTd_pccpch());
					sbTem.append(",");
					sbTem.append(version_code);
					sbTem.append(",");
					sbTem.append(mSignal.getTypeGps());
					sbTem.append(",");
					sbTem.append(mSignal.getSpeed());
					sbTem.append(",");
					sbTem.append(mSignal.altitude);
					sbTem.append(",");
					sbTem.append(mSignal.getUl());
					sbTem.append(",");
					sbTem.append(mSignal.getDl());
					sbTem.append(",");
					sbTem.append(mSignal.getLte_band());
					sbTem.append(",");
					sbTem.append(mSignal.lte_pd);
					sbTem.append(",");
					sbTem.append(mSignal.getLte_rsrq());
					sbTem.append(",");
					if ("NR".equals(mSignal.getTypeNet()) || "LTE".equals(mSignal.getTypeNet())) {
						sbTem.append(mSignal.getLte_name());
					}else if ("TD".equals(mSignal.getTypeNet())) {
						sbTem.append(mSignal.getTd_name());
					}else {
						sbTem.append(mSignal.getGsm_name());
					}

					if(isFrom){ //NSA
						sbTem.append(",");
						sbTem.append(mSignal.lte_rsrp_nr==null?"":mSignal.lte_rsrp_nr);
						sbTem.append(",");
						sbTem.append(mSignal.lte_sinr_nr==null?"":mSignal.lte_sinr_nr);
						sbTem.append(",");
						sbTem.append(mSignal.lte_rsrq_nr==null?"":mSignal.lte_rsrq_nr);
						sbTem.append(",");
						sbTem.append(mSignal.lte_pci_nr==null?"":mSignal.lte_pci_nr);
						sbTem.append(",");
						sbTem.append(mSignal.lte_earfcn_nr==null?"":mSignal.lte_earfcn_nr);
						sbTem.append(",");
						sbTem.append(mSignal.lte_band_nr==null?"":mSignal.lte_band_nr);
					}else{ //基站信号
						sbTem.append(",");
						sbTem.append(mSignal.lte_rsrp_nr==null?"":mSignal.lte_rsrp_nr);
						sbTem.append(",");
						sbTem.append(mSignal.lte_sinr_nr==null?"":mSignal.lte_sinr_nr);
						sbTem.append(",");
						sbTem.append(mSignal.lte_rsrq_nr==null?"":mSignal.lte_rsrq_nr);
						sbTem.append(",");
						sbTem.append(mSignal.lte_pci_nr==null?"":mSignal.lte_pci_nr);
						sbTem.append(",");
						sbTem.append(mSignal.lte_earfcn_nr==null?"":mSignal.lte_earfcn_nr);
						sbTem.append(",");
						sbTem.append(mSignal.lte_band_nr==null?"":mSignal.lte_band_nr);
						sbTem.append(",");
						sbTem.append(mSignal.subRsrp==null?"":mSignal.subRsrp);
						sbTem.append(",");
						sbTem.append(mSignal.subSinr==null?"":mSignal.subSinr);
					}

					// 设置往文件里写的数据
					sb = new StringBuffer();
					sb.append(fileName.replace(".csv", ""));
					sb.append(",");
					sb.append(login.getUser_id());
					sb.append(",");
					sb.append(login.getPhone());
					sb.append(",");
					sb.append(login.getModel());
					sb.append(",");
					sb.append(login.getRelease());
					sb.append(",");
					sb.append(mSignal.getMcc());
					sb.append(",");
					sb.append(mSignal.getMnc());
					sb.append(",");
					sb.append(login.getImei());
					sb.append(",");
					sb.append(login.getImsi());
					sb.append(",");
					sb.append(sbTem.toString());
					bfw.write(sb.toString());
					// 写好每条记录后换行
					bfw.newLine();

					sbNet.append(sbTem.toString());
					if(!(i == list.size() - 1)) {
						sbNet.append("|");
					}
				}
			}
			// 将缓存数据写入文件
			bfw.flush();
		} catch (IOException e) {

		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
				if (bfw != null) {
					bfw.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return sbNet;
	}

	//计费操作日志状态转中文
	private String getJifeiStrStatus(int status){
		String strStatus = "未知";
		switch (status) {
		case 0:
			strStatus = "未开始";
			break;
		case 1:
			strStatus = "测试中";
			break;
		case 2:
			strStatus = "暂停";
			break;
		case 3:
			strStatus = "完成";
			break;
		default:
			break;
		}
		return strStatus;
	}
}
