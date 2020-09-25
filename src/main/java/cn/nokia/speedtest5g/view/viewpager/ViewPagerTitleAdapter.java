package cn.nokia.speedtest5g.view.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.view.viewpager.IndicatorViewPager.IndicatorFragmentPagerAdapter;

/**
 * 标题适配器
 * @author zwq
 *
 */
public class ViewPagerTitleAdapter extends IndicatorFragmentPagerAdapter {
	
	private LayoutInflater mInflater;
	
	private List<ViewPagerTitleItem> mArrList;
	
	public ViewPagerTitleAdapter(FragmentManager fragmentManager, LayoutInflater inflater, List<ViewPagerTitleItem> arrList) {
		super(fragmentManager);
		this.mInflater = inflater;
		this.mArrList  = arrList;
	}
	
	@Override
	public int getCount() {
		return mArrList == null ? 0 : mArrList.size();
	}

	@Override
	public View getViewForTab(int position, View convertView,ViewGroup container) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_viewpagertitle, container, false);
        }
        ((TextView) convertView).setText(mArrList.get(position).title);
		return convertView;
	}
	
	@Override
	public Fragment getFragmentForPage(int position) {
		return mArrList.get(position).fragment;
	}

	@Override
	public int getItemPosition(Object object) {
		return FragmentListPageAdapter.POSITION_NONE;
	}
}
