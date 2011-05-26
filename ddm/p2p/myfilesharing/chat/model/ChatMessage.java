package ddm.p2p.myfilesharing.chat.model;

/**
 * @author 刘浩
 * 聊天信息
 *
 */
public class ChatMessage {
	
	private String userName;//软件用户昵称
	private String hostName;//本机用户名
	private String message; //聊天信息
	private String datetime;//
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	
}
