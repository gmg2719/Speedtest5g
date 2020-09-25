package cn.nokia.speedtest5g.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 通用列表适配器，实现公共方法
 * 
 * @author jinhaizheng
 */
public abstract class CommonListAdapter<T> extends BaseAdapter {
	public List<T> mList = new ArrayList<T>();
	public LayoutInflater mInflater;
	public Context mContext;

	public CommonListAdapter(Context context) {
		this(context, null);
	}

	public CommonListAdapter(Context context, List<T> list) {
		this.mList = list;
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
	}

	public void updateData(List<T> mList) {
		this.mList = mList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mList != null && mList.size() > 0) {
			return mList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mList != null && mList.size() > 0) {
			return mList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);
}
