package cn.nokia.speedtest5g.util.gps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.request.ViaPointsItem;
import cn.nokia.speedtest5g.app.request.ViaPointsRequest;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import com.android.volley.util.JsonHandler;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;

public class GPSUtil {

	private static GPSUtil gu = null;

	public synchronized static GPSUtil getInstances() {
		if (gu == null) {
			gu = new GPSUtil();
		}
		return gu;
	}
	
	/*------------------------------------根据像素获取经纬度---------------------------------------*/
	public LatLng proToLatLng(BaiduMap bMap,int x,int y){
		try {
			if (bMap != null) {
				Projection projection = bMap.getProjection();
				if (projection == null) {
					return null;
				}
				return projection.fromScreenLocation(new Point(x, y));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	/**
	 * 计算起点到终点方位角
	 * 请使用新的计算方式---getAngle方法
	 * @param startLatLng
	 * @param endLatLng
	 * @return
	 */
	@Deprecated
	public float getRotateAngle(LatLng startLatLng, LatLng endLatLng) {
		float rotateAngle = 0;
		// 右上角
		if (endLatLng.latitude >= startLatLng.latitude && endLatLng.longitude >= startLatLng.longitude) {
			rotateAngle = (float) (360f
					- (180 / Math.PI) * Math.atan(((endLatLng.longitude - startLatLng.longitude) * 100.8)
							/ ((endLatLng.latitude - startLatLng.latitude) * 111)));
			// 左上角
		} else if (endLatLng.latitude >= startLatLng.latitude && endLatLng.longitude <= startLatLng.longitude) {
			rotateAngle = (float) (360f
					- (180 / Math.PI) * Math.atan(((endLatLng.longitude - startLatLng.longitude) * 100.8)
							/ ((endLatLng.latitude - startLatLng.latitude) * 111)));
			// 下边（左下、右下）
		} else {
			rotateAngle = (float) (360f
					- (180 / Math.PI) * Math.atan(((endLatLng.longitude - startLatLng.longitude) * 100.8)
							/ ((endLatLng.latitude - startLatLng.latitude) * 111))
					+ 180f);
		}
		return rotateAngle;
	}
	
	/**
	 * 获取2点间的角度(这里取java角度《正右边开始算起顺时针》---如果地图的话角度逆时针)
	 * @param startLat
	 * @param startLon
	 * @param endLat
	 * @param endLon
	 * @param isJavaOrMap true顺时针java计算 false逆时针map计算
	 * @return
	 */
	public double getAngle(double startLat,double startLon,double endLat,double endLon,boolean isJavaOrMap){
        double dRotateAngle = Math.atan2(Math.abs(startLat - endLat), Math.abs(startLon - endLon));
        if (endLat >= startLat){
       	 if (endLon >= startLon){
       		 
            }else {
                dRotateAngle = Math.PI - dRotateAngle;
            }
        }else {
            if (endLon >= startLon) {
                dRotateAngle = 2 * Math.PI - dRotateAngle;
            }else                {
                dRotateAngle = Math.PI + dRotateAngle;
            }
        }
        dRotateAngle = dRotateAngle * 180f / Math.PI;
        return (isJavaOrMap ? 90 : 270) + dRotateAngle;
    }
	
	/**
	 * 根据角度和斜边求C点
	 * @param startLatLng 起点
	 * @param endLatLng 终点
	 * @param absLat 图片高度对应地图像素转纬度大小
	 * @param baiduMap
	 * @return
	 */
	public LatLng getXyToAngle(LatLng startLatLng, LatLng endLatLng,double absLat,BaiduMap baiduMap){
		double angle = getAngle(startLatLng.latitude,startLatLng.longitude, endLatLng.latitude,endLatLng.longitude,true);
		double radius = getRadius(endLatLng.latitude - startLatLng.latitude,endLatLng.longitude - startLatLng.longitude, angle);
		if (radius < 0) {
			radius -= absLat;
		}else {
			radius += absLat;
		}
		double endLat = (startLatLng.latitude + radius * Math.cos(-angle * Math.PI / 180f)); 
		double endLon = (startLatLng.longitude + radius * Math.sin(-angle * Math.PI / 180f));
		return new LatLng(endLat, endLon);
	}
	
	/**
	 * 根据角度求半径
	 * @param x 边缘点-中心点
	 * @param angle 角度
	 * @return
	 */
	public double getRadius(double x,double y,double angle){
		double radius = 0;
		if (angle == 90 || angle == 270) {
			radius = (y/Math.sin(angle * Math.PI/180f));
		}else {
			radius = (x/Math.cos(angle * Math.PI/180f));
		}
		return radius;
	}
	
	private Pattern patternLon,patternLat;
	/**
	 * 纬度--正则过滤
	 * @param lat
	 * @return
	 */
	public boolean matchesLat(double lat){
		if (patternLat == null) {
			patternLat = Pattern.compile("^(-?((90)|((([0-8]\\d)|(\\d{1}))(\\.\\d+)?)))$");
		}
		return patternLat.matcher(String.valueOf(lat)).matches();
	}
	
	/**
	 * 经度--正则过滤
	 * @param lat
	 * @return
	 */
	public boolean matchesLon(double lat){
		if (patternLon == null) {
			patternLon = Pattern.compile("^(-?((180)|(((1[0-7]\\d)|(\\d{1,2}))(\\.\\d+)?)))$");
		}
		return patternLon.matcher(String.valueOf(lat)).matches();
	}
	
	/**
	 *  获取当前屏蔽显示范围的坐标 GPS坐标系
	 * @param baiduMap 
	 * @param mapView
	 * @param move 偏移量 默认0.001
	 * @return [0]左上角经度 [1]右下角经度 [2]右下角纬度 [3]左上角纬度
	 */
	public double[] readProjection(BaiduMap baiduMap,View mapView,double move) {
		Projection projection = baiduMap.getProjection();
		if (projection == null) {
			return null;
		}
		LatLng leftP = projection.fromScreenLocation(new Point(0, 0));
		LatLng bottomP = projection.fromScreenLocation(new Point(mapView.getWidth(), mapView.getHeight()));
		if (bottomP == null || leftP == null) {
			return null;
		}

		Map<String, Double> maplp = GPSUtil.getInstances().bd2wgs(leftP.latitude, leftP.longitude);
		Map<String, Double> mapbp = GPSUtil.getInstances().bd2wgs(bottomP.latitude, bottomP.longitude);
		double[] _edges = new double[4];
		_edges[0] = maplp.get("lon") - move;
		_edges[1] = mapbp.get("lon") + move;
		_edges[2] = mapbp.get("lat") - move;
		_edges[3] = maplp.get("lat") + move;

		return _edges;
	}
	
	/**
	 * 根据经纬度获取坐标
	 * @param baiduMap
	 * @param latLng
	 * @return
	 */
	public Point toScreenLocation(BaiduMap baiduMap,LatLng latLng){
		Projection projection = baiduMap.getProjection();
		if (projection == null) {
			return null;
		}
		return projection.toScreenLocation(latLng);
	}
	
	/**
	 * 根据坐标获取经纬度
	 * @param baiduMap
	 * @return
	 */
	public LatLng fromScreenLocation(BaiduMap baiduMap,Point point){
		Projection projection = baiduMap.getProjection();
		if (projection == null) {
			return null;
		}
		return projection.fromScreenLocation(point);
	}

	private final int GPS_TYPE_CLOSD = 0;
	private final int GPS_TYPE_GPS = 1;
	private final int GPS_TYPE_AGPS = 2;
	private final int GPS_TYPE_GPS_AGPS = 3;
	
	// 经纬度转换工具---GPS转百度
	private CoordinateConverter mCoordinateCvt;
	/**
	 * GPS转百度经纬度方法
	 * 
	 * @return
	 */
	public LatLng toBdLatlng(LatLng gpsLatLng) {
		if (mCoordinateCvt == null) {
			mCoordinateCvt = new CoordinateConverter();
			mCoordinateCvt.from(CoordType.GPS);
		}
		mCoordinateCvt.coord(gpsLatLng);
		return mCoordinateCvt.convert();
	}
	
	/**
	 * 导航调用
	 * @param context
	 * @param gpsLat
	 * @param gpsLon
	 * @param viaPoints 途径点
	 */
	public void navigation(final Context context,double gpsLat,double gpsLon,List<ViaPointsItem> viaPoints){
		if (gpsLat <= 0 || gpsLon <= 0) {
			new CommonDialog(context).show("经纬度有误!");
			return;
		}
		//判断app是否已经安装
		PackageInfo packageInfoSmarttest = null;
		try {
			packageInfoSmarttest = context.getPackageManager().getPackageInfo("com.baidu.BaiduMap", 0);
		} catch (Exception e) {
			packageInfoSmarttest = null;
		}
		if (packageInfoSmarttest != null) {
			Intent intentNavigation = new Intent();
			LatLng bdLatlng = GPSUtil.getInstances().toBdLatlng(new LatLng(gpsLat, gpsLon));
			String strViaPoints = "";
			if (viaPoints != null) {
				strViaPoints = "&viaPoints=" + JsonHandler.getHandler().toJson(new ViaPointsRequest(viaPoints));
			}
			intentNavigation.setData(Uri.parse("baidumap://map/direction?origin=我的位置&coord_type=bd09ll&destination=" + bdLatlng.latitude + "," + bdLatlng.longitude + strViaPoints));
			context.startActivity(intentNavigation);
		}else {
			new CommonDialog(context).setButtonText("立即下载", "稍后再说").setListener(new ListenerBack() {
				
				@Override
				public void onListener(int type, Object object, boolean isTrue) {
					if (isTrue) {
						Intent intentNavigation = new Intent();
						intentNavigation.setAction("android.intent.action.VIEW");
						intentNavigation.setData(Uri.parse("http://map.baidu.com/zt/client/index/"));
						context.startActivity(intentNavigation);
					}
				}
			}).show("没有安装百度地图客户端，是否立即下载？");
		}
	}
	
	/**
	 * 导航调用
	 * @param context
	 * @param gpsLat
	 * @param gpsLon
	 */
	public void navigation(final Context context,double gpsLat,double gpsLon){
		navigation(context, gpsLat, gpsLon, null);
	}

	/**
	 * 百度转GPS经纬度方法
	 * 
	 * @param bdLatLng
	 * @return
	 */
	public LatLng toGpsLatLng(LatLng bdLatLng) {
//		double lon = bdLatLng.longitude;
//		double lat = bdLatLng.latitude;
		Map<String, Double> transform = bd2wgs(bdLatLng.latitude, bdLatLng.longitude);
//		double lontitude = lon - (((Double) transform.get("lon")).doubleValue() - lon);
//		double latitude = lat - (((Double) transform.get("lat")).doubleValue() - lat);
		return new LatLng((Double) transform.get("lat"), (Double) transform.get("lon"));
	}

	// 把经纬度转换成度分秒显示
	public String getLocationString(double input) {
		int du = (int) input;
		int fen = (((int) ((input - du) * 3600))) / 60;
		int miao = (((int) ((input - du) * 3600))) % 60;
		return String.valueOf(du) + "°" + String.valueOf(fen) + "′" + String.valueOf(miao) + "″";
	}

	/**
	 * 判断GPS开启状态
	 * 
	 * @param context
	 * @return true 表示开启
	 */
	public int GPSType(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean agps = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps && !agps) {
			return GPS_TYPE_GPS;
		} else if (!gps && agps) {
			return GPS_TYPE_AGPS;
		} else if (gps && agps) {
			return GPS_TYPE_GPS_AGPS;
		}
		return GPS_TYPE_CLOSD;
	}

	public boolean isOpen(Context context) {
		int type = GPSType(context);
		if (type == GPS_TYPE_GPS || type == GPS_TYPE_GPS_AGPS) {
			return true;
		}
		return false;
	}

	/**
	 * 跳转打开GPS界面
	 * 
	 */
	public void open(Activity context) {
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		context.startActivityForResult(intent, 0);
	}

	private final double pi = 3.14159265358979324D;// 圆周率
	private final double a = 6378245.0D;// WGS 长轴半径
	private final double ee = 0.00669342162296594323D;// WGS 偏心率的平方

	private boolean outOfChina(double lat, double lon) {
		if (lon < 72.004 || lon > 137.8347)
			return true;
		if (lat < 0.8293 || lat > 55.8271)
			return true;
		return false;
	}

	private double transformLat(double x, double y) {
		double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
		return ret;
	}

	private double transformLon(double x, double y) {
		double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
		return ret;
	}

	// 84->gcj02
	public Map<String, Double> transform(double lon, double lat) {
		HashMap<String, Double> localHashMap = new HashMap<String, Double>();
		if (outOfChina(lat, lon)) {
			localHashMap.put("lon", Double.valueOf(lon));
			localHashMap.put("lat", Double.valueOf(lat));
			return localHashMap;
		}
		double dLat = transformLat(lon - 105.0, lat - 35.0);
		double dLon = transformLon(lon - 105.0, lat - 35.0);
		double radLat = lat / 180.0 * pi;
		double magic = Math.sin(radLat);
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
		dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
		double mgLat = lat + dLat;
		double mgLon = lon + dLon;
		localHashMap.put("lon", mgLon);
		localHashMap.put("lat", mgLat);
		return localHashMap;
	}

	// gcj02-84
	public Map<String, Double> gcj2wgs(double lon, double lat) {
		Map<String, Double> localHashMap = new HashMap<String, Double>();
		double lontitude = lon - (((Double) transform(lon, lat).get("lon")).doubleValue() - lon);
		double latitude = (lat - (((Double) (transform(lon, lat)).get("lat")).doubleValue() - lat));
		localHashMap.put("lon", lontitude);
		localHashMap.put("lat", latitude);
		return localHashMap;
	}

	private final double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

	public Map<String, Double> gcj2bd(double gcj_lat, double gcj_lon) {
		Map<String, Double> localHashMap = new HashMap<String, Double>();
		double x = gcj_lon, y = gcj_lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		localHashMap.put("lon", z * Math.cos(theta) + 0.0065);
		localHashMap.put("lat", z * Math.sin(theta) + 0.006);
		return localHashMap;
	}

	public Map<String, Double> bd2gcj(double bd_lat, double bd_lon) {
		Map<String, Double> localHashMap = new HashMap<String, Double>();
		double x = bd_lon - 0.0065, y = bd_lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		localHashMap.put("lon", z * Math.cos(theta));
		localHashMap.put("lat", z * Math.sin(theta));
		return localHashMap;
	}

	public Map<String, Double> bd2wgs(double bd_lat, double bd_lon) {
		Map<String, Double> localHashMap = new HashMap<String, Double>();
		double x = bd_lon - 0.0065, y = bd_lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		double lon = z * Math.cos(theta);
		double lat = z * Math.sin(theta);
		Map<String, Double> transform = transform(lon, lat);
		double lontitude = lon - (((Double) transform.get("lon")).doubleValue() - lon);
		double latitude = lat - (((Double) transform.get("lat")).doubleValue() - lat);
		localHashMap.put("lon", lontitude);
		localHashMap.put("lat", latitude);
		return localHashMap;
	}
	
	/**
    * 两个经纬度之间的角度---道路测试旋转地图使用
    * @param lat_a 纬度1
    * @param lng_a 经度1
    * @param lat_b 纬度2
    * @param lng_b 经度2
    * @return
    */
   public float getAngleToMap(double lat_a, double lng_a, double lat_b, double lng_b) {

       double y = Math.sin(lng_b-lng_a) * Math.cos(lat_b);
       double x = Math.cos(lat_a)*Math.sin(lat_b) - Math.sin(lat_a)*Math.cos(lat_b)*Math.cos(lng_b-lng_a);
       double brng = Math.atan2(y, x);

       brng = Math.toDegrees(brng);
       if(brng < 0)
           brng = brng +360;
       return (float) brng;
   }
}
