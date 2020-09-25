package cn.nokia.speedtest5g.speedtest.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.speedtest.bean.BeanAppFtpConfig;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestUtil;

/**
 * ftp设置适配器
 * @author JQJ
 *
 */
public class SpeedTestSetAdapter extends BaseAdapter {

    private Activity mActivity = null;
    private List<BeanAppFtpConfig> mList = null;
    private LayoutInflater mInflater = null;
    private ListenerBack mListenerBack = null;

    public SpeedTestSetAdapter(Activity activity, ListenerBack listenerBack) {
        this.mActivity = activity;
        this.mInflater = LayoutInflater.from(mActivity);
        this.mListenerBack = listenerBack;
    }

    public void updateData(List<BeanAppFtpConfig> list){
        if(list != null){
            this.mList = list;
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.jqj_speed_test_set_adapter, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BeanAppFtpConfig config = (BeanAppFtpConfig) getItem(position);
        holder.mTvTitle1.setText(config.hostType);
        holder.mTvTitle2.setText(config.province + "-" + config.city);
        holder.mProgressBar.setProgress(config.performance);
        holder.mTvTitle3.setText(SpeedTestUtil.getInstance().getLevelByPerformance(config.performance));
        holder.setListener(position, convertView);

        return convertView;
    }

    private class ViewHolder implements OnClickListener{

        private TextView mTvTitle1,mTvTitle2;
        private ProgressBar mProgressBar = null;
        private TextView mTvTitle3 = null;

        public ViewHolder(View v){
            mTvTitle1 = (TextView) v.findViewById(R.id.speed_test_set_adapter_tv_title1);
            mTvTitle2 = (TextView) v.findViewById(R.id.speed_test_set_adapter_tv_title2);
            mProgressBar = (ProgressBar) v.findViewById(R.id.speed_test_set_adapter_pb_value);
            mTvTitle3 = (TextView) v.findViewById(R.id.speed_test_set_adapter_tv_title3);
        }

        public void setListener(int position,View v){
            v.setTag(R.id.idPosition, position);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListenerBack.onListener(R.id.speed_test_set_adapter_ll_content, getItem((int) v.getTag(R.id.idPosition)), true);
        }
    }
}
