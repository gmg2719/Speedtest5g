package cn.nokia.speedtest5g.app.uitl.ftp;

public class FtpUtil {
	
	private static FtpUtil fu = null;

	public static synchronized FtpUtil getInstances() {
		if (fu == null) {
			fu = new FtpUtil();
		}
		return fu;
	}
	
	/**
	 * 服务器名.
	 */
	private String hostName;

	/**
	 * 端口号
	 */
	private int serverPort;

	/**
	 * 用户名.
	 */
	private String userName;

	/**
	 * 密码.
	 */
	private String password;
	
	/**
	 * 设置参数
	 * @param hostName ip地址
	 * @param serverPort 端口号
	 * @param userName 用户名
	 * @param password 密码
	 */
	public void setIpPort(String hostName,int serverPort,String userName,String password){
		setHostName(hostName);
		setServerPort(serverPort);
		setUserName(userName);
		setPassword(password);
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
