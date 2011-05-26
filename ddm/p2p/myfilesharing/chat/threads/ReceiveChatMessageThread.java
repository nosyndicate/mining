package ddm.p2p.myfilesharing.chat.threads;

import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JList;
import javax.swing.JTextArea;

import ddm.p2p.myfilesharing.chat.model.ChatMessage;
import ddm.p2p.myfilesharing.chat.model.OfflineMessage;
import ddm.p2p.myfilesharing.chat.model.OnlineMessage;
import ddm.p2p.myfilesharing.view.ChatWindow;
import ddm.p2p.myfilesharing.view.P2PForm;

import net.jxta.peergroup.PeerGroup;

/**
 * @author 刘浩
 * 处理接到聊天消息的线程
 * 主要是显示接受到的消息
 */
public class ReceiveChatMessageThread implements Runnable{

	private JList peerList=null;
	private ChatMessage msg=null;
	private PeerGroup pg=null;
	
	public ReceiveChatMessageThread(JList peerList,ChatMessage msg,PeerGroup pg){
		this.peerList=peerList;
		this.msg=msg;
		this.pg=pg;
	}
	
	public void run(){
		synchronized(peerList) {
			ChatWindow chatWindow = null;
    		if(!P2PForm.chatwins.containsKey(msg.getHostName())) {
    			OnlineMessage onlineMessage = new OnlineMessage();
    			onlineMessage.setHostName(msg.getHostName());
    			onlineMessage.setUserName(msg.getUserName());
				chatWindow = new ChatWindow(msg.getHostName(),pg, onlineMessage);
				P2PForm.chatwins.put(msg.getHostName(), chatWindow);
				chatWindow.setVisible(true);
    		} else {
    			chatWindow = (ChatWindow)P2PForm.chatwins.get(msg.getHostName());
   				chatWindow.setVisible(true);
   			}
   			JTextArea chatlogTextArea = chatWindow.getchatTextArea();
   			chatlogTextArea.append(msg.getUserName());
   			chatlogTextArea.append(" "+msg.getDatetime());
			chatlogTextArea.append(":\r\n");
			chatlogTextArea.append(msg.getMessage());
			chatlogTextArea.append("\r\n");
		}
	}
}
