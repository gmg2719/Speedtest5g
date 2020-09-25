package cn.nokia.speedtest5g.speedtest.util;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.speedtest.bean.BeanPeriphery;
import cn.nokia.speedtest5g.util.TimeUtil;
import cn.nokia.speedtest5g.util.gps.GPSUtil;

/**
 * 满格宝 读取图层信息并画出
 *
 * @author JQJ
 *
 */
public class SpeedWideDisplayLayerRunnable implements Runnable {

    private BaiduMap mBaiduMap = null;
    private ArrayList<BeanPeriphery> mPeripheryList = null;

    private Handler mHandler = null;
    private int mClearMapWhat;
    private int mLoadOverWhat;

    private boolean mIsWifi = true;
    private boolean mIs4g = false;
    private boolean mIs5g = false;

    public SpeedWideDisplayLayerRunnable(BaiduMap bdMap, ArrayList<BeanPeriphery> list, boolean isWifi, boolean is4g, boolean is5g,
                                         Handler handler, int clearMapWhat, int loadOverWhat) {
        this.mBaiduMap = bdMap;
        this.mPeripheryList = list;
        this.mIsWifi = isWifi;
        this.mIs4g = is4g;
        this.mIs5g = is5g;
        this.mHandler = handler;
        this.mClearMapWhat = clearMapWhat;
        this.mLoadOverWhat = loadOverWhat;
    }

    @Override
    public void run() {
        try {
            if(mPeripheryList.size() <= 0){
                return;
            }

            if(mHandler != null){
                mHandler.sendEmptyMessage(mClearMapWhat);
            }

            SystemClock.sleep(250);

            //数据过滤
            ArrayList<BeanPeriphery> wifiList = new ArrayList<BeanPeriphery>();
            ArrayList<BeanPeriphery> _2gList = new ArrayList<BeanPeriphery>();
            ArrayList<BeanPeriphery> _3gList = new ArrayList<BeanPeriphery>();
            ArrayList<BeanPeriphery> _4gList = new ArrayList<BeanPeriphery>();
            ArrayList<BeanPeriphery> _5gList = new ArrayList<BeanPeriphery>();
            for(BeanPeriphery beanPeriphery: mPeripheryList){
                if(beanPeriphery.netType.contains("WiFi") || beanPeriphery.netType.contains("WIFI") ||
                        beanPeriphery.netType.contains("wifi")){
                    wifiList.add(beanPeriphery);
                }else if(beanPeriphery.netType.contains("2G") || beanPeriphery.netType.contains("2g")){
                    _2gList.add(beanPeriphery);
                }else if(beanPeriphery.netType.contains("3G") || beanPeriphery.netType.contains("3g")){
                    _3gList.add(beanPeriphery);
                }else if(beanPeriphery.netType.contains("4G") || beanPeriphery.netType.contains("4g")){
                    _4gList.add(beanPeriphery);
                }else if(beanPeriphery.netType.contains("5G") || beanPeriphery.netType.contains("5g")){
                    _5gList.add(beanPeriphery);
                }
            }
            //降序排序
            descOrderByCreateTime(wifiList);
            descOrderByCreateTime(_2gList);
            descOrderByCreateTime(_3gList);
            descOrderByCreateTime(_4gList);
            descOrderByCreateTime(_5gList);

            //去除经纬度相近的
            ArrayList<BeanPeriphery> wifiDeleteList = removeDuplicate(wifiList);
            ArrayList<BeanPeriphery> _2gDeleteList = removeDuplicate(_2gList);
            ArrayList<BeanPeriphery> _3gDeleteList = removeDuplicate(_3gList);
            ArrayList<BeanPeriphery> _4gDeleteList = removeDuplicate(_4gList);
            ArrayList<BeanPeriphery> _5gDeleteList = removeDuplicate(_5gList);

            showLayer(_2gDeleteList);
            showLayer(_3gDeleteList);
            if(mIs4g){
                showLayer(_4gDeleteList);
            }
            if(mIs5g){
                showLayer(_5gDeleteList);
            }
            if(mIsWifi){
                showLayer(wifiDeleteList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<BeanPeriphery> removeDuplicate(ArrayList<BeanPeriphery> list){
        ArrayList<BeanPeriphery> listTemp = new ArrayList<BeanPeriphery>();
        for(int i=0;i<list.size();i++){
            if(!listTemp.contains(list.get(i))){
                listTemp.add(list.get(i));
            }
        }
        return listTemp;
    }

    private void descOrderByCreateTime(ArrayList<BeanPeriphery> list){
        if(list.size() <= 0){
            return;
        }

        Collections.sort(list, new Comparator<BeanPeriphery>() {
            @Override
            public int compare(BeanPeriphery b1, BeanPeriphery b2) {
                long time1 = TimeUtil.getInstance().stringToLong(b1.createTime);
                long time2 = TimeUtil.getInstance().stringToLong(b2.createTime);
                if (time1 < time2){
                    return 1;
                }else if(time1 == time2){
                    return 0;
                }
                return -1;
            }
        });
    }

    private void descOrderByDownSpeed(ArrayList<BeanPeriphery> list){
        if(list.size() <= 0){
            return;
        }

        Collections.sort(list, new Comparator<BeanPeriphery>() {
            @Override
            public int compare(BeanPeriphery b1, BeanPeriphery b2) {
                if (b1.downSpeedAvg < b2.downSpeedAvg){
                    return 1;
                }else if(b1.downSpeedAvg == b2.downSpeedAvg){
                    return 0;
                }
                return -1;
            }
        });
    }

    /**
     * 处理数据，并显示图层
     */
    private void showLayer(List<BeanPeriphery> list) {
        for (BeanPeriphery beanPeriphery : list) {
            LatLng latlng = new LatLng(beanPeriphery.latitude, beanPeriphery.longitude);
            LatLng bdlatlng = GPSUtil.getInstances().toBdLatlng(latlng);
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(changeView2Drawble(beanPeriphery));;
            OverlayOptions options = new MarkerOptions()
                    .position(new LatLng(bdlatlng.latitude, bdlatlng.longitude))//设置位置
                    .icon(bitmap);
            mBaiduMap.addOverlay(options);
        }

        if(mHandler != null){
            mHandler.sendEmptyMessage(mLoadOverWhat);
        }
    }

    /*
     **把自定义的布局文件转成Bitmap
     */
    private Bitmap changeView2Drawble(BeanPeriphery beanPeriphery) {
        View view = LayoutInflater.from(SpeedTest5g.getContext()).inflate(R.layout.jqj_speed_wide_layer_dialog, null);
        //显示数字，如小区人数
        ImageView tvType = view.findViewById(R.id.speed_wide_layer_iv_type);
        //显示文字，如小区名称
        TextView tvValue = view.findViewById(R.id.speed_wide_layer_tv_value);

        if(beanPeriphery != null){
            if(beanPeriphery.netType.contains("WiFi") || beanPeriphery.netType.contains("WIFI") ||
                    beanPeriphery.netType.contains("wifi")){
                tvType.setImageResource(R.drawable.icon_speed_wide_wifi_flag);
            }else if(beanPeriphery.netType.contains("2G") || beanPeriphery.netType.contains("2g")){
                tvType.setImageResource(R.drawable.icon_speed_wide_2g_flag);
            }else if(beanPeriphery.netType.contains("3G") || beanPeriphery.netType.contains("3g")){
                tvType.setImageResource(R.drawable.icon_speed_wide_3g_flag);
            }else if(beanPeriphery.netType.contains("4G") || beanPeriphery.netType.contains("4g")){
                tvType.setImageResource(R.drawable.icon_speed_wide_4g_flag);
            }else if(beanPeriphery.netType.contains("5G") || beanPeriphery.netType.contains("5g")){
                tvType.setImageResource(R.drawable.icon_speed_wide_5g_flag);
            }

            tvValue.setText(beanPeriphery.downSpeedAvg + "Mbps");
        }

        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        //获取到图片，这样就可以添加到Map上
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        return bitmap;
    }
}
