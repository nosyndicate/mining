package ddm.p2p.myfilesharing.chat.model;

/**
 * @author 刘浩
 * 在线消息
 */
public class OnlineMessage {
	
	private String hostName;//本机用户名
	private String userName;//软件用户昵称
	private String computerName;//
	private String ip;
	
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
	public String getComputerName() {
		return computerName;
	}
	public void setComputerName(String computerName) {
		this.computerName = computerName;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
}
