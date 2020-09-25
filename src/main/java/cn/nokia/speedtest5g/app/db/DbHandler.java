package cn.nokia.speedtest5g.app.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.database.Cursor;
import android.text.TextUtils;
import com.android.volley.util.SharedPreHandler;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.bean.CityInfo;
import cn.nokia.speedtest5g.app.bean.WybBitmapDb;
import cn.nokia.speedtest5g.app.uitl.PathUtil;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;
/**
 * 本地数据库操作类
 * 
 * --增删该查
 * @author zwq
 *
 */
public class DbHandler {

	private Object lockObject = "lock";
	
	private final String DB_NAME_RUWANG = "speedtest5g.db";// 数据库名称
	
	private DataBase db;

	private static DbHandler dbHandler = null;

	private DbHandler() {

	}

	public static DbHandler getInstance() {
		if (dbHandler == null) {
			synchronized (DbHandler.class) {
				if (dbHandler == null) {
					dbHandler = new DbHandler();
				}
			}
		}
		return dbHandler;
	}

	/**
	 * 批量插入数据
	 * 
	 * 
	 * @param type
	 *            0城市 1室内扫楼具体位置
	 * @return
	 */
	public void insertType(int type) {
		synchronized (lockObject) {
			openDb();
			InputStream is = null;
			InputStreamReader read = null;
			BufferedReader bufferedReader = null;
			try {
				String lineTxt = null;
				String[] split;
				switch (type) {
				case 0:
					db.delete(CityInfo.class);
					is = SpeedTest5g.getContext().getResources().openRawResource(R.raw.city_code);
					read = new InputStreamReader(is, "UTF-8");// 考虑到编码格式
					bufferedReader = new BufferedReader(read);
					CityInfo info;
					while ((lineTxt = bufferedReader.readLine()) != null) {
						split = lineTxt.split(",");
						info = new CityInfo(split[1], split[0]);
						db.save(info);
					}
					SharedPreHandler.getShared(SpeedTest5g.getContext()).setBooleanShared("isCreadCitys", true);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				WybLog.syso("DbHandler","insertType错误：" + e.getMessage());
			}finally{
				try {
					if (is != null) {
						is.close();
					}
					if (read != null) {
						read.close();
					}
					if (bufferedReader != null) {
						bufferedReader.close();
					}
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
	}

	/**
	 * 插入数据
	 * 
	 * @return
	 */
	public long insert(Object object) {
		synchronized (lockObject) {
			openDb();
			try {
				return db.insert(object);
			} catch (Exception e) {
				WybLog.syso("DbHandler","insert错误：" + e.getMessage());
			}
			return -1;
		}
	}
	
	/**
	 * 判断表是否存在
	 * @return
	 */
	public boolean isExistTable(Class<?> c){
		synchronized (lockObject) {
			openDb();
			boolean bool = false;
			Cursor cursor = null;
			try {
				@SuppressWarnings("static-access")
				String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + db.getTableManager().getTableName(c) + "' "; 
				cursor = db.getReadableDatabase().rawQuery(sql, null);  
	            if(cursor!=null && cursor.moveToNext()){  
	                int count = cursor.getInt(0);  
	                if(count>0){  
	                	bool = true;  
	                }  
	            }
	            cursor.close();
			} catch(Exception e) {
				if(cursor!=null) {
					cursor.close();
				}
				bool = false;
			}
			return bool;
		}
	}
	
	@SuppressWarnings("static-access")
	/**
	 * 创建新表
	 * @param c
	 */
	public void createTable(Class<?> c){
		synchronized (lockObject) {
			openDb();
			try {
				if (!isExistTable(c)) {
					SQLBuilder.buildCreateTable(db.getTableManager().getTable(c)).execute(db.getWritableDatabase());
				}
			} catch (Exception e) {
				
			}
		}
	}
	
//	private boolean createTable(SQLiteDatabase db, EntityTable table)
//	  {
//	    return SQLBuilder.buildCreateTable(table).execute(db);
//	  }
	
	/**
	 * 保存数据---主键ID存在则更新，否则新增
	 * 
	 * @return
	 */
	public long save(Object object) {
		synchronized (lockObject) {
			openDb();
			try {
				return db.save(object);
			} catch (Exception e) {
				WybLog.syso("DbHandler","save错误：" + e.getMessage());
			}
			return -1;
		}
	}
	
	/**
	 * 根据条件查询数据
	 * @param c class表类
	 * @param key 条件---"id=? AND type=?"
	 * @param value 值--- new Object[]{1,2}
	 * @param groupBy 分组
	 * @param orderBy 排序---- id DESC或id ASC
	 * @return
	 */
	public ArrayList<?> queryObj(Class<?> c, String key, Object[] value,String groupBy,String orderBy) {
		synchronized (lockObject) {
			openDb();
			try {
				QueryBuilder qb = new QueryBuilder(c);
				qb.where(key, value);
				qb.groupBy(groupBy);
				qb.orderBy(orderBy);
				return db.query(qb);
			} catch (Exception e) {
				WybLog.syso("DbHandler","queryObj错误：" + e.getMessage());
			}
			return null;
		}
	}


	/**
	 * 根据对象的主键ID删除对象数据
	 * @param object
	 */
	public void deleteObj(Object object) {
		synchronized (lockObject) {
			openDb();
			try {
				db.delete(object);
			} catch (Exception e) {
				WybLog.syso("DbHandler","deleteObj错误：" + e.getMessage());
			}
		}
	}

	/**
	 * 根据类删除数据---此类的所有数据
	 * @param detaleClass
	 */
	public void deleteClass(Class<?> detaleClass) {
		synchronized (lockObject) {
			openDb();
			try {
				db.deleteAll(detaleClass);
			} catch (Exception e) {
				WybLog.syso("DbHandler","deleteClass错误：" + e.getMessage());
			}
		}
	}

	/**
	 * 条件删除数据
	 * @param c class表类
	 * @param key 条件---"id=? AND type=?"
	 * @param value 值--- new Object[]{1,2}
	 */
	public void deleteObj(Class<?> c, String key, Object[] value) {
		synchronized (lockObject) {
			openDb();
			try {
				db.delete(WhereBuilder.create(c).where(key,value));
			} catch (Exception e) {
				WybLog.syso("DbHandler","deleteObj错误：" + e.getMessage());
			}
		}
	}

	/**
	 * 根据ID更新数据
	 * @param object
	 * @return
	 */
	public int updateObj(Object object) {
		synchronized (lockObject) {
			openDb();
			try {
				return db.update(object, ConflictAlgorithm.Fail);
			} catch (Exception e) {
				WybLog.syso("DbHandler","updateObj错误：" + e.getMessage());
			}
			return -1;
		}
	}

	/**
	 * 指定条数根据条件查询
	 * @param t 查询对象类
	 * @param start 开始游标
	 * @param length 结束游标
	 * @param key 查询条件
	 * @param arrValue 查询条件值
	 * @param orderBy 排序
	 * @return
	 */
	public ArrayList<?> queryObj(Class<?> t, int start, int length,String key, Object[] arrValue,String orderBy) {
		synchronized (lockObject) {
			openDb();
			try {
				QueryBuilder qb = new QueryBuilder(t);
				qb.where(key,arrValue);
				qb.orderBy(orderBy);
				qb.limit(start, length);
				return db.query(qb);
			} catch (Exception e) {
				WybLog.syso("DbHandler","queryObj错误：" + e.getMessage());
			}
			return null;
		}
	}

	/**
	 * 查询class类的所有数据条数
	 * @param t
	 * @return
	 */
	public long queryCount(Class<?> t) {
		synchronized (lockObject) {
			openDb();
			try {
				return db.queryCount(t);
			} catch (Exception e) {
				WybLog.syso("DbHandler","queryCount错误：" + e.getMessage());
			}
			return 0;
		}
	}

	/**
	 * 查询对应数量
	 * @param c class表类
	 * @param key 条件---"id=? AND type=?"
	 * @param value 值--- new Object[]{1,2}
	 * @return
	 */
	public long queryCount(Class<?> c, String key, Object[] value) {
		synchronized (lockObject) {
			openDb();
			try {
				return db.queryCount(new QueryBuilder(c).where(key, value));
			} catch (Exception e) {
				WybLog.syso("DbHandler","queryCount错误：" + e.getMessage());
			}
			return 0;
		}
	}
	
	/**
	 * 查询对应数量
	 * @param key 条件---"id=? AND type=?"
	 * @param value 值--- new Object[]{1,2}
	 * @return
	 */
	public long queryBitCount(String key, Object[] value) {
		synchronized (lockObject) {
			openDb();
			try {
				ArrayList<WybBitmapDb> arrBit = db.query(new QueryBuilder(WybBitmapDb.class).where(key, value));
				if (arrBit != null && arrBit.size() > 0) {
					WybBitmapDb mWybBitmapDb;
					for (int i = arrBit.size() - 1; i >= 0; i--) {
						mWybBitmapDb = arrBit.get(i);
						if (mWybBitmapDb.getPath().endsWith("/")) {
							if (!PathUtil.getInstances().isExitFile(mWybBitmapDb.getPath() + mWybBitmapDb.getName()) && 
									TextUtils.isEmpty(mWybBitmapDb.getNetUrl())) {
								db.delete(mWybBitmapDb);
								arrBit.remove(i);
							}
						}else {
							if (!PathUtil.getInstances().isExitFile(mWybBitmapDb.getPath() + "/" + mWybBitmapDb.getName()) && 
									TextUtils.isEmpty(mWybBitmapDb.getNetUrl())) {
								db.delete(mWybBitmapDb);
								arrBit.remove(i);
							}
						}
					}
					return arrBit.size();
				}
			} catch (Exception e) {
				WybLog.syso("DbHandler","queryCount错误：" + e.getMessage());
			}
			return 0;
		}
	}

	/**
	 * 查询对应对象的所有数据
	 * 
	 * @param c
	 *            "superTime " + QueryBuilder.DESC
	 * @return
	 */
	public ArrayList<?> queryObj(Class<?> c, String orderBy) {
		synchronized (lockObject) {
			openDb();
			try {
				return db.query(new QueryBuilder(c).orderBy(orderBy));
			} catch (Exception e) {
				WybLog.syso("DbHandler","queryObj错误：" + e.getMessage());
			}
			return null;
		}
	}

	/**
	 * 查询class表所有数据
	 * @param c
	 * @return
	 */
	public ArrayList<?> queryAll(Class<?> c) {
		synchronized (lockObject) {
			openDb();
			try {
				return db.query(c);
			} catch (Exception e) {
				WybLog.syso("DbHandler","queryAll错误：" + e.getMessage());
			}
			return null;
		}
	}

	/**
	 * 根据条件查询class表数据
	 * @param c class表类
	 * @param key 条件---"id=? AND type=?"
	 * @param value 值--- new Object[]{1,2}
	 * @return
	 */
	public ArrayList<?> queryObj(Class<?> c, String key, Object[] value) {
		synchronized (lockObject) {
			openDb();
			try {
				return db.query(new QueryBuilder(c).where(key, value));
			} catch (Exception e) {
				WybLog.syso("DbHandler","queryObj错误：" + e.getMessage());
			}
			return null;
		}
	}
	
	/**
	 * 查询单一对象
	 * @param c
	 * @param key
	 * @param value
	 * @return
	 */
	public Object queryObjOne(Class<?> c, String key, Object[] value) {
		synchronized (lockObject) {
			openDb();
			try {
				ArrayList<Object> query = db.query(new QueryBuilder(c).where(key, value));
				return (query != null && query.size() > 0) ? query.get(0) : null;
			} catch (Exception e) {
				WybLog.syso("DbHandler","queryObj错误：" + e.getMessage());
			}
			return null;
		}
	}
	
	/**
	 * 查询单一对象
	 * @param key
	 * @param value
	 * @return
	 */
	public WybBitmapDb queryBitOne(String key, Object[] value) {
		synchronized (lockObject) {
			openDb();
			try {
				ArrayList<WybBitmapDb> queryArr = db.query(new QueryBuilder(WybBitmapDb.class).where(key, value));
				if (queryArr != null && queryArr.size() > 0) {
					WybBitmapDb mWybBitmapDb = queryArr.get(0);
					if (mWybBitmapDb.getPath().endsWith("/")) {
						if (PathUtil.getInstances().isExitFile(mWybBitmapDb.getPath() + mWybBitmapDb.getName())) {
							mWybBitmapDb.setExist(true);
							return mWybBitmapDb;
						}else if(!TextUtils.isEmpty(mWybBitmapDb.getNetUrl())){
							return mWybBitmapDb;
						}else {
							db.delete(mWybBitmapDb);
						}
					}else {
						if (PathUtil.getInstances().isExitFile(mWybBitmapDb.getPath() + "/" + mWybBitmapDb.getName())) {
							mWybBitmapDb.setExist(true);
							return mWybBitmapDb;
						}else if(!TextUtils.isEmpty(mWybBitmapDb.getNetUrl())){
							return mWybBitmapDb;
						}else {
							db.delete(mWybBitmapDb);
						}
					}
				}
			} catch (Exception e) {
				WybLog.syso("DbHandler","queryObj错误：" + e.getMessage());
			}
			return null;
		}
	}
	
	/**
	 * 根据条件查询具体条数
	 * @param c
	 * @param key
	 * @param value
	 * @return
	 */
	public ArrayList<?> queryObjLimit(Class<?> c, String key, Object[] value,int limitStarat,int limitEnd) {
		synchronized (lockObject) {
			openDb();
			try {
				return db.query(new QueryBuilder(c).limit(limitStarat, limitEnd).where(key, value));
			} catch (Exception e) {
				WybLog.syso("DbHandler","queryObj错误：" + e.getMessage());
			}
			return null;
		}
	}

	/**
	 * 根据条件查询class表数据
	 * @param c class表类
	 * @param key 条件---"id=? AND type=?"
	 * @param value 值--- new Object[]{1,2}
	 * @param order 排序----id DESC降或id ASC升
	 * @return
	 */
	public ArrayList<?> queryObj(Class<?> c, String key, Object[] value, String order) {
		synchronized (lockObject) {
			openDb();
			try {
				return db.query(new QueryBuilder(c).where(key, value).orderBy(order));
			} catch (Exception e) {
				WybLog.syso("DbHandler","queryObj错误：" + e.getMessage());
			}
			return null;
		}
	}

	/**
	 * 删除表并创建新表
	 * 
	 */
	@SuppressWarnings("static-access")
	public void dropTableToCreat(Class<?> c) {
		synchronized (lockObject) {
			openDb();
			try {
				if (isExistTable(c)) {
					db.dropTable(db.getTableManager().getTableName(c));
				}
				SQLBuilder.buildCreateTable(db.getTableManager().getTable(c)).execute(db.getWritableDatabase());
			} catch (Exception e) {
				WybLog.syso("DbHandler","dropTable错误：" + e.getMessage());
			}
		}
	}
	
	/**
	 * 初始化打开数据库
	 */
	private void openDb(){
		if (db == null) {
//			db = LiteOrm.newCascadeInstance(MyApplication.getContext(), DB_NAME_RUWANG);
			db = LiteOrm.newSingleInstance(SpeedTest5g.getContext(), DB_NAME_RUWANG);//全局保存单例
		}
	}
	
	/**
	 * 删除数据库文件
	 */
	public void deleteDb(){
		openDb();
		PathUtil.getInstances().deteleFile(db.getWritableDatabase().getPath());
		closeDb();
		db = null;
	}
	
	/**
	 * 退出程序的时候关闭数据库释放内存
	 */
	public void closeDb(){
		try {
			WybLog.syso("DbHandler","关闭db");
			if (db != null) {
				db.close();
			}
		} catch (Exception e) {
			
		}
	}
}