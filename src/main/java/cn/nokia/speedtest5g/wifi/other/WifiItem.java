package cn.nokia.speedtest5g.wifi.other;

import java.io.Serializable;
/**
 * WIFI信息
 * @author zwq
 *
 */
@SuppressWarnings("serial")
public class WifiItem implements Serializable {

	public int frequency;
	
	public int level;
	
	public String SSID;
	
	public String BSSID;
	
	public String capabilities;

	public WifiItem(int level, int frequency, String SSID, String BSSID,String capabilities) {
		super();
		this.frequency = frequency;
		this.level = level;
		this.SSID = SSID;
		this.BSSID = BSSID;
		this.capabilities = capabilities;
	}
}
