package cn.nokia.speedtest5g.speedtest.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.speedtest.SpeedRankingFragment;
import cn.nokia.speedtest5g.view.viewpager.FragmentListPageAdapter;
import cn.nokia.speedtest5g.view.viewpager.IndicatorViewPager.IndicatorFragmentPagerAdapter;

/**
 * 测速排行适配器
 * @author JQJ
 *
 */

public class SpeedRankingMainAdapter extends IndicatorFragmentPagerAdapter {

    public static final String WHAT_TYPE_WIFI = "WiFi";
    public static final String WHAT_TYPE_4G = "4G";
    public static final String WHAT_TYPE_5G = "5G";

    private LayoutInflater mInflater;
    private String[] mArrTitle;

    public SpeedRankingMainAdapter(Context mContext, FragmentManager fragmentManager, LayoutInflater inflater, ListenerBack listenerBack) {
        super(fragmentManager, listenerBack);
        this.mInflater = inflater;
        mArrTitle = new String[] { "WiFi", "4G", "5G" };
    }

    public void updateTitle(String[] arrTitle) {
        mArrTitle = arrTitle;
        notifyDataSetChanged();
    }

    public String getCurrentTitle(int position) {
        return position < 0 || position >= mArrTitle.length ? "" : mArrTitle[position];
    }

    @Override
    public int getCount() {
        return mArrTitle == null ? 0 : mArrTitle.length;
    }

    @Override
    public View getViewForTab(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.wq_adapter_sfdy_super, container, false);
        }
        TextView textView = (TextView) convertView;
        textView.setTextSize(16);
        textView.setText(mArrTitle[position % mArrTitle.length]);
        return convertView;
    }

    @Override
    public Fragment getFragmentForPage(int position) {
        Fragment mFragment = null;
        String type = WHAT_TYPE_WIFI;
        switch (position) {
            case 0:
                type = WHAT_TYPE_WIFI;
                mFragment = new SpeedRankingFragment();
                break;
            case 1:
                type = WHAT_TYPE_4G;
                mFragment = new SpeedRankingFragment();
                break;
            case 2:
                type = WHAT_TYPE_5G;
                mFragment = new SpeedRankingFragment();
                break;
        }
        if (mFragment != null) {
            Bundle bundle = new Bundle();
            bundle.putString("data", type);
            mFragment.setArguments(bundle);
        }
        return mFragment;
    }

    @Override
    public int getItemPosition(Object object) {
        return FragmentListPageAdapter.POSITION_NONE;
    }
}
