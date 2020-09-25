package cn.nokia.speedtest5g.speedtest.bean;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.Serializable;
import java.util.Objects;

/**
 * 周边网速
 * @author JQJ
 *
 */
@SuppressWarnings("serial")
public class BeanPeriphery implements Serializable {

    public double longitude; //经度
    public double latitude; //纬度
    public String netType; //网络类型
    public float downSpeedAvg; //下载平均速率
    public String createTime; //创建时间

    private float dValue = 0.0003f; //30米

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeanPeriphery that = (BeanPeriphery) o;
        if((longitude < that.longitude + dValue && longitude > that.longitude - dValue) &&
                (latitude < that.latitude + dValue && latitude > that.latitude - dValue)){
            return true;
        }else{
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude);
    }
}
