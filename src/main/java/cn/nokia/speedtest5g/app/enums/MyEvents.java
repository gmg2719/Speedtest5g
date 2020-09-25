package cn.nokia.speedtest5g.app.enums;


/**
 * mode 模式 1MyDialog
 * type 类型
 * object 对象
 * isOK 属性
 * @author Administrator
 *
 */
public class MyEvents {
	//模式
	private ModeEnum mode;
	//类型
	private int type;
	//属性
	public boolean isOK;
	//对象1
	private Object object;
	//对象2
	private Object objectTwo;
	//对象3
	private Object objectThree;
	
	public MyEvents(){}
	
	/**
	 * mode 模式 
	*/
	public MyEvents(ModeEnum mode){
		this.mode = mode;
	}
	
	/**
	 * mode 模式 
	*/
	public MyEvents(Object object){
		this.object = object;
	}
	
	/**
	 * mode 模式
	 * type 类型
	*/
	public MyEvents(ModeEnum mode,int type){
		this.mode = mode;
		this.type = type;
	}
	
	/**
	 * mode 模式 
	 * type 类型
	 * object 对象
	*/
	public MyEvents(ModeEnum mode,int type,Object object){
		this.mode = mode;
		this.type = type;
		this.object = object;
	}
	
	public MyEvents(int type,Object object){
		this.type = type;
		this.object = object;
	}
	
	/**
	 * mode 模式
	 * type 类型
	 * object 对象
	 * isOK 属性
	*/
	public MyEvents(ModeEnum mode,int type,Object object,boolean isOK){
		this.mode = mode;
		this.type = type;
		this.object = object;
		this.isOK = isOK;
	}

	/**
	 * @param mode 模式
	 * @param type 类型
	 * @param object 对象1
	 * @param objectTwo 对象2
	 * @param isOK 属性
	 */
	public MyEvents(ModeEnum mode,int type,Object object,Object objectTwo,boolean isOK){
		this.mode = mode;
		this.type = type;
		this.object = object;
		this.isOK = isOK;
		this.objectTwo = objectTwo;
	}
	
	/**
	 * @param mode 模式
	 * @param type 类型
	 * @param object 对象1
	 * @param objectTwo 对象2
	 * @param objectThree 对象3
	 * @param isOK 属性
	 */
	public MyEvents(ModeEnum mode,int type,Object object,Object objectTwo,Object objectThree,boolean isOK){
		this.mode = mode;
		this.type = type;
		this.object = object;
		this.isOK = isOK;
		this.objectTwo = objectTwo;
		this.objectThree = objectThree;
	}
	
	public Object getObjectTwo() {
		return objectTwo;
	}

	public void setObjectTwo(Object objectTwo) {
		this.objectTwo = objectTwo;
	}

	public ModeEnum getMode() {
		return mode;
	}

	public void setMode(ModeEnum mode) {
		this.mode = mode;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Object getObjectThree() {
		return objectThree;
	}

	public void setObjectThree(Object objectThree) {
		this.objectThree = objectThree;
	}
	
}
