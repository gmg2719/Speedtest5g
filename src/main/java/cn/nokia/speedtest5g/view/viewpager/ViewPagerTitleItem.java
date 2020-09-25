package cn.nokia.speedtest5g.view.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ViewPagerTitleItem {
	
	public Fragment fragment;
	public String title;
	
	public ViewPagerTitleItem(Fragment fragment,String title,Bundle bundle){
		this.fragment = fragment;
		this.title	  = title;
		if (bundle != null) {
			fragment.setArguments(bundle);
		}
	}
}
