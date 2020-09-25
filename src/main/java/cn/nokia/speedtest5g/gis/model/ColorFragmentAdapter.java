package cn.nokia.speedtest5g.gis.model;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


/**
 * 颜色适配器
 * @author zwq
 *
 */
public class ColorFragmentAdapter extends FragmentStatePagerAdapter {
	
	private HashMap<Integer, Fragment> fragmentsList;
	 
	private FragmentManager manager;
	
	public ColorFragmentAdapter(FragmentManager fm, HashMap<Integer,Fragment> fragments) {
        super(fm);
        this.manager = fm;
        this.fragmentsList = fragments;
    }

	@SuppressLint("UseSparseArrays")
	public void setData(HashMap<Integer,Fragment> fragments){
		if(this.fragmentsList != null){
			FragmentTransaction ft = manager.beginTransaction();
			for (int i = 0; i < fragmentsList.size(); i++) {
				ft.remove(fragmentsList.get(i));
			}
			ft.commit();
			ft=null;
			manager.executePendingTransactions();
		}
		this.fragmentsList = new HashMap<Integer, Fragment>();
		if (fragments != null && fragments.size() > 0) {
			Iterator<Entry<Integer, Fragment>> iter = fragments.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Integer, Fragment> entry = iter.next();
				this.fragmentsList.put(entry.getKey(), entry.getValue());
			}
		}
		notifyDataSetChanged();
	}
	
	public HashMap<Integer, Fragment> getData(){
		return fragmentsList;
	}
	
	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return fragmentsList.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragmentsList == null ? 0 : fragmentsList.size();
	}

	@Override  
	public int getItemPosition(Object object) {  
	    return POSITION_NONE;  
	}  

}
