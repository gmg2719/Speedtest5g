package cn.nokia.speedtest5g.app.uitl.ftp;

/**
 * 状态...
 * @author zwq
 *
 */
public class FtpState {

	/**
	 * ftp连接失败
	 */
	public static final int FTP_CONNECT_FAIL = -1;
	
	/**
	 * "ftp连接成功"
	 */
	public static final int FTP_CONNECT_SUCCESSS = 0; 
	
	/**
	 * "ftp断开连接"
	 */
	public static final int FTP_DISCONNECT_SUCCESS = 1;
	
	/**
	 * "ftp上文件不存在"
	 */
	public static final int FTP_FILE_NOTEXISTS = 2;
	
	/**
	 * "ftp文件上传成功"
	 */
	public static final int FTP_UPLOAD_SUCCESS = 3;
	
	/**
	 * "ftp文件上传失败"
	 */
	public static final int FTP_UPLOAD_FAIL = 4;
	
	/**
	 * "ftp文件正在上传"
	 */
	public static final int FTP_UPLOAD_LOADING = 5;
	
	/**
	 * "ftp文件正在下载"
	 */
	public static final int FTP_DOWN_LOADING = 6;
	
	/**
	 * "ftp文件下载成功"
	 */
	public static final int FTP_DOWN_SUCCESS = 7;
	
	/**
	 * "ftp文件下载失败"
	 */
	public static final int FTP_DOWN_FAIL = 8;
	
	/**
	 * "ftp文件删除成功"
	 */
	public static final int FTP_DELETEFILE_SUCCESS = 9;
	
	/**
	 * "ftp文件删除失败"
	 */
	public static final int FTP_DELETEFILE_FAIL = 10;
}
