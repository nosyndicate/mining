package ddm.p2p.myfilesharing.chat.threads;

import java.util.concurrent.locks.ReentrantLock;

import ddm.p2p.myfilesharing.chat.model.OfflineMessage;
import ddm.p2p.myfilesharing.chat.model.OnlineMessage;
import ddm.p2p.myfilesharing.view.ChatWindow;
import ddm.p2p.myfilesharing.view.P2PForm;

/**
 * @author 刘浩
 * 处理接到在线线消息的线程
 * 主要是找到与离线用户聊天的窗口，使该窗口恢复输入
 */
public class OnlineChatThread implements Runnable{

	private ReentrantLock lock = new ReentrantLock(); 
	private OnlineMessage msg;
	public OnlineChatThread(ReentrantLock lock,OnlineMessage message){
		this.lock=lock;
		this.msg=message;
	}
	public void run(){
		lock.lock();
		try {
			if(P2PForm.chatwins.containsKey(msg.getHostName())) {
				ChatWindow chatWindow = (ChatWindow)P2PForm.chatwins.get(msg.getHostName());
				if(chatWindow != null)
					chatWindow.enableInputAndSend();
			}
			
		} finally {
			lock.unlock();
		}
	}
}
