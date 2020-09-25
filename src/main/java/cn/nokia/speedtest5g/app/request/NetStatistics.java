package cn.nokia.speedtest5g.app.request;

public class NetStatistics {
//	moduleId	模块ID
//	userId	操作人ID
//	optionTime	操作时间
//	mobileIdentity	手机唯一标识

	private String moduleId,userId,optionTime,mobileIdentity;

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOptionTime() {
		return optionTime;
	}

	public void setOptionTime(String optionTime) {
		this.optionTime = optionTime;
	}

	public String getMobileIdentity() {
		return mobileIdentity;
	}

	public void setMobileIdentity(String mobileIdentity) {
		this.mobileIdentity = mobileIdentity;
	}
}
