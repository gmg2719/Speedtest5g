package cn.nokia.speedtest5g.speedtest.bean;

import java.io.Serializable;

/**
 * ftp配置bean
 * @author JQJ
 *
 */
@SuppressWarnings("serial")
public class BeanAppFtpConfig implements Serializable {

    public String iD;//服务器id
    public String operator;//运营商
    public String province;//省
    public String city;//地市
    public String ip;//IP
    public String port;//端口号
    public String upAccount;//上传用户名
    public String upPassword;//上传密码
    public String filename;//ftp文件路径
    public String uploadSize;//上传大小
    public String hostType;//服务器名称
    public String downAccount;//下载用户名
    public String downPassword;//下载密码
    public int performance; //服务器性能
    public String isdelete;
    public String ftpType;//类型 上传下载：ALL, 上传：UPLOAD, 下载：DOWNLOAD

    //自定义
    public String customDownIp;//下载IP
    public String customDownPort;//下载端口号
    public String customDownPath;//下载文件路径
    public String customDownThread;//下载线程
    public String customDownAccount;//下载用户名
    public String customDownPassword;//下载密码
    public String customUpIp;//上传IP
    public String customUpPort;//上传端口号
    public String customUpSize;//上传大小
    public String customUpThread;//下载线程
    public String customUpAccount;//上传用户名
    public String customUpPassword;//上传密码

    public boolean serverType;//服务器类型 true 正常服务器  false自定义apn

    //2测一测 1正在刷新  0常态  状态码
    public static final byte STATUS_0 = (byte)0;
    public static final byte STATUS_1 = (byte)1;
    public static final byte STATUS_2 = (byte)2;
    public byte status = STATUS_0;

}
