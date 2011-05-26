package ddm.p2p.myfilesharing.chat.model;

/**
 * @author 刘浩
 * 离线消息
 */
public class OfflineMessage {
	
	private String hostName;//本机用户名
	private String userName;//软件用户昵称
	
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String toString(){
		return hostName;
	}
	
	
}
