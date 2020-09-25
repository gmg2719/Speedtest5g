package cn.nokia.speedtest5g.gis.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.gis.model.LinquInfo;
import cn.nokia.speedtest5g.gis.model.LteBandChild;
import cn.nokia.speedtest5g.gis.model.LteBandItem;
import cn.nokia.speedtest5g.gis.model.WqGisLayer;
import cn.nokia.speedtest5g.gis.model.WqLayerDetailInfo;
import cn.nokia.speedtest5g.gis.model.JJ_PciLayer;
import cn.nokia.speedtest5g.util.gps.GisLayerKey;
import com.android.volley.util.SharedPreHandler;
import com.baidu.mapapi.model.LatLng;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

/**
 * GIS地图数据处理
 * 
 * @author zwq
 *
 */
public class WQ_GisDbHandler {

	private Object lockObject = "lock", lockObject2 = "lock2";

	private static WQ_GisDbHandler dbGisHandler = null;

	// 判断类型展示图层地址 0位gis 1为pci
	private static int mType = 0;

	public static synchronized WQ_GisDbHandler getIntances() {
		if (dbGisHandler == null) {
			dbGisHandler = new WQ_GisDbHandler();
		}
		mType = 0;
		return dbGisHandler;
	}

	public static synchronized WQ_GisDbHandler getIntances(int type) {
		if (dbGisHandler == null) {
			dbGisHandler = new WQ_GisDbHandler();
		}
		mType = type;
		return dbGisHandler;
	}

	private String oldPath = "";

	private SQLiteDatabase db = null;

	/**
	 * 获取对应的地图数据
	 * 
	 * @param dbPath
	 * @return
	 */
	public WQ_GisDbHandler getDb(String dbPath) {
		try {
			synchronized (lockObject2) {
				if (!TextUtils.isEmpty(dbPath) && ((oldPath != null && !oldPath.equals(dbPath)) || db == null)) {
					File cache;
					if (mType == 1) {
						cache = new File(SharedPreHandler.getShared(SpeedTest5g.getContext())
								.getStringShared(TypeKey.getInstance().CODE_PCI_PATH(), ""));
					} else {
						cache = new File(SharedPreHandler.getShared(SpeedTest5g.getContext())
								.getStringShared(TypeKey.getInstance().CODE_PATH(), ""));
					}

					if (cache != null && cache.exists()) {
						if (mHandler != null) {
							Message msg = mHandler.obtainMessage();
							msg.what = 1012;
							msg.arg1 = View.VISIBLE;
							mHandler.sendMessage(msg);
						}
						WybLog.syso("数据库正在打开");
						db = SQLiteDatabase.openOrCreateDatabase(cache, null);
						WybLog.syso("数据库打开完成");
						if (mHandler != null) {
							Message msg = mHandler.obtainMessage();
							msg.what = 1012;
							msg.arg1 = View.GONE;
							mHandler.sendMessage(msg);
						}
					} else {
						db = null;
					}
				}
				return this;
			}
		} catch (Exception e) {
			WybLog.syso("打开DB错误：" + e.getMessage());
		}
		return this;
	}

	private Handler mHandler;

	public void setHanlder(Handler handler) {
		this.mHandler = handler;
	}

	public String getOldPath() {
		return oldPath;
	}

	public void setOldPath(String p) {
		if (p != null && !p.equals(oldPath)) {
			closeDb();
		}
		this.oldPath = p;

	}

	/**
	 * 关闭数据库
	 */
	public void closeDb() {
		try {
			if (db != null) {
				db.close();
			}
			db = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 数据库是否被关闭
	 * 
	 * @return
	 */
	public boolean isDbClosed() {
		return db == null;
	}

	/**
	 * 搜索表里所有符合条件的记录中的指定字段值
	 * 
	 * @param table_name
	 *            表名
	 * @param type
	 *            字段名称数组 -1全部 //0-GSM小区 ,1-TDS小区 ,2 LTE小区 ,3 LTE规划, 4 铁塔-移动,5
	 *            铁塔-联通,6 铁塔-电信,7 铁塔-全量 8 铁塔-移动联通,9 铁塔-移动电信,10 铁塔-联通电信,11
	 *            铁塔-移动联通电信,12 铁塔-其他
	 * @param selection
	 *            条件，如：column_name1='?' and column_name2='?'
	 * @param selectionArgs
	 *            替换?的值，new String[]{column_value1, column_value2}
	 * @param orderBy
	 *            排序，如： id desc /asc，不需要排序就填null
	 * @return Cursor 搜索结果
	 */
	public Cursor queryRows(String table_name, int type, String selection, String[] selectionArgs, String orderBy) {
		synchronized (lockObject) {
			getDb(getOldPath());
			if (db == null) {
				return null;
			}
			Cursor cursor = null;
			StringBuffer sqlBuf = new StringBuffer("select ");
			if (type == -1) {
				sqlBuf.append("*");
			} else {
				switch (type) {
				case 0:// GSM,
					sqlBuf.append("_id,LON,LAT,CELLNAME,BSCAD11,DIRECTION,JXXQJ,DXQJ,LINEHIGH,CI,LAC,XYTYPE");
					// sqlBuf.append("_id,LON,LAT,CELLNAME,DIRECTION,JXXQJ,DXQJ,LINEHIGH,CI,LAC");
					break;
				case 1:// TDS
					sqlBuf.append("_id,LON,LAT,CELLNAME,DIRECTION,JXXQJ,DXQJ,LINEHIGH,CI,LAC");
					break;
				case 2:// LTE
						// sqlBuf.append("_id,LON,LAT,CELLNAME,WORKPD,DIRECTION,JXXQJ,DXQJ,LINEHIGH,CI,CITY,AREA,XYTYPE,PCI,earfcn,DEVICENAME");
					sqlBuf.append("*");
					break;
				case 3:// 规划
					sqlBuf.append("_id,LON,LAT,eNodeB_NAME,DIRECTION,XQJ,LINEHIGH,eNodeB_ID");
					break;
				case 4:// 移动
				case 5:// 联通
				case 6:// 电信
				case 7:// 全量
				case 8:// 移动联通
				case 9:// 移动电信
				case 10:// 联通电信
				case 11:// 移动联通电信
				case 12:// 其他
					sqlBuf.append("_id,LONGITUDE,LATITUDE,SITE_NAME,EQUITIES_ORG,MAINTAIN_STATE");
					break;
				case 13:// 高铁
					sqlBuf.append("_id,LONGITUDE,LATITUDE,ENODEBNAME,COVER_TYPE");
					break;
				case 15:// 电信2G
				case 16:// 电信4G
					sqlBuf.append("*");
					break;
				default:
					sqlBuf.append("*");
					break;
				}
			}
			sqlBuf.append(" from " + table_name);
			if (selection != null && !"".equals(selection)) {
				sqlBuf.append(" where ");

				if (selection.contains("?")) {
					int len = selectionArgs.length;
					for (int i = 0; i < len; i++) {
						if (selectionArgs[i].length() <= 0) {
							selectionArgs[i] = "";
						}
						selection = selection.replaceFirst("\\?", "'" + selectionArgs[i] + "'");
					}
					sqlBuf.append(selection);
				} else {
					sqlBuf.append(selection);
				}
			}

			if (orderBy != null) {
				sqlBuf.append(" " + orderBy);
			}
			cursor = db.rawQuery(sqlBuf.toString(), null);

			return cursor;
		}
	}

	//
	public Cursor queryRows3(String table_name, String column, List<LinquInfo> selectionArgs, String other) {
		synchronized (lockObject) {
			getDb(getOldPath());
			if (db == null) {
				return null;
			}
			Cursor cursor = null;
			StringBuffer sqlBuf = new StringBuffer("select * from " + table_name + " where ");
			for (int i = 0; i < selectionArgs.size(); i++) {
				if (i == 0) {
					sqlBuf.append(column + "'" + selectionArgs.get(i).neighbor_ci + "'");
				} else {
					sqlBuf.append(" OR " + column + "'" + selectionArgs.get(i).neighbor_ci + "'");
				}
			}

			if (other != null) {
				sqlBuf.append(other);
			}
			String sql = sqlBuf.toString();
			cursor = db.rawQuery(sql, null);

			return cursor;
		}
	}

	public Cursor queryRows2(String table_name, String selection) {
		synchronized (lockObject) {
			getDb(getOldPath());
			if (db == null) {
				return null;
			}
			Cursor cursor = null;
			StringBuffer sqlBuf = new StringBuffer("select * from " + table_name + " where " + selection);

			cursor = db.rawQuery(sqlBuf.toString(), null);

			return cursor;
		}
	}

	/**
	 * 查询对应的marker图层相关信息
	 * 
	 * @param layer
	 * @return
	 */
	public List<WqGisLayer> queryMarker(WqGisLayer layer) {
		synchronized (lockObject) {
			getDb(getOldPath());
			List<WqGisLayer> list = new ArrayList<WqGisLayer>();
			if (db == null) {
				return list;
			}
			double delLatLng = 0.00001;
			String whereSql = "";
			String orderSql = "";
			String tableName = layer.getTabName();
			;
			String ci = layer.getCI();
			LatLng point = layer.getLlGps();
			if (point == null) {
				point = new LatLng(layer.getLAT(), layer.getLON());
			}
			switch (layer.getType()) {
			case 0:
				ci = ci.substring(0, ci.length() - 1);
				whereSql = "CI like " + "'" + ci + "%' and LAC='" + layer.getLAC() + "' and LON>='"
						+ String.valueOf(point.longitude - delLatLng) + "' and LAT>='"
						+ String.valueOf(point.latitude - delLatLng) + "' and LON<='"
						+ String.valueOf(point.longitude + delLatLng) + "' and LAT<='"
						+ String.valueOf(point.latitude + delLatLng) + "'";
				orderSql = " order by CI asc";
				break;
			case 1:
				ci = ci.substring(0, ci.length() - 1);
				whereSql = "CI like " + "'" + ci + "%' and LAC='" + layer.getLAC() + "' and LON>='"
						+ String.valueOf(point.longitude - delLatLng) + "' and LAT>='"
						+ String.valueOf(point.latitude - delLatLng) + "' and LON<='"
						+ String.valueOf(point.longitude + delLatLng) + "' and LAT<='"
						+ String.valueOf(point.latitude + delLatLng) + "'";
				orderSql = " order by CI asc";
				break;
			case 2:
				String[] arrays = ci.split("-");
				if (arrays.length > 3) {
					ci = arrays[2];
				}
				whereSql = "CI like " + "'%" + ci + "%'" + " and LON>='" + String.valueOf(point.longitude - delLatLng)
						+ "' and LAT>='" + String.valueOf(point.latitude - delLatLng) + "' and LON<='"
						+ String.valueOf(point.longitude + delLatLng) + "' and LAT<='"
						+ String.valueOf(point.latitude + delLatLng) + "'";
				orderSql = " order by CI asc";
				break;
			case 3:
				whereSql = (TextUtils.isEmpty(ci) ? "eNodeB_NAME= '" + layer.getCELLNAME() : "eNodeB_ID= '" + ci)  + "' and LON>='" + String.valueOf(point.longitude - delLatLng)
						+ "' and LAT>='" + String.valueOf(point.latitude - delLatLng) + "' and LON<='"
						+ String.valueOf(point.longitude + delLatLng) + "' and LAT<='"
						+ String.valueOf(point.latitude + delLatLng) + "'";
				orderSql = " order by eNodeB_ID asc";
				break;
			case 13:// 高铁
				whereSql = "COVER_TYPE='" + layer.getLAC() + "'" + " and LONGITUDE>='"
						+ String.valueOf(point.longitude - delLatLng) + "' and LATITUDE>='"
						+ String.valueOf(point.latitude - delLatLng) + "' and LONGITUDE<='"
						+ String.valueOf(point.longitude + delLatLng) + "' and LATITUDE<='"
						+ String.valueOf(point.latitude + delLatLng) + "'";
				break;
			default:
				//5G小区
				if (layer.getType() == GisLayerKey.KEY_LAYER_TYPE_5G) {
					whereSql = "lon>='" + String.valueOf(point.longitude - delLatLng) + 
							"' and lat>='" + String.valueOf(point.latitude - delLatLng) + 
							"' and lon<='" + String.valueOf(point.longitude + delLatLng) + 
							"' and lat<='" + String.valueOf(point.latitude + delLatLng) + "'";
					orderSql = " order by ci asc";
				//NB图层	
				}else if (layer.getType() == GisLayerKey.KEY_LAYER_TYPE_NB) {
					whereSql = "longitude>=" + (point.longitude - delLatLng)
							+ " and latitude>=" + (point.latitude - delLatLng) + " and longitude<="
							+ (point.longitude + delLatLng) + " and latitude<="
							+ (point.latitude + delLatLng);
					orderSql = " order by ci asc";
				}
				break;
			}
			// WybLog.syso(tableName + "--" + whereSql + "--" + orderSql);
			Cursor cursor = queryRows(tableName, null, whereSql, null, orderSql);
			if (cursor.getCount() > 0) {
				WqGisLayer layerCursor;
				while (cursor.moveToNext()) {
					layerCursor = new WqGisLayer();
					layerCursor.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
					switch (layer.getType()) {
					case 0:// GSM,
					case 1:// ,TDS
						layerCursor.setLAT(cursor.getDouble(cursor.getColumnIndex("LAT")));
						layerCursor.setLON(cursor.getDouble(cursor.getColumnIndex("LON")));
						layerCursor.setLINEHIGH(cursor.getString(cursor.getColumnIndex("LINEHIGH")));
						layerCursor.setCELLNAME(cursor.getString(cursor.getColumnIndex("CELLNAME")));
						layerCursor.setDIRECTION(cursor.getFloat(cursor.getColumnIndex("DIRECTION")));
						layerCursor.setJXXQJ(cursor.getString(cursor.getColumnIndex("JXXQJ")));
						layerCursor.setDXQJ(cursor.getString(cursor.getColumnIndex("DXQJ")));
						layerCursor.setCI(cursor.getString(cursor.getColumnIndex("CI")));
						layerCursor.setLAC(cursor.getString(cursor.getColumnIndex("LAC")));
						layerCursor.setCity(cursor.getString(cursor.getColumnIndex("CITY")));
						int temp = cursor.getColumnIndex("bcchPD");
						if (temp > 0) {
//							layerCursor.setBand(cursor.getString(temp));
							layerCursor.earfcn = cursor.getString(temp);
						} 
						break;
					case 2:// LTE
						layerCursor.setLAT(cursor.getDouble(cursor.getColumnIndex("LAT")));
						layerCursor.setLON(cursor.getDouble(cursor.getColumnIndex("LON")));
						layerCursor.setLINEHIGH(cursor.getString(cursor.getColumnIndex("LINEHIGH")));
						layerCursor.setCELLNAME(cursor.getString(cursor.getColumnIndex("CELLNAME")));
						layerCursor.setDIRECTION(cursor.getFloat(cursor.getColumnIndex("DIRECTION")));
						layerCursor.setJXXQJ(cursor.getString(cursor.getColumnIndex("JXXQJ")));
						layerCursor.setDXQJ(cursor.getString(cursor.getColumnIndex("DXQJ")));
						layerCursor.setCI(cursor.getString(cursor.getColumnIndex("CI")));
						layerCursor.setCity(cursor.getString(cursor.getColumnIndex("CITY")));
						layerCursor.setCountry(cursor.getString(cursor.getColumnIndex("AREA")));
						layerCursor.setVender(cursor.getString(cursor.getColumnIndex("DEVICENAME")));
						layerCursor.PCI = cursor.getString(cursor.getColumnIndex("PCI"));
						int tempPd = cursor.getColumnIndex("earfcn");
						if (tempPd > 0) {
							layerCursor.earfcn = cursor.getString(tempPd);
						}
						if (cursor.getColumnIndex("WORKPD") > 0) {
							layerCursor.setBand(cursor.getString(cursor.getColumnIndex("WORKPD")));
						}
						break;
					case 3:// 规划
						layerCursor.setLAT(cursor.getDouble(cursor.getColumnIndex("LAT")));
						layerCursor.setLON(cursor.getDouble(cursor.getColumnIndex("LON")));
						layerCursor.setLINEHIGH(cursor.getString(cursor.getColumnIndex("LINEHIGH")));
						layerCursor.setCELLNAME(cursor.getString(cursor.getColumnIndex("eNodeB_NAME")));
						layerCursor.setCI(cursor.getString(cursor.getColumnIndex("eNodeB_ID")));
						layerCursor.setJXXQJ(cursor.getString(cursor.getColumnIndex("XQJ")));
						try {
							layerCursor.setDIRECTION(cursor.getFloat(cursor.getColumnIndex("DIRECTION")));
						} catch (Exception e) {
						}
						break;
					case 13:// 高铁
						layerCursor.setLAT(cursor.getDouble(cursor.getColumnIndex("LATITUDE")));
						layerCursor.setLON(cursor.getDouble(cursor.getColumnIndex("LONGITUDE")));
						layerCursor.setCELLNAME(cursor.getString(cursor.getColumnIndex("ENODEBNAME")));
						// RRU覆盖类型 == 室内。室外
						layerCursor.setLAC(cursor.getString(cursor.getColumnIndex("COVER_TYPE")));
						// 方位角，下倾角，挂高
						layerCursor.setCI(cursor.getString(cursor.getColumnIndex("CI")));
						layerCursor.setLINEHIGH(cursor.getString(cursor.getColumnIndex("ANT_HEIGHT1")));
						break;
					default:
						//5G小区
						if (layer.getType() == GisLayerKey.KEY_LAYER_TYPE_5G) {
							layerCursor.setLAT(cursor.getDouble(cursor.getColumnIndex("lat")));
							layerCursor.setLON(cursor.getDouble(cursor.getColumnIndex("lon")));
							layerCursor.setLINEHIGH(cursor.getString(cursor.getColumnIndex("ant_height")));//天线挂高
							layerCursor.setCELLNAME(cursor.getString(cursor.getColumnIndex("cell_name")));//小区名称
							layerCursor.setDIRECTION(cursor.getFloat(cursor.getColumnIndex("azumuth")));//方位角
							layerCursor.setDXQJ(cursor.getString(cursor.getColumnIndex("downtilt")));//下倾角
							layerCursor.earfcn = cursor.getString(cursor.getColumnIndex("earfcn"));//频点
							layerCursor.PCI = cursor.getString(cursor.getColumnIndex("pci"));//PCI
							layerCursor.setCI(cursor.getString(cursor.getColumnIndex("ci")));//CI
							layerCursor.TAC = cursor.getString(cursor.getColumnIndex("tac"));//TAc
						//NB图层	
						}else if (layer.getType() == GisLayerKey.KEY_LAYER_TYPE_NB) {
							layerCursor.setLAT(cursor.getDouble(cursor.getColumnIndex("latitude")));
							layerCursor.setLON(cursor.getDouble(cursor.getColumnIndex("longitude")));
							layerCursor.setLINEHIGH(cursor.getString(cursor.getColumnIndex("height")));
							layerCursor.setCELLNAME(cursor.getString(cursor.getColumnIndex("cell_name")));
							layerCursor.setDIRECTION(UtilHandler.getInstance().toFloat(cursor.getString(cursor.getColumnIndex("azimuth")), Integer.MIN_VALUE));
							layerCursor.setJXXQJ(cursor.getString(cursor.getColumnIndex("downtilt_mach")));
							layerCursor.setDXQJ(cursor.getString(cursor.getColumnIndex("electricity_angle")));
							layerCursor.setCI(cursor.getString(cursor.getColumnIndex("ci")));
							layerCursor.setCity(cursor.getString(cursor.getColumnIndex("region_name")));
							layerCursor.setCountry(cursor.getString(cursor.getColumnIndex("city_name")));
							layerCursor.PCI = cursor.getString(cursor.getColumnIndex("pci"));
							layerCursor.setBand(cursor.getString(cursor.getColumnIndex("earfcndl")));
						}
						break;
					}
					layerCursor.setTabName(layer.getTabName());
					layerCursor.setType(layer.getType());
					layerCursor.setKey(layer.getKey());
					layerCursor.setKeyId(layer.getKeyId());
					list.add(layerCursor);
				}
			}
			cursor.close();
			return list;
		}
	}

	/**
	 * 查询对应的marker图层相关信息
	 * 
	 * @param layer
	 * @return
	 */
	public List<JJ_PciLayer> queryPciMarker(JJ_PciLayer layer, String mPindian) {
		synchronized (lockObject) {
			getDb(getOldPath());
			List<JJ_PciLayer> list = new ArrayList<JJ_PciLayer>();
			if (db == null) {
				return list;
			}
			double delLatLng = 0.00001;
			String whereSql = "";
			String orderSql = "";
			String tableName = layer.getTabName();
			String ci = layer.getCI();
			LatLng point = layer.getLlGps();
			switch (layer.getType()) {

			case 2:
				String[] arrays = ci.split("-");
				if (arrays.length > 3) {
					ci = arrays[2];
				}
				whereSql = "CI like " + "'%" + ci + "%'" + " and LON>='" + String.valueOf(point.longitude - delLatLng)
						+ "' and LAT>='" + String.valueOf(point.latitude - delLatLng) + "' and LON<='"
						+ String.valueOf(point.longitude + delLatLng) + "' and LAT<='"
						+ String.valueOf(point.latitude + delLatLng) + "'";
				orderSql = " order by CI asc";
				break;

			}
			// WybLog.syso(tableName + "--" + whereSql + "--" + orderSql);
			Cursor cursor = queryRows(tableName, null, whereSql, null, orderSql);
			if (cursor.getCount() > 0) {
				JJ_PciLayer layerCursor;
				while (cursor.moveToNext()) {

					String pindian = cursor.getString(cursor.getColumnIndex("earfcn"));
					// 只显示相同频点的数据
					if (pindian.equals(mPindian)) {
						layerCursor = new JJ_PciLayer();
						layerCursor.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
						switch (layer.getType()) {
						case 2:// LTE
							layerCursor.setLAT(cursor.getDouble(cursor.getColumnIndex("LAT")));
							layerCursor.setLON(cursor.getDouble(cursor.getColumnIndex("LON")));
							layerCursor.setLINEHIGH(cursor.getString(cursor.getColumnIndex("LINEHIGH")));
							layerCursor.setCELLNAME(cursor.getString(cursor.getColumnIndex("CELLNAME")));
							layerCursor.setDIRECTION(cursor.getFloat(cursor.getColumnIndex("DIRECTION")));
							layerCursor.setJXXQJ(cursor.getString(cursor.getColumnIndex("JXXQJ")));
							layerCursor.setDXQJ(cursor.getString(cursor.getColumnIndex("DXQJ")));
							layerCursor.setCI(cursor.getString(cursor.getColumnIndex("CI")));
							layerCursor.setCity(cursor.getString(cursor.getColumnIndex("CITY")));
							layerCursor.setPci(cursor.getInt(cursor.getColumnIndex("PCI")));
							layerCursor.setPindian(cursor.getString(cursor.getColumnIndex("earfcn")));
							layerCursor.setDeviceName(cursor.getString(cursor.getColumnIndex("DEVICENAME")));
							try {
								layerCursor.setTAC(cursor.getString(cursor.getColumnIndex("ENBAJ18")));
							} catch (Exception e) {
								// TODO: handle exception
							}

							break;

						default:
							break;
						}
						layerCursor.setTabName(layer.getTabName());
						layerCursor.setType(layer.getType());
						layerCursor.setKey(layer.getKey());
						layerCursor.setKeyId(layer.getKeyId());
						list.add(layerCursor);
					}
				}
			}
			cursor.close();
			return list;
		}
	}

	/**
	 * 搜索表里所有符合条件的记录中的指定字段值
	 * 
	 * @param table_name
	 *            表名
	 * @param column_names
	 *            字段名称数组
	 * @param selection
	 *            条件，如：column_name1='?' and column_name2='?'
	 * @param selectionArgs
	 *            替换?的值，new String[]{column_value1, column_value2}
	 * @param orderBy
	 *            排序，如： id desc /asc，不需要排序就填null
	 * @return Cursor 搜索结果
	 */
	private Cursor queryRows(String table_name, String[] column_names, String selection, String[] selectionArgs,
			String orderBy) {
		synchronized (lockObject) {
			Cursor cursor = null;
			if (db == null) {
				return null;
			}

			// cursor = db.query(table_name, column_names, selection,
			// selectionArgs, null, null, orderBy);
			StringBuffer sqlBuf = new StringBuffer("select ");
			if (column_names == null) {
				sqlBuf.append("*");
			} else {
				int length = column_names.length;
				if (length > 0) {
					for (int i = 0; i < length - 1; i++) {
						sqlBuf.append(column_names[i] + ", ");
					}
					sqlBuf.append(column_names[length - 1]);
				}
			}
			sqlBuf.append(" from " + table_name);
			if (selection != null && !"".equals(selection)) {
				sqlBuf.append(" where ");

				if (selection.contains("?")) {
					int len = selectionArgs.length;
					for (int i = 0; i < len; i++) {
						if (selectionArgs[i].length() <= 0) {
							selectionArgs[i] = "";
						}
						selection = selection.replaceFirst("\\?", "'" + selectionArgs[i] + "'");
					}
					sqlBuf.append(selection);
				} else {
					sqlBuf.append(selection);
				}
			}

			if (orderBy != null) {
				sqlBuf.append(" " + orderBy);
			}
			cursor = db.rawQuery(sqlBuf.toString(), null);

			return cursor;
		}
	}

	public String[] querySearch(String tabName, String queryKey, String where) {
		synchronized (lockObject) {
			getDb(getOldPath());
			if (db == null) {
				return null;
			}
			try {
				StringBuffer sbSql = new StringBuffer();
				sbSql.append("select distinct ");
				sbSql.append(queryKey);
				sbSql.append(" from ");
				sbSql.append(tabName);
				sbSql.append(" where ");
				sbSql.append(queryKey);
				sbSql.append(" is not null");
				if (where != null && !where.isEmpty()) {
					sbSql.append(" and ");
					sbSql.append(where);
				}
				Cursor mCursor = db.rawQuery(sbSql.toString(), null);

				if (mCursor != null && mCursor.getCount() > 0) {
					String[] arrStr = new String[mCursor.getCount()];
					int position = 0;
					while (mCursor.moveToNext()) {
						arrStr[position] = mCursor.getString(mCursor.getColumnIndex(queryKey));
						position += 1;
					}
					mCursor.close();
					return arrStr;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}
	}
	
	/**
	 * 查询不重复返回指定值
	 *  strSql select distinct WORKPD,earfcn from lte_cell where WORKPD is not null AND earfcn is not null
	 * @return
	 */
	public HashMap<String, LteBandItem> querySearchBand() {
		synchronized (lockObject) {
			getDb(getOldPath());
			if (db == null) {
				return null;
			}
			try {
				Cursor mCursor = db.rawQuery("select distinct WORKPD,earfcn from " + getTABLE_LTE_CELL() + " where WORKPD is not null AND earfcn is not null ORDER BY WORKPD ASC", null);
				if (mCursor != null && mCursor.getCount() > 0) {
					HashMap<String, LteBandItem> mHashMap = new HashMap<>();
					String WORKPD;
					String earfcn;
					String key;
					while (mCursor.moveToNext()) {
						WORKPD = mCursor.getString(mCursor.getColumnIndex("WORKPD"));
						earfcn = mCursor.getString(mCursor.getColumnIndex("earfcn"));
						if (WORKPD.contains("其它")) {
							key = "其它";
							if (mHashMap.get(key) == null) {
								mHashMap.put(key, new LteBandItem(WORKPD, earfcn,key,mHashMap.size()));
							}else {
								mHashMap.get(key).childMap.put(WORKPD + earfcn, new LteBandChild(WORKPD, earfcn));
							}
						}else if ("FG".equals(WORKPD) || WORKPD.length() == 1) {
							key = WORKPD + "频段";
							if (mHashMap.get(key) == null) {
								mHashMap.put(key, new LteBandItem(WORKPD, earfcn,key,mHashMap.size()));
							}else {
								mHashMap.get(key).childMap.put(WORKPD + earfcn, new LteBandChild(WORKPD, earfcn));
							}
						}else if (WORKPD.length() == 2) {
							key = WORKPD.substring(0, 1) + "频段";
							if (mHashMap.get(key) == null) {
								mHashMap.put(key, new LteBandItem(WORKPD, earfcn,key,mHashMap.size()));
							}else {
								mHashMap.get(key).childMap.put(WORKPD + earfcn, new LteBandChild(WORKPD, earfcn));
							}
						}else {
							String[] split = WORKPD.split("_");
							if (split.length > 1) {
								key = split[1] + "-" + split[0];
							}else {
								key = split[0];
							}
							if (mHashMap.get(key) == null) {
								mHashMap.put(key, new LteBandItem(WORKPD, earfcn,key,mHashMap.size()));
							}else {
								mHashMap.get(key).childMap.put(WORKPD + earfcn, new LteBandChild(WORKPD, earfcn));
							}
						}
					}
					mCursor.close();
					return mHashMap;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}
	}

	/**
	 * 查询对应基站详细信息
	 * 
	 * @param tabName
	 *            表名称
	 * @param _id
	 *            主键ID
	 * @param position
	 *            对应编码
	 */
	public WqLayerDetailInfo queryLayerDetail(String tabName, int _id, int position) {
		synchronized (lockObject) {
			getDb(getOldPath());
			if (db == null) {
				return null;
			}
			try {
				WqLayerDetailInfo detailInfo = new WqLayerDetailInfo();
				Cursor mCursor = db.rawQuery("select * from " + tabName + " where _id=" + _id, null);
				if (mCursor != null && mCursor.getCount() > 0) {
					while (mCursor.moveToNext()) {
						detailInfo.setType(position);
						if (position < 3) {
							detailInfo.setCI(mCursor.getString(mCursor.getColumnIndex("CI")));
							detailInfo.setCELLNAME(mCursor.getString(mCursor.getColumnIndex("CELLNAME")));
							detailInfo.setDEVICENAME(mCursor.getString(mCursor.getColumnIndex("DEVICENAME")));
							detailInfo.setXYTYPE(mCursor.getString(mCursor.getColumnIndex("XYTYPE")));
							detailInfo.setDISCT(mCursor.getString(mCursor.getColumnIndex("DISCT")));
							detailInfo.setJXXQJ(mCursor.getString(mCursor.getColumnIndex("JXXQJ")));
							detailInfo.setDXQJ(mCursor.getString(mCursor.getColumnIndex("DXQJ")));
						}
						// 0 GSM小区 、
						// 1 TDS小区、
						// 2 LTE小区
						// 3 LTE规划
						// 4-12 铁塔
						// 15-16 电信2G/4G
						// 26 5G小区
						switch (position) {
						case 0:
							detailInfo.setCITY(mCursor.getString(mCursor.getColumnIndex("CITY")));
							detailInfo.setAREA(mCursor.getString(mCursor.getColumnIndex("AREA")));
							detailInfo.setLON(mCursor.getString(mCursor.getColumnIndex("LON")));
							detailInfo.setLAT(mCursor.getString(mCursor.getColumnIndex("LAT")));
							detailInfo.setDIRECTION(mCursor.getString(mCursor.getColumnIndex("DIRECTION")));
							detailInfo.setLINEHIGH(mCursor.getString(mCursor.getColumnIndex("LINEHIGH")));
							detailInfo.setLAC(mCursor.getString(mCursor.getColumnIndex("LAC")));
							detailInfo.setWLNUM(mCursor.getString(mCursor.getColumnIndex("WLNUM")));
							detailInfo.setJZNUM(mCursor.getString(mCursor.getColumnIndex("JZNUM")));
							detailInfo.setBcchPD(mCursor.getString(mCursor.getColumnIndex("bcchPD")));
							break;
						case 1:
							detailInfo.setCITY(mCursor.getString(mCursor.getColumnIndex("CITY")));
							detailInfo.setAREA(mCursor.getString(mCursor.getColumnIndex("AREA")));
							detailInfo.setLON(mCursor.getString(mCursor.getColumnIndex("LON")));
							detailInfo.setLAT(mCursor.getString(mCursor.getColumnIndex("LAT")));
							detailInfo.setDIRECTION(mCursor.getString(mCursor.getColumnIndex("DIRECTION")));
							detailInfo.setLINEHIGH(mCursor.getString(mCursor.getColumnIndex("LINEHIGH")));
							detailInfo.setZRNUM(mCursor.getString(mCursor.getColumnIndex("ZRNUM")));
							detailInfo.setPD(mCursor.getString(mCursor.getColumnIndex("PD")));
							detailInfo.setLAC(mCursor.getString(mCursor.getColumnIndex("LAC")));
							break;
						case 2:
							detailInfo.setCITY(mCursor.getString(mCursor.getColumnIndex("CITY")));
							detailInfo.setAREA(mCursor.getString(mCursor.getColumnIndex("AREA")));
							detailInfo.setLON(mCursor.getString(mCursor.getColumnIndex("LON")));
							detailInfo.setLAT(mCursor.getString(mCursor.getColumnIndex("LAT")));
							detailInfo.setDIRECTION(mCursor.getString(mCursor.getColumnIndex("DIRECTION")));
							detailInfo.setLINEHIGH(mCursor.getString(mCursor.getColumnIndex("LINEHIGH")));
							detailInfo.setWORKPD(mCursor.getString(mCursor.getColumnIndex("WORKPD")));
							detailInfo.setEarfcn(mCursor.getString(mCursor.getColumnIndex("earfcn")));
							detailInfo.setPCI(mCursor.getString(mCursor.getColumnIndex("PCI")));
							detailInfo.TAC = mCursor.getString(mCursor.getColumnIndex("ENBAJ08"));
							try {
								detailInfo.setENBAJ12(mCursor.getString(mCursor.getColumnIndex("ENBAJ12")));
							} catch (Exception e) {

							}
							break;
						case 15:
							detailInfo.setCITY(mCursor.getString(mCursor.getColumnIndex("region_name")));
							detailInfo.setCELLNAME(mCursor.getString(mCursor.getColumnIndex("site_name")));
							detailInfo.setAREA_TYPE(mCursor.getString(mCursor.getColumnIndex("area_type")));
							detailInfo.setCREATE_TIME(mCursor.getString(mCursor.getColumnIndex("create_time")));
							detailInfo.seteNodeB_TYPE(mCursor.getString(mCursor.getColumnIndex("site_type")));
							detailInfo.setLON(mCursor.getString(mCursor.getColumnIndex("longitude")));
							detailInfo.setLAT(mCursor.getString(mCursor.getColumnIndex("latitude")));
							break;
						case 16:
							detailInfo.setLTE_TYPE(mCursor.getString(mCursor.getColumnIndex("lte_type")));
							detailInfo.setCITY(mCursor.getString(mCursor.getColumnIndex("region_name")));
							detailInfo.setCELLNAME(mCursor.getString(mCursor.getColumnIndex("site_name")));
							detailInfo.setAREA_TYPE(mCursor.getString(mCursor.getColumnIndex("area_type")));
							detailInfo.setCREATE_TIME(mCursor.getString(mCursor.getColumnIndex("create_time")));
							detailInfo.seteNodeB_TYPE(mCursor.getString(mCursor.getColumnIndex("site_type")));
							detailInfo.setLON(mCursor.getString(mCursor.getColumnIndex("longitude")));
							detailInfo.setLAT(mCursor.getString(mCursor.getColumnIndex("latitude")));
							// detailInfo.setNEAREST_SITE_NAME(mCursor.getString(mCursor.getColumnIndex("nearest_site_name")));
							// detailInfo.setNEAREST_DIS(mCursor.getString(mCursor.getColumnIndex("nearest_distance")));
							// detailInfo.setNEAREST_LAT(mCursor.getString(mCursor.getColumnIndex("nearest_latitude")));
							// detailInfo.setNEAREST_LON(mCursor.getString(mCursor.getColumnIndex("nearest_longitude")));
							break;
						case 3:
							detailInfo.setCITY(mCursor.getString(mCursor.getColumnIndex("CITY")));
							detailInfo.setAREA(mCursor.getString(mCursor.getColumnIndex("AREA")));
							detailInfo.setLON(mCursor.getString(mCursor.getColumnIndex("LON")));
							detailInfo.setLAT(mCursor.getString(mCursor.getColumnIndex("LAT")));
							detailInfo.setDIRECTION(mCursor.getString(mCursor.getColumnIndex("DIRECTION")));
							detailInfo.setLINEHIGH(mCursor.getString(mCursor.getColumnIndex("LINEHIGH")));
							detailInfo.setCI(mCursor.getString(mCursor.getColumnIndex("eNodeB_ID")));
							detailInfo.setCELLNAME(mCursor.getString(mCursor.getColumnIndex("eNodeB_NAME")));
							detailInfo.seteNodeB_TYPE(mCursor.getString(mCursor.getColumnIndex("eNodeB_TYPE")));
							detailInfo.seteNodeB_STATE(mCursor.getString(mCursor.getColumnIndex("eNodeB_STATE")));
							detailInfo.setDISCT(mCursor.getString(mCursor.getColumnIndex("AREATYPE")));
							detailInfo.setCELLCOUNT(mCursor.getString(mCursor.getColumnIndex("CELLCOUNT")));
							detailInfo.setDEVICENAME(mCursor.getString(mCursor.getColumnIndex("DEVICECJ")));
							detailInfo.setXMCHNAME(mCursor.getString(mCursor.getColumnIndex("XMCHNAME")));
							detailInfo.setXQJ(mCursor.getString(mCursor.getColumnIndex("XQJ")));
							detailInfo.setPD(mCursor.getString(mCursor.getColumnIndex("PD")));
							break;
						case 26://5G小区
							detailInfo.setLAT(mCursor.getString(mCursor.getColumnIndex("lat")));
							detailInfo.setLON(mCursor.getString(mCursor.getColumnIndex("lon")));
							detailInfo.setLINEHIGH(mCursor.getString(mCursor.getColumnIndex("ant_height")));//天线挂高
							detailInfo.setCELLNAME(mCursor.getString(mCursor.getColumnIndex("cell_name")));//小区名称
							detailInfo.setDIRECTION(mCursor.getString(mCursor.getColumnIndex("azumuth")));//方位角
							detailInfo.setXQJ(mCursor.getString(mCursor.getColumnIndex("downtilt")));//下倾角
							detailInfo.setEarfcn(mCursor.getString(mCursor.getColumnIndex("earfcn")));//频点
							detailInfo.setPCI(mCursor.getString(mCursor.getColumnIndex("pci")));//PCI
							detailInfo.setCI(mCursor.getString(mCursor.getColumnIndex("ci")));//CI
							detailInfo.TAC = mCursor.getString(mCursor.getColumnIndex("tac"));//TAc
							detailInfo.gnb = mCursor.getString(mCursor.getColumnIndex("gnb"));//GNB
							detailInfo.setCITY(mCursor.getString(mCursor.getColumnIndex("city_name")));//地市
							detailInfo.setAREA(mCursor.getString(mCursor.getColumnIndex("county_name")));//区县
							detailInfo.setDEVICENAME(mCursor.getString(mCursor.getColumnIndex("vendor_name")));//厂家
							detailInfo.band_width = mCursor.getString(mCursor.getColumnIndex("band_width"));//宽带
							break;
						default:// 铁塔
							if (position <= 12) {
								detailInfo.setCELLNAME(mCursor.getString(mCursor.getColumnIndex("SITE_NAME")));
								detailInfo.setSITE_CODE(mCursor.getString(mCursor.getColumnIndex("SITE_CODE")));
								detailInfo.setCITY(mCursor.getString(mCursor.getColumnIndex("CITY_NAME")));
								detailInfo.setAREA(mCursor.getString(mCursor.getColumnIndex("AREA_NAME")));
								detailInfo.setSITE_TYPE(mCursor.getString(mCursor.getColumnIndex("SITE_TYPE")));
								detailInfo.setLON(mCursor.getString(mCursor.getColumnIndex("LONGITUDE")));
								detailInfo.setLAT(mCursor.getString(mCursor.getColumnIndex("LATITUDE")));
								detailInfo.setSITE_ADDR(mCursor.getString(mCursor.getColumnIndex("SITE_ADDR")));
								detailInfo.setSITE_LEVEL(mCursor.getString(mCursor.getColumnIndex("SITE_LEVEL")));
								detailInfo
										.setMAINTAIN_STATE(mCursor.getString(mCursor.getColumnIndex("MAINTAIN_STATE")));
								detailInfo.setSCENE(mCursor.getString(mCursor.getColumnIndex("SCENE")));
								detailInfo.setTERRAIN(mCursor.getString(mCursor.getColumnIndex("TERRAIN")));
								detailInfo.setEQUITIES_ORG(mCursor.getString(mCursor.getColumnIndex("EQUITIES_ORG")));
								detailInfo.setIS_SHARE(mCursor.getString(mCursor.getColumnIndex("IS_SHARE")));
								detailInfo.setSHARE_ORG(mCursor.getString(mCursor.getColumnIndex("SHARE_ORG")));
							} else {
								// 高铁
								detailInfo.setCI(mCursor.getString(mCursor.getColumnIndex("CI")));
								detailInfo.setCELLNAME(mCursor.getString(mCursor.getColumnIndex("ENBAJ02")));
								detailInfo.setENODEBNAME(mCursor.getString(mCursor.getColumnIndex("ENODEBNAME")));
								detailInfo.setCITY(mCursor.getString(mCursor.getColumnIndex("REGION_NAME")));
								detailInfo.setAREA(mCursor.getString(mCursor.getColumnIndex("CITY_NAME")));
								detailInfo.setENBAG05(mCursor.getString(mCursor.getColumnIndex("ENBAG05")));
								detailInfo.setDEVICENAME(mCursor.getString(mCursor.getColumnIndex("VENDOR_NAME")));
								detailInfo.setIS_REMOTE(mCursor.getString(mCursor.getColumnIndex("IS_REMOTE")));
								detailInfo.setENBAE02(mCursor.getString(mCursor.getColumnIndex("ENBAE02")));
								detailInfo.setENBAE06(mCursor.getString(mCursor.getColumnIndex("ENBAE06")));
								detailInfo.setENBAE11(mCursor.getString(mCursor.getColumnIndex("ENBAE11")));
								detailInfo.setPOSITION(mCursor.getString(mCursor.getColumnIndex("POSITION")));
								detailInfo.setLON(mCursor.getString(mCursor.getColumnIndex("LONGITUDE")));
								detailInfo.setLAT(mCursor.getString(mCursor.getColumnIndex("LATITUDE")));
								detailInfo.setCOVER_TYPE(mCursor.getString(mCursor.getColumnIndex("COVER_TYPE")));
								detailInfo.setAZIMUTH1(mCursor.getString(mCursor.getColumnIndex("AZIMUTH1")));
								detailInfo.setAZIMUTH2(mCursor.getString(mCursor.getColumnIndex("AZIMUTH2")));
								detailInfo.setAZIMUTH3(mCursor.getString(mCursor.getColumnIndex("AZIMUTH3")));
								detailInfo.setTILT1(mCursor.getString(mCursor.getColumnIndex("TILT1")));
								detailInfo.setTILT2(mCursor.getString(mCursor.getColumnIndex("TILT2")));
								detailInfo.setTILT3(mCursor.getString(mCursor.getColumnIndex("TILT3")));
								detailInfo.setANT_HEIGHT1(mCursor.getString(mCursor.getColumnIndex("ANT_HEIGHT1")));
								detailInfo.setANT_HEIGHT2(mCursor.getString(mCursor.getColumnIndex("ANT_HEIGHT2")));
								detailInfo.setANT_HEIGHT3(mCursor.getString(mCursor.getColumnIndex("ANT_HEIGHT3")));
								detailInfo.setIF_ZOOMOUT(mCursor.getString(mCursor.getColumnIndex("IF_ZOOMOUT")));
								detailInfo.setENBHQ01(mCursor.getString(mCursor.getColumnIndex("ENBHQ01")));
								detailInfo.setENBHQ02(mCursor.getString(mCursor.getColumnIndex("ENBHQ02")));
								if (detailInfo.getENBHQ01().startsWith(".")) {
									detailInfo.setENBHQ01("0" + detailInfo.getENBHQ01());
								}
								if (detailInfo.getENBHQ02().startsWith(".")) {
									detailInfo.setENBHQ02("0" + detailInfo.getENBHQ01());
								}
							}
							break;
						}
						mCursor.close();
						return detailInfo;
					}
				}
			} catch (Exception e) {

			}
			return null;
		}
	}

	/**
	 * 查询对应基站详细信息
	 * 
	 * @param tabName
	 *            表名称
	 *            主键ID
	 * @param position
	 *            对应编码
	 */
	public List<WqLayerDetailInfo> queryLayerDetailByCi(String tabName, String ci, int position) {
		synchronized (lockObject) {
			getDb(getOldPath());
			if (db == null) {
				return null;
			}
			try {
				List<WqLayerDetailInfo> list = new ArrayList<WqLayerDetailInfo>();
				WqLayerDetailInfo detailInfo ;
				//460-00-429210-18
				Cursor mCursor = db.rawQuery("select * from " + tabName + " where CI=" + ci, null);
				WybLog.syso("---ci 查询条件："+tabName+","+ci);
				
				if (mCursor != null && mCursor.getCount() > 0) {
					while (mCursor.moveToNext()) {
						detailInfo = new WqLayerDetailInfo();
						detailInfo.setType(position);
						if (position < 3) {
							detailInfo.setCI(mCursor.getString(mCursor.getColumnIndex("CI")));
							detailInfo.setCELLNAME(mCursor.getString(mCursor.getColumnIndex("CELLNAME")));
							detailInfo.setDEVICENAME(mCursor.getString(mCursor.getColumnIndex("DEVICENAME")));
							detailInfo.setXYTYPE(mCursor.getString(mCursor.getColumnIndex("XYTYPE")));
							detailInfo.setDISCT(mCursor.getString(mCursor.getColumnIndex("DISCT")));
							detailInfo.setJXXQJ(mCursor.getString(mCursor.getColumnIndex("JXXQJ")));
							detailInfo.setDXQJ(mCursor.getString(mCursor.getColumnIndex("DXQJ")));
						}
						// 0 GSM小区 、
						// 1 TDS小区、
						// 2 LTE小区
						// 3 LTE规划
						// 4-12 铁塔
						// 15-16 电信2G/4G
						switch (position) {

						case 2:
							detailInfo.setCITY(mCursor.getString(mCursor.getColumnIndex("CITY")));
							detailInfo.setAREA(mCursor.getString(mCursor.getColumnIndex("AREA")));
							detailInfo.setLON(mCursor.getString(mCursor.getColumnIndex("LON")));
							detailInfo.setLAT(mCursor.getString(mCursor.getColumnIndex("LAT")));
							detailInfo.setDIRECTION(mCursor.getString(mCursor.getColumnIndex("DIRECTION")));
							detailInfo.setLINEHIGH(mCursor.getString(mCursor.getColumnIndex("LINEHIGH")));
							detailInfo.setWORKPD(mCursor.getString(mCursor.getColumnIndex("WORKPD")));
							detailInfo.setEarfcn(mCursor.getString(mCursor.getColumnIndex("earfcn")));
							detailInfo.setPCI(mCursor.getString(mCursor.getColumnIndex("PCI")));
							try {
								detailInfo.setENBAJ12(mCursor.getString(mCursor.getColumnIndex("ENBAJ12")));
							} catch (Exception e) {

							}

							break;
						}
						list.add(detailInfo);
					}
					mCursor.close();
					return list;
				}
			} catch (Exception e) {
				WybLog.syso("---ci 错误："+e.getMessage());
			}
			return null;
		}
	}

	/**
	 * 根据语句查询数据
	 * 
	 * @param strSql
	 * @return
	 * @throws Exception
	 */
	public Cursor querySql(String strSql) throws Exception {
		synchronized (lockObject) {
			getDb(getOldPath());
			if (db == null) {
				return null;
			}
			return db.rawQuery(strSql, null);
		}
	}

	private final String TABLE_NR_CELL = "t_5g_cm_cell";
	private final String TABLE_GSM_CELL = "gsm_cell";
	private final String TABLE_TDS_CELL = "tds_cell";
	private final String TABLE_LTE_CELL = "lte_cell";
	private final String TABLE_LTE_GUIHUA = "lte_guihua";
	private final String TABLE_TT_CMCC = "tt_site_cmcc";
	private final String TABLE_TT_CUN = "tt_site_cun";
	private final String TABLE_TT_CNET = "tt_site_cnet";
	private final String TABLE_TT_ALL = "tt_site_all";
	private final String TABLE_TT_YL = "tt_site_cmcc_cun";
	private final String TABLE_TT_YD = "tt_site_cmcc_cnet";
	private final String TABLE_TT_LD = "tt_site_cun_cnet";
	private final String TABLE_TT_YLD = "tt_site_cmcc_cun_cnet";
	private final String TABLE_TT_OTHER = "tt_site_other";
	private final String TABLE_LTE_CELL_PCI = "lte_cell_pci";
	/**
	 * 5G表
	 */
	public final String TABLE_5G = "t_5g_cm_cell";
	/**
	 * NB表
	 */
	public final String TABLE_NB = "tdl_nb_cm_cell";

	/**
	 * 判断表是否存在
	 * 
	 * @param table_name
	 * @return
	 */
	public boolean isExist(String table_name) {
		synchronized (lockObject) {
			getDb(getOldPath());
			if (db == null) {
				return false;
			}
			boolean bool = false;
			Cursor cursor = null;
			try {
				String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"
						+ table_name.trim() + "' ";
				cursor = db.rawQuery(sql, null);
				if (cursor != null && cursor.moveToNext()) {
					int count = cursor.getInt(0);
					if (count > 0) {
						bool = true;
					}
				}
				cursor.close();
			} catch (Exception e) {
				if (cursor != null) {
					cursor.close();
				}
				bool = false;
			}
			return bool;
		}
	}

	public String getTABLE_GSM_CELL() {
		return TABLE_GSM_CELL;
	}

	public String getTABLE_TDS_CELL() {
		return TABLE_TDS_CELL;
	}
	
	public String getTABLE_NR_CELL() {
		return TABLE_NR_CELL;
	}

	public String getTABLE_LTE_CELL() {
		return TABLE_LTE_CELL;
	}

	public String getTABLE_LTE_GUIHUA() {
		return TABLE_LTE_GUIHUA;
	}

	/*--------------铁塔---------------------*/
	public String getTABLE_TT_CMCC() {
		return TABLE_TT_CMCC;
	}

	public String getTABLE_TT_CUN() {
		return TABLE_TT_CUN;
	}

	public String getTABLE_TT_CNET() {
		return TABLE_TT_CNET;
	}

	public String getTABLE_TT_ALL() {
		return TABLE_TT_ALL;
	}

	public String getTABLE_TT_YL() {
		return TABLE_TT_YL;
	}

	public String getTABLE_TT_YD() {
		return TABLE_TT_YD;
	}

	public String getTABLE_TT_LD() {
		return TABLE_TT_LD;
	}

	public String getTABLE_TT_YLD() {
		return TABLE_TT_YLD;
	}

	public String getTABLE_TT_OTHER() {
		return TABLE_TT_OTHER;
	}

	/*-------------pci------------------*/
	public String getTABLE_LTE_CELL_PCI() {
		return TABLE_LTE_CELL_PCI;
	}
}
