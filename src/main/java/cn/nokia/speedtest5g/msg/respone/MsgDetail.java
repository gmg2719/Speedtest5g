package cn.nokia.speedtest5g.msg.respone;

/**
 * 消息详情
 *
 * @author jinhaizheng
 */
public class MsgDetail {
	public long id;
	public String menuCode;// 应用(模块)来源
	public String title;// 标题
	public String content;// 内容
	public String createTime;// 创建时间
	public int type;// 跳转类型1内部；2外部
	public String subMenuCode;// 跳转类型

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		MsgDetail other = (MsgDetail) obj;
		if (id != other.id)
			return false;
		return true;
	}

}