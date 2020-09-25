package cn.nokia.speedtest5g.app;

import java.io.Serializable;

@SuppressWarnings("serial")
public class KeyValue implements Serializable{

	public Object key;
	
	public Object value;
	
	public KeyValue(Object key, Object value){
		this.key = key;
		this.value = value;
	}
}
