package cn.nokia.speedtest5g.app.adapter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity2.PhotoAlbumActivity.LocalFile;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 相册列表适配器
 * @author zwq
 *
 */
public class PhotoAlbumListAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	
	private DisplayImageOptions mOption;
	
	public PhotoAlbumListAdapter(LayoutInflater inflater,DisplayImageOptions option){
		this.mInflater = inflater;
		this.mOption   = option;
	}
	
	private String[] mArrName,mArrNumber,mArrUrl;
	
	public void setData(Map<String, List<LocalFile>> hashMap){
		if (hashMap != null) {
			mArrName     = new String[hashMap.size()];
			mArrNumber   = new String[hashMap.size()];
			mArrUrl      = new String[hashMap.size()];
			int position = 0;
			List<LocalFile> value;
			Iterator<Entry<String, List<LocalFile>>> iterator = hashMap.entrySet().iterator();
	        while (iterator.hasNext()) {
	        	Entry<String, List<LocalFile>> next = iterator.next();
	        	value 				 = next.getValue();
	        	if (value != null && value.size() > 0) {
	        		mArrName[position]   = next.getKey();
		        	mArrNumber[position] = value.size() + "张";
		        	mArrUrl[position]    = value.get(0).getPath() + "/" + value.get(0).getName();
				}
	        	position 			+= 1;
			}
		}else {
			mArrName = null;
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mArrName == null ? 0 : mArrName.length;
	}

	@Override
	public Object getItem(int position) {
		return mArrName[position];
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_photoalbum, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		}else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.tvName.setText(mArrName[position]);
		vh.tvNumber.setText(mArrNumber[position]);
		ImageLoader.getInstance().displayImage("file://" + mArrUrl[position], vh.iv, mOption);
		return convertView;
	}
	
	class ViewHolder{
		ImageView iv;
		TextView tvName,tvNumber;
		public ViewHolder(View v){
			iv 		 = (ImageView) v.findViewById(R.id.adapter_photoalbum_iv);
			tvName   = (TextView) v.findViewById(R.id.adapter_photoalbum_tv_name);
			tvNumber = (TextView) v.findViewById(R.id.adapter_photoalbum_tv_number);
		}
	}

}
