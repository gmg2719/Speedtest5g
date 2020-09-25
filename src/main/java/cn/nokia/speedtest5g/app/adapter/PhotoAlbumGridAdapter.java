package cn.nokia.speedtest5g.app.adapter;

import java.util.ArrayList;
import java.util.List;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity2.PhotoAlbumActivity.LocalFile;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 相册列表子数据适配器
 * @author zwq
 *
 */
public class PhotoAlbumGridAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	
	private DisplayImageOptions mOption;
	
	private List<LocalFile> mArrList;
	
	public PhotoAlbumGridAdapter(LayoutInflater inflater,DisplayImageOptions option){
		this.mInflater = inflater;
		this.mOption   = option;
		this.mArrList  = new ArrayList<LocalFile>();
	}
	
	
	public void setData(List<LocalFile> listFile){
		mArrList = listFile;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mArrList == null ? 0 : mArrList.size();
	}

	@Override
	public Object getItem(int position) {
		return mArrList.get(position);
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
		LocalFile localFile = (LocalFile) getItem(position);
		ImageLoader.getInstance().displayImage("file://" + localFile.getPath() + "/" + localFile.getName(), vh.iv, mOption);
		return convertView;
	}
	
	class ViewHolder{
		ImageView iv;
		public ViewHolder(View v){
			iv = (ImageView) v.findViewById(R.id.adapter_photoalbum_iv);
		}
	}

}
