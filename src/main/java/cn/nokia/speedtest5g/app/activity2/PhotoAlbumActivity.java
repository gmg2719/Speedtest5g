package cn.nokia.speedtest5g.app.activity2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.adapter.PhotoAlbumGridAdapter;
import cn.nokia.speedtest5g.app.adapter.PhotoAlbumListAdapter;
import cn.nokia.speedtest5g.app.enums.EnumRequest;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 本地相册照片选择
 * @author zwq
 *
 */
public class PhotoAlbumActivity extends BaseActionBarActivity {

	/**
	 *  DisplayImageOptions是用于设置图片显示的类
	 */
	private DisplayImageOptions mOptions; 

	private List<LocalFile> paths;

	private Map<String, List<LocalFile>> folders;

	//大图遍历字段
	private static final String[] STORE_IMAGES = {
		MediaStore.Images.Media._ID,
		MediaStore.Images.Media.DATA,
		MediaStore.Images.Media.ORIENTATION
	};
	//小图遍历字段
	//    private static final String[] THUMBNAIL_STORE_IMAGE = {
	//            MediaStore.Images.Thumbnails._ID,
	//            MediaStore.Images.Thumbnails.DATA
	//    };

	private GridView mGridView;

	private ListView mListView;

	private PhotoAlbumListAdapter mAdapterListView;

	private PhotoAlbumGridAdapter mAdapterGridView;

	private String actionTo;

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			mAdapterListView.setData(folders);
			mListView.setVisibility(View.VISIBLE);
			dismissMyDialog();
			return true;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photoalbum);
		initOptions();
		init("本地图片",true);
		initDataPhoto();
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);
		actionTo = getIntent().getStringExtra("actionTo");

		mGridView = (GridView) findViewById(R.id.photoalbum_gridview);
		mListView = (ListView) findViewById(R.id.photoalbum_listview);

		mAdapterListView = new PhotoAlbumListAdapter(getLayoutInflater(), mOptions);
		mAdapterGridView = new PhotoAlbumGridAdapter(getLayoutInflater(), mOptions);

		mListView.setAdapter(mAdapterListView);
		mGridView.setAdapter(mAdapterGridView);

		mListView.setOnItemClickListener(listItemClickListener);
		mGridView.setOnItemClickListener(gridItemClickListener);
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		if (type == EnumRequest.DIALOG_NET_DISMISS.toInt()) {
			onBackPressed();
		}
	}

	@Override
	public void onBackPressed() {
		if (mGridView.getVisibility() == View.VISIBLE) {
			mGridView.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
			return;
		}
		mHandler = null;
		super.onBackPressed();
	}

	private OnItemClickListener listItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mAdapterGridView.setData(folders.get(parent.getAdapter().getItem(position)));
			mGridView.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
	};

	private OnItemClickListener gridItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			LocalFile mLocalFile = (LocalFile) parent.getAdapter().getItem(position);
			Intent intent = new Intent(actionTo == null || actionTo.isEmpty() ? TypeKey.getInstance().ACTION_TRAVERSE() : actionTo);
			intent.putExtra("path", mLocalFile.getPath());
			intent.putExtra("name", mLocalFile.getName());
			intent.putExtra("photo", true);
			sendBroadcast(intent);
			PhotoAlbumActivity.this.finish();
		}
	};

	/**
	 * 初始化获取本地相册数据
	 */
	private void initDataPhoto(){
		showMyDialog();
		ivDialog.setListener(PhotoAlbumActivity.this);
		new Thread(){
			@Override
			public void run() {
				paths = new ArrayList<LocalFile>();
				folders = new HashMap<String, List<LocalFile>>();
				//获取大图的游标
				Cursor cursor = PhotoAlbumActivity.this.getContentResolver().query(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  // 大图URI
						STORE_IMAGES,   // 字段
						null,         // No where clause
						null,         // No where clause
						MediaStore.Images.Media.DATE_TAKEN + " DESC"); //根据时间升序
				if (cursor == null)
					return;
				//		        int id;
				String path;
				File file;
				while (cursor.moveToNext()) {
					try {
						//		        		id = cursor.getInt(0);//大图ID
						path = cursor.getString(1);//大图路径
						file = new File(path);
						//判断大图是否存在
						if (file.exists()) {
							//小图URI
							//			                String thumbUri = getThumbnail(id, path);
							//获取大图URI
							//			                String uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
							//			                        appendPath(Integer.toString(id)).build().toString();
							//			                if(isEmpty(uri))
							//			                    continue;
							//			                if (isEmpty(thumbUri))
							//			                    thumbUri = uri;
							//获取目录名
							String folder = file.getParentFile().getName();

							LocalFile localFile = new LocalFile();
							//			                localFile.setOriginalUri(uri);
							//			                localFile.setThumbnailUri(thumbUri);
							localFile.setName(file.getName());
							localFile.setPath(file.getParent());
							//			                int degree = cursor.getInt(2);
							//			                if (degree != 0) {
							//			                    degree = degree + 180;
							//			                }
							//			                localFile.setOrientation(360-degree);

							paths.add(localFile);
							//判断文件夹是否已经存在
							if (folders.containsKey(folder)) {
								folders.get(folder).add(localFile);
							} else {
								List<LocalFile> files = new ArrayList<LocalFile>();
								files.add(localFile);
								folders.put(folder, files);
							}
						}
					} catch (Exception e) {
					}
				}
				folders.put("所有图片", paths);
				cursor.close();
				if (mHandler != null) {
					mHandler.sendEmptyMessage(0);
				}
			}
		}.start();
	}

	//	private String getThumbnail(int id, String path) {
	//        //获取大图的缩略图
	//        Cursor cursor = PhotoAlbumActivity.this.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
	//                THUMBNAIL_STORE_IMAGE,
	//                MediaStore.Images.Thumbnails.IMAGE_ID + " = ?",
	//                new String[]{id + ""},
	//                null);
	//        if (cursor.getCount() > 0) {
	//            cursor.moveToFirst();
	//            int thumId = cursor.getInt(0);
	//            String uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI.buildUpon().
	//                    appendPath(Integer.toString(thumId)).build().toString();
	//            cursor.close();
	//            return uri;
	//        }
	//        cursor.close();
	//        return null;
	//    }
	//	
	//	private boolean isEmpty(String input) {
	//		if (input == null || "".equals(input))
	//			return true;
	//
	//		for (int i = 0; i < input.length(); i++) {
	//			char c = input.charAt(i);
	//			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
	//				return false;
	//			}
	//		}
	//		return true;
	//	}

	private void initOptions(){
		mOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.camera)//加载开始默认的图片   
		.showImageForEmptyUri(R.drawable.none)  // 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.none)  //加载图片出现问题，会显示该图片
		.cacheInMemory(false)//内存缓存--设置下载的图片是否缓存在内存中
		.cacheOnDisk(false)//磁盘缓存--设置下载的图片是否缓存在SD卡中----/sdcard/Android/data/[package_name]/cache
		.considerExifParams(false)
		.bitmapConfig(Bitmap.Config.RGB_565)
		//				.displayer(new RoundedBitmapDisplayer(20)) //图片圆角显示，值为整数
		.build();
	}

	public class LocalFile {
		private String name;//图片名称
		private String path;//图片地址
		private String originalUri;//原图URI
		private String thumbnailUri;//缩略图URI
		private int orientation;//图片旋转角度

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getThumbnailUri() {
			return thumbnailUri;
		}

		public void setThumbnailUri(String thumbnailUri) {
			this.thumbnailUri = thumbnailUri;
		}

		public String getOriginalUri() {
			return originalUri;
		}

		public void setOriginalUri(String originalUri) {
			this.originalUri = originalUri;
		}

		public int getOrientation() {
			return orientation;
		}

		public void setOrientation(int exifOrientation) {
			orientation =  exifOrientation;
		}
	}
}
