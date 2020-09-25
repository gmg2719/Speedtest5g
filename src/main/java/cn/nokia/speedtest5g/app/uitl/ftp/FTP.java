package cn.nokia.speedtest5g.app.uitl.ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FTP {

	/**
	 * FTP连接.
	 */
	private FTPClient ftpClient;
	
	/**
	 * 服务器名.
	 */
	private String hostName = "";

	/**
	 * 端口号
	 */
	private int serverPort = -1;

	/**
	 * 用户名.
	 */
	private String userName = "";

	/**
	 * 密码.
	 */
	private String password = "";
	
	/**
	 * 设置参数
	 * @param hostName ip地址
	 * @param serverPort 端口号
	 * @param userName 用户名
	 * @param password 密码
	 */
	public FTP(String hostName,int serverPort,String userName,String password){
		this.ftpClient = new FTPClient();
		this.hostName = hostName;
		this.serverPort = serverPort;
		this.userName = userName;
		this.password = password;
	}

	public FTP() {
		this.ftpClient = new FTPClient();
	}
	//每一次上传的大小
	private int sizeEvery  = 1024;
	
	public FTP(int every) {
		this.sizeEvery = every;
		this.ftpClient = new FTPClient();
	}
	
	public FTP(int every,String hostName,int serverPort,String userName,String password) {
		this.sizeEvery = every;
		this.ftpClient = new FTPClient();
		this.hostName = hostName;
		this.serverPort = serverPort;
		this.userName = userName;
		this.password = password;
	}

	// -------------------------------------------------------文件上传方法------------------------------------------------

	/**
	 * 上传单个文件.
	 * 
	 * @param localFile
	 *            本地文件
	 * @param remotePath
	 *            FTP目录
	 * @param listener
	 *            监听器
	 * @throws IOException
	 */
	public void uploadSingleFile(File singleFile, String remotePath,
			UploadProgressListener listener) throws IOException {

		// 上传之前初始化
		this.uploadBeforeOperate(remotePath, listener);

		boolean flag;
		flag = uploadingSingle(singleFile, listener);
		if (flag) {
			listener.onUploadProgress(FtpState.FTP_UPLOAD_SUCCESS, 0,
					singleFile);
		} else {
			listener.onUploadProgress(FtpState.FTP_UPLOAD_FAIL, 0,
					singleFile);
		}

		// 上传完成之后关闭连接
		this.uploadAfterOperate(listener);
	}

	/**
	 * 上传多个文件.
	 * 
	 * @param localFile
	 *            本地文件
	 * @param remotePath
	 *            FTP目录
	 * @param listener
	 *            监听器
	 * @throws IOException
	 */
	public void uploadMultiFile(LinkedList<File> fileList, String remotePath,
			UploadProgressListener listener) throws IOException {

		// 上传之前初始化
		this.uploadBeforeOperate(remotePath, listener);

		boolean flag;

		for (File singleFile : fileList) {
			flag = uploadingSingle(singleFile, listener);
			if (flag) {
				listener.onUploadProgress(FtpState.FTP_UPLOAD_SUCCESS, 0,
						singleFile);
			} else {
				listener.onUploadProgress(FtpState.FTP_UPLOAD_FAIL, 0,
						singleFile);
			}
		}

		// 上传完成之后关闭连接
		this.uploadAfterOperate(listener);
	}

	private ProgressInputStream progressInput;
	private BufferedInputStream buffIn;
	/**
	 * 上传单个文件.
	 * 
	 * @param localFile
	 *            本地文件
	 * @return true上传成功, false上传失败
	 * @throws IOException
	 */
	private boolean uploadingSingle(File localFile,
			UploadProgressListener listener) throws IOException {
		boolean flag = true;
		// 不带进度的方式
		// // 创建输入流
		// InputStream inputStream = new FileInputStream(localFile);
		// // 上传单个文件
		// flag = ftpClient.storeFile(localFile.getName(), inputStream);
		// // 关闭文件流
		// inputStream.close();

		// 带有进度的方式
		buffIn = new BufferedInputStream(
				new FileInputStream(localFile));
		progressInput = new ProgressInputStream(buffIn,
				listener, localFile);
		//中文
		flag = ftpClient.storeFile(new String(localFile.getName().getBytes("GBK"),"gb2312"), progressInput);
		buffIn.close();

		return flag;
	}
	
	/**
	 * 上传文件之前初始化相关参数
	 * 
	 * @param remotePath
	 *            FTP目录
	 * @param listener
	 *            监听器
	 * @throws IOException
	 */
	private void uploadBeforeOperate(String remotePath,
			UploadProgressListener listener) throws IOException {

		// 打开FTP服务
		try {
			this.openConnect();
			listener.onUploadProgress(FtpState.FTP_CONNECT_SUCCESSS, 0,
					null);
		} catch (IOException e1) {
			e1.printStackTrace();
			listener.onUploadProgress(FtpState.FTP_CONNECT_FAIL, 0, null);
			return;
		}
		ftpClient.setBufferSize(sizeEvery);
		// 设置模式
		ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
		// FTP下创建文件夹//中文
		ftpClient.makeDirectory(new String(remotePath.getBytes("GBK"),"gb2312"));
		// 改变FTP目录
		ftpClient.changeWorkingDirectory(remotePath);
		// 上传单个文件

	}

	/**
	 * 上传完成之后关闭连接
	 * 
	 * @param listener
	 * @throws IOException
	 */
	private void uploadAfterOperate(UploadProgressListener listener)
			throws IOException {
		this.closeConnect();
		listener.onUploadProgress(FtpState.FTP_DISCONNECT_SUCCESS, 0, null);
	}

	// -------------------------------------------------------文件下载方法------------------------------------------------

	/**
	 * 下载单个文件，可实现断点下载.
	 * 
	 * @param serverPath
	 *            Ftp目录及文件路径
	 * @param localPath
	 *            本地目录
	 * @param fileName       
	 *            下载之后的文件名称
	 * @param listener
	 *            监听器
	 * @throws IOException
	 */
	public void downloadSingleFile(String serverPath, String localPath, String fileName, DownLoadProgressListener listener)
			throws Exception {

		// 打开FTP服务
		try {
			this.openConnect();
			listener.onDownLoadProgress(FtpState.FTP_CONNECT_SUCCESSS, 0, null);
		} catch (IOException e1) {
			e1.printStackTrace();
			listener.onDownLoadProgress(FtpState.FTP_CONNECT_FAIL, 0, null);
			return;
		}

		// 先判断服务器文件是否存在
		FTPFile[] files = ftpClient.listFiles(serverPath);
		if (files.length == 0) {
			listener.onDownLoadProgress(FtpState.FTP_FILE_NOTEXISTS, 0, null);
			return;
		}

		//创建本地文件夹
		File mkFile = new File(localPath);
		if (!mkFile.exists()) {
			mkFile.mkdirs();
		}

		localPath = localPath + fileName;
//		// 接着判断下载的文件是否能断点下载
//		long serverSize = files[0].getSize(); // 获取远程文件的长度
//		File localFile = new File(localPath);
//		long localSize = 0;
//		if (localFile.exists()) {
//			localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
//			if (localSize >= serverSize) {
//				File file = new File(localPath);
//				file.delete();
//			}
//		}
		
		//判断文件存在就删除
		long serverSize = files[0].getSize(); // 获取远程文件的长度
		File localFile = new File(localPath);
		long localSize = 0;
		if (localFile.exists()) {
			localFile.delete();
			localFile = new File(localPath);
			if (!localFile.exists()) {
				localFile.createNewFile();
			}
		}
		isBreak = false;
		// 进度
		long step = serverSize / 100;
		long process = 0;
		long currentSize = 0;
		// 开始准备下载文件
		OutputStream out = new FileOutputStream(localFile, true);
		ftpClient.setRestartOffset(localSize);
		InputStream input = ftpClient.retrieveFileStream(serverPath);
		byte[] b = new byte[sizeEvery];
		int length = 0;
		while ((length = input.read(b)) != -1 && !isBreak) {
			out.write(b, 0, length);
			currentSize = currentSize + length;
			if (currentSize / step != process) {
				process = currentSize / step;
//				if (process % 5 == 0) {  //每隔%5的进度返回一次
//					listener.onDownLoadProgress(FtpState.FTP_DOWN_LOADING, process, null);
//				}
				listener.onDownLoadProgress(FtpState.FTP_DOWN_LOADING, process, null);
			}
			listener.onDownLoadSumProgress(length,currentSize, serverSize);
		}
		out.flush();
		out.close();
		input.close();
		if (isBreak) {
			ftpClient.completePendingCommand();
			return;
		}
		// 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
		if (ftpClient.completePendingCommand()) {
			listener.onDownLoadProgress(FtpState.FTP_DOWN_SUCCESS, 0, new File(localPath));
		} else {
			listener.onDownLoadProgress(FtpState.FTP_DOWN_FAIL, 0, null);
		}

		// 下载完成之后关闭连接
		this.closeConnect();
		listener.onDownLoadProgress(FtpState.FTP_DISCONNECT_SUCCESS, 0, null);

		return;
	}
	
	/**
	 * 下载多个文件
	 * @param serverPath
	 * @param localPath
	 * @param listener
	 * @throws Exception
	 */
	public void downloadMutiFile(String serverPath, String localPath, DownLoadProgressListener listener) throws Exception{  

		// 打开FTP服务
		try {
			this.openConnect();
			ftpClient.setControlEncoding("UTF-8");
			listener.onDownLoadProgress(FtpState.FTP_CONNECT_SUCCESSS, 0, null);
		} catch (IOException e1) {
			e1.printStackTrace();
			listener.onDownLoadProgress(FtpState.FTP_CONNECT_FAIL, 0, null);
			return;
		}

		// 先判断服务器文件是否存在
		FTPFile[] files = ftpClient.listFiles(serverPath);
		if (files.length == 0) {
			listener.onDownLoadProgress(FtpState.FTP_FILE_NOTEXISTS, 0, null);
			return;
		}

		//创建本地文件夹
		File mkFile = new File(localPath);
		if (!mkFile.exists()) {
			mkFile.mkdirs();
		}
		
		for (FTPFile ftpFile : files) {
			if(ftpFile != null) {
				if(ftpFile.isDirectory()) {
					downloadMutiFile(serverPath, serverPath+ftpFile.getName(), listener);
				} else {
					downloadSingleFile(serverPath, ftpFile, localPath, ftpFile.getName(), listener);
				}
			}
		}
		// 下载完成之后关闭连接
		this.closeConnect();
		listener.onDownLoadProgress(FtpState.FTP_DISCONNECT_SUCCESS, 0, null);

	} 
	
	/**
	 * 下载单个文件，可实现断点下载.
	 * 
	 * @param serverPath
	 * 			 FTP文件下载目录
	 * @param FTPFile
	 *            Ftp目录文件
	 * @param localPath
	 *            本地目录
	 * @param fileName       
	 *            下载之后的文件名称
	 * @param listener
	 *            监听器
	 * @throws IOException
	 */
	public void downloadSingleFile(String serverPath, FTPFile ftpFile, String localPath, String fileName, DownLoadProgressListener listener)
			throws Exception {
		localPath = localPath + fileName;
		//判断文件存在就删除
		long serverSize = ftpFile.getSize(); // 获取远程文件的长度
		File localFile = new File(localPath);
		long localSize = 0;
		if (localFile.exists()) {
			localFile.delete();
			localFile = new File(localPath);
			if (!localFile.exists()) {
				localFile.createNewFile();
			}
		}
		isBreak = false;
		// 进度
		long step = serverSize / 100;
		long process = 0;
		long currentSize = 0;
		// 开始准备下载文件
		OutputStream out = new FileOutputStream(localFile, true);
		ftpClient.setRestartOffset(localSize);
		InputStream input = ftpClient.retrieveFileStream(serverPath+fileName);
		byte[] b = new byte[sizeEvery];
		int length = 0;
		while ((length = input.read(b)) != -1 && !isBreak) {
			out.write(b, 0, length);
			currentSize = currentSize + length;
			if (currentSize / step != process) {
				process = currentSize / step;
				listener.onDownLoadProgress(FtpState.FTP_DOWN_LOADING, process, null);
			}
			listener.onDownLoadSumProgress(length,currentSize, serverSize);
		}
		out.flush();
		out.close();
		input.close();
		if (isBreak) {
			ftpClient.completePendingCommand();
			return;
		}
		// 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
		if (ftpClient.completePendingCommand()) {
			listener.onDownLoadProgress(FtpState.FTP_DOWN_SUCCESS, 0, new File(localPath));
		} else {
			listener.onDownLoadProgress(FtpState.FTP_DOWN_FAIL, 0, null);
		}
		return;
	}

	// -------------------------------------------------------文件删除方法------------------------------------------------

	/**
	 * 删除Ftp下的文件.
	 * 
	 * @param serverPath
	 *            Ftp目录及文件路径
	 * @param listener
	 *            监听器
	 * @throws IOException
	 */
	public void deleteSingleFile(String serverPathName, DeleteFileProgressListener listener)
			throws Exception {

		// 打开FTP服务
		try {
			this.openConnect();
			listener.onDeleteProgress(FtpState.FTP_CONNECT_SUCCESSS);
		} catch (IOException e1) {
			e1.printStackTrace();
			listener.onDeleteProgress(FtpState.FTP_CONNECT_FAIL);
			return;
		}

		// 先判断服务器文件是否存在
		FTPFile[] files = ftpClient.listFiles(serverPathName);
		if (files.length == 0) {
			listener.onDeleteProgress(FtpState.FTP_FILE_NOTEXISTS);
			return;
		}
		
		//进行删除操作
		boolean flag = true;
		flag = ftpClient.deleteFile(serverPathName);
		if (flag) {
			listener.onDeleteProgress(FtpState.FTP_DELETEFILE_SUCCESS);
		} else {
			listener.onDeleteProgress(FtpState.FTP_DELETEFILE_FAIL);
		}
		
		// 删除完成之后关闭连接
		this.closeConnect();
		listener.onDeleteProgress(FtpState.FTP_DISCONNECT_SUCCESS);
		
		return;
	}

	// -------------------------------------------------------打开关闭连接------------------------------------------------

	/**
	 * 打开FTP服务.
	 * 
	 * @throws IOException
	 */
	public void openConnect() throws IOException {
//		//设置默认超时
//		ftpClient.setDefaultTimeout(20*1000);
//		//设置连接超时
		ftpClient.setConnectTimeout(60*1000);
//		//设置数据传输超时
//		ftpClient.setDataTimeout(20*1000);
		// 中文转码
		ftpClient.setControlEncoding("UTF-8");
		int reply; // 服务器响应值
		// 连接至服务器
		if (hostName.isEmpty()) {
			if (FtpUtil.getInstances().getServerPort() == -1) {
				ftpClient.connect(FtpUtil.getInstances().getHostName());
			}else {
				ftpClient.connect(FtpUtil.getInstances().getHostName(), FtpUtil.getInstances().getServerPort());
			}
		}else {
			if (serverPort == -1) {
				ftpClient.connect(hostName);
			}else {
				ftpClient.connect(hostName, serverPort);
			}
		}
		
		// 获取响应值
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			// 断开连接
			ftpClient.disconnect();
			throw new IOException("connect fail: " + reply);
		}
		// 登录到服务器
		if (hostName.isEmpty()) {
			ftpClient.login(FtpUtil.getInstances().getUserName(), FtpUtil.getInstances().getPassword());
		}else {
			ftpClient.login(userName, password);
		}
		// 获取响应值
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			// 断开连接
			ftpClient.disconnect();
			throw new IOException("connect fail: " + reply);
		} else {
			// 获取登录信息
			FTPClientConfig config = new FTPClientConfig(ftpClient
					.getSystemType().split(" ")[0]);
			config.setServerLanguageCode("zh");
			ftpClient.configure(config);
			// 使用被动模式设为默认
			ftpClient.enterLocalPassiveMode();
			// 二进制文件支持
			ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
		}
	}

	/**
	 * 关闭FTP服务.
	 * 
	 * @throws IOException
	 */
	public void closeConnect(){
		isBreak = true;
		try {
			if (ftpClient != null && ftpClient.isConnected()) {
				try {
					// 断开连接
					ftpClient.disconnect();
				} catch (Exception e) {
					// TODO: handle exception
				}
				if (buffIn != null) {
					buffIn.close();
				}
				// 退出FTP
//				ftpClient.logout();
				if (progressInput != null) {
					progressInput.close();
				}
			}
		} catch (Exception e2) {
			// TODO: handle exception
		}
	}
	
	private boolean isBreak = false;

	// ---------------------------------------------------上传、下载、删除监听---------------------------------------------
	
	/*
	 * 上传进度监听
	 */
	public interface UploadProgressListener {
		/**
		 * 上传进度监听
		 * @param state 状态--- FTP_UPLOAD_SUCCESS(成功)   FTP_UPLOAD_FAIL(失败)   FTP_UPLOAD_LOADING(进行中)
 		 * @param uploadSize 当前已上传大小  
		 * @param file 当前上传文件
		 */
		public void onUploadProgress(int state, long uploadSize, File file);
		
		/**
		 * @param uploadSize 当前上传大小
		 * @param fileSize 总大小
		 */
		public void onUploadSumProgress(long uploadSize, long uploadProcess, long fileSize);
	}

	/*
	 * 下载进度监听
	 */
	public interface DownLoadProgressListener {
		/**
		 * 下载进度监听
		 * @param state 下载状态 FTP_DOWN_LOADING(进行中)   FTP_DOWN_SUCCESS(成功)  FTP_DOWN_FAIL(失败)
		 * @param downProcess 下载进度
		 * @param file 下载文件名
		 */
		public void onDownLoadProgress(int state, long downProcess, File file);
		
		/**
		 * @param downSize 当前下载的大小
		 * @param downProcess 当前下载总大小
		 * @param fileSize 总大小
		 */
		public void onDownLoadSumProgress(long downSize, long downProcess, long fileSize);
	}

	/*
	 * 文件删除监听
	 */
	public interface DeleteFileProgressListener {
		/**
		 * 删除监听
		 * @param state 状态     FTP_DELETEFILE_SUCCESS(成功)     FTP_DELETEFILE_FAIL(失败)
		 */
		public void onDeleteProgress(int state);
	}

}
