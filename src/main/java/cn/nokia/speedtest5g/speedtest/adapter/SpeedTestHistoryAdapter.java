package cn.nokia.speedtest5g.speedtest.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FTPTestInfo;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.speedtest.SpeedTestHistoryActivity;

/**
 * 测速历史适配器
 * @author JQJ
 *
 */
public class SpeedTestHistoryAdapter extends BaseAdapter {

    private Activity mActivity = null;
    private List<Db_JJ_FTPTestInfo> mList = null;
    private LayoutInflater mInflater = null;
    private ListenerBack mListenerBack = null;

    public SpeedTestHistoryAdapter(Activity activity, ListenerBack listenerBack) {
        this.mActivity = activity;
        this.mInflater = LayoutInflater.from(mActivity);
        this.mListenerBack = listenerBack;
    }

    public void updateData(List<Db_JJ_FTPTestInfo> list){
        if(list != null){
            Collections.sort(list);
            this.mList = list;
            initIcon();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= getCount()) {
            return null;
        }
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void initIcon(){
        if (getCount() > 0) {
            for (int i = 0; i < mList.size(); i++) {
                // 模块来源
                String sourceType = mList.get(i).getSourceType();
                if ("速率测试工具".equals(sourceType) || "速率PK".equals(sourceType)) {
                    mList.get(i).modelIcon = R.drawable.icon_speed_test_source_type_slcs;
                } else if ("网络挑刺".equals(sourceType)) {
                    mList.get(i).modelIcon = R.drawable.icon_speed_test_source_type_wltc;
                } else if ("定点测试".equals(sourceType) || "室内扫楼".equals(sourceType)) {
                    mList.get(i).modelIcon = R.drawable.icon_speed_test_source_type_ddcs;
                } else if ("道路测试GISMAP".equals(sourceType) || "道路测试".equals(sourceType)) {
                    mList.get(i).modelIcon = R.drawable.icon_speed_test_source_type_dlcs;
                } else if ("室分单验".equals(sourceType)) {
                    mList.get(i).modelIcon = R.drawable.icon_speed_test_source_type_sfdy;
                } else if ("宏站CQT单验".equals(sourceType)) {
                    mList.get(i).modelIcon = R.drawable.icon_speed_test_source_type_hzdy;
                } else if ("投诉测试".equals(sourceType)) {
                    mList.get(i).modelIcon = R.drawable.icon_speed_test_source_type_tscs;
                } else if ("基站信号".equals(sourceType)) {
                    mList.get(i).modelIcon = R.drawable.icon_speed_test_source_type_jzxh;
                } else if ("满格宝".equals(sourceType)) {
                    mList.get(i).modelIcon = R.drawable.icon_speed_test_source_type_mgb;
                } else if ("遍历测试".equals(sourceType)) {
                    mList.get(i).modelIcon = R.drawable.icon_speed_test_source_type_blcs;
                }else {
                    mList.get(i).modelIcon = R.drawable.icon_speed_test_source_type_slcs;
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.jqj_speed_test_history_adapter, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Db_JJ_FTPTestInfo ftpTestInfo = (Db_JJ_FTPTestInfo) getItem(position);

        String netType = ftpTestInfo.getNetType();
        if(!TextUtils.isEmpty(netType)){
            if("wifi".equalsIgnoreCase(netType)){
                holder.mIvType.setImageResource(R.drawable.icon_speed_test_history_adapter_wifi);
            }else if(netType.contains("5G")){
                holder.mIvType.setImageResource(R.drawable.icon_speed_test_history_adapter_5g);
            }else if(netType.contains("4G")){
                holder.mIvType.setImageResource(R.drawable.icon_speed_test_history_adapter_4g);
            }else if(netType.contains("3G")){
                holder.mIvType.setImageResource(R.drawable.icon_speed_test_history_adapter_3g);
            }else if(netType.contains("2G")){
                holder.mIvType.setImageResource(R.drawable.icon_speed_test_history_adapter_2g);
            }
        }

        // 测试时间
        String time = ftpTestInfo.getTestBegin();
        if(!TextUtils.isEmpty(time)){
            String[] timeArr = time.split(" ");
            holder.mTvTime1.setText(timeArr[0]);
            holder.mTvTime2.setText(timeArr[1]);
        }
        // 下载平均/最大
        if (ftpTestInfo.getDownSpeedAvg() == 0) {
            holder.mTvDownAvg.setText("--");
        } else {
            holder.mTvDownAvg.setText(String.valueOf(ftpTestInfo.getDownSpeedAvg()));
        }
        // 上传平均/最大
        if (ftpTestInfo.getUpSpeedAvg() == 0) {
            holder.mTvUploadAvg.setText("--");
        } else {
            holder.mTvUploadAvg.setText(String.valueOf(ftpTestInfo.getUpSpeedAvg()));
        }
        holder.setListener(position, convertView);

        return convertView;
    }

    private class ViewHolder implements OnClickListener, OnLongClickListener{

        private TextView mTvTime1,mTvTime2,mTvUploadAvg,mTvDownAvg;
        private ImageView mIvType;

        public ViewHolder(View v){
            mIvType = (ImageView) v.findViewById(R.id.ftpHistoryAdapter_iv_type);
            mTvTime1 = (TextView) v.findViewById(R.id.ftpHistoryAdapter_tv_time1);
            mTvTime2 = (TextView) v.findViewById(R.id.ftpHistoryAdapter_tv_time2);
            mTvUploadAvg = (TextView) v.findViewById(R.id.ftpHistoryAdapter_tv_up);
            mTvDownAvg	 = (TextView) v.findViewById(R.id.ftpHistoryAdapter_tv_down);
        }

        public void setListener(int position,View v){
            v.setTag(R.id.idPosition, position);
            v.setOnLongClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListenerBack.onListener(SpeedTestHistoryActivity.WHAT_ITEM_ON_CLICK, getItem((int) v.getTag(R.id.idPosition)), true);
        }

        @Override
        public boolean onLongClick(View v) {
            mListenerBack.onListener(SpeedTestHistoryActivity.WHAT_ITEM_ON_LONG_CLICK, getItem((int) v.getTag(R.id.idPosition)), true);
            return true;
        }
    }
}
