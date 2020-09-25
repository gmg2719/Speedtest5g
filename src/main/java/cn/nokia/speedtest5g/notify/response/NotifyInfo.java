package cn.nokia.speedtest5g.notify.response;

import java.io.Serializable;

import cn.nokia.speedtest5g.util.TimeUtil;

import android.text.TextUtils;

/**
 * 公告信息
 *
 * @author jinhaizheng
 */
public class NotifyInfo implements Serializable, Comparable<NotifyInfo> {
	// 公告类型
	private String NOTICE_TYPE;;
	// 公告ID
	private String ID;
	// 公告标题
	private String NOTICE_TITLE;
	// 公告内容,
	private String NOTICE_CONTENT;
	// 公告更新时间,
	private String UPDATE_TIME;
	// 公告影响地市
	private String REGION;
	// 公告影响范围
	private String RANGE;
	// 公告影响开始时间
	private String START_TIME;
	// 公告影预计恢复时间
	private String END_TIME;
	// 公告影实际恢复时间
	private String RECOVER_TIME;
	// 公告影响现象
	private String PHENOMENON;
	// 公告影响口径
	private String CALIBER;
	// 公告是否已读
	private boolean isRead;
	// 是否选中
	private boolean isChecked = false;

	public String getNOTICE_TYPE() {
		return NOTICE_TYPE;
	}

	public void setNOTICE_TYPE(String NOTICE_TYPE) {
		this.NOTICE_TYPE = NOTICE_TYPE;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public String getNOTICE_TITLE() {
		return NOTICE_TITLE;
	}

	public void setNOTICE_TITLE(String NOTICE_TITLE) {
		this.NOTICE_TITLE = NOTICE_TITLE;
	}

	public String getNOTICE_CONTENT() {
		return NOTICE_CONTENT;
	}

	public void setNOTICE_CONTENT(String NOTICE_CONTENT) {
		this.NOTICE_CONTENT = NOTICE_CONTENT;
	}

	public String getUPDATE_TIME() {
		return UPDATE_TIME;
	}

	public void setUPDATE_TIME(String UPDATE_TIME) {
		this.UPDATE_TIME = UPDATE_TIME;
	}

	public String getREGION() {
		return REGION;
	}

	public void setREGION(String REGION) {
		this.REGION = REGION;
	}

	public String getRANGE() {
		return RANGE;
	}

	public void setRANGE(String RANGE) {
		this.RANGE = RANGE;
	}

	public String getSTART_TIME() {
		return START_TIME;
	}

	public void setSTART_TIME(String START_TIME) {
		this.START_TIME = START_TIME;
	}

	public String getEND_TIME() {
		return END_TIME;
	}

	public void setEND_TIME(String END_TIME) {
		this.END_TIME = END_TIME;
	}

	public String getRECOVER_TIME() {
		return RECOVER_TIME;
	}

	public void setRECOVER_TIME(String rECOVER_TIME) {
		RECOVER_TIME = rECOVER_TIME;
	}

	public String getPHENOMENON() {
		return PHENOMENON;
	}

	public void setPHENOMENON(String PHENOMENON) {
		this.PHENOMENON = PHENOMENON;
	}

	public String getCALIBER() {
		return CALIBER;
	}

	public void setCALIBER(String CALIBER) {
		this.CALIBER = CALIBER;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		result = prime * result + ((NOTICE_CONTENT == null) ? 0 : NOTICE_CONTENT.hashCode());
		result = prime * result + ((NOTICE_TITLE == null) ? 0 : NOTICE_TITLE.hashCode());
		result = prime * result + ((NOTICE_TYPE == null) ? 0 : NOTICE_TYPE.hashCode());
		result = prime * result + ((UPDATE_TIME == null) ? 0 : UPDATE_TIME.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NotifyInfo other = (NotifyInfo) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		if (NOTICE_CONTENT == null) {
			if (other.NOTICE_CONTENT != null)
				return false;
		} else if (!NOTICE_CONTENT.equals(other.NOTICE_CONTENT))
			return false;
		if (NOTICE_TITLE == null) {
			if (other.NOTICE_TITLE != null)
				return false;
		} else if (!NOTICE_TITLE.equals(other.NOTICE_TITLE))
			return false;
		if (NOTICE_TYPE == null) {
			if (other.NOTICE_TYPE != null)
				return false;
		} else if (!NOTICE_TYPE.equals(other.NOTICE_TYPE))
			return false;
		if (UPDATE_TIME == null) {
			if (other.UPDATE_TIME != null)
				return false;
		} else if (!UPDATE_TIME.equals(other.UPDATE_TIME))
			return false;
		return true;
	}

	public long getUpdateTime() {
		long updateTime = 0;
		if (!TextUtils.isEmpty(UPDATE_TIME)) {
			updateTime = TimeUtil.getInstance().stringToLong(UPDATE_TIME);
		}
		return updateTime;
	}

	/*
	 * (non-Javadoc) 2.9.4版开始按公告列表时间排序
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(NotifyInfo another) {
		// if (another.isRead() == false && this.isRead() == true) {
		// return 1;
		//
		// } else if (another.isRead() == true && this.isRead() == false) {
		// // this未读排在前面，another和this顺序对调
		// return -1;
		//
		// } else {
		return (int) (another.getUpdateTime() - this.getUpdateTime());
		// }
	}
}