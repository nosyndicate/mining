package ddm.p2p.myfilesharing.chat.threads;

import java.util.concurrent.locks.ReentrantLock;

import ddm.p2p.myfilesharing.chat.model.OfflineMessage;
import ddm.p2p.myfilesharing.view.ChatWindow;
import ddm.p2p.myfilesharing.view.P2PForm;


/**
 * @author 刘浩
 * 处理接到离线消息的线程
 * 主要是找到与离线用户聊天的窗口，使该窗口不能再输入
 */
public class OfflineChatThread implements Runnable{

	private ReentrantLock lock = new ReentrantLock(); 
	private OfflineMessage msg;
	public OfflineChatThread(ReentrantLock lock,OfflineMessage message){
		this.lock=lock;
		this.msg=message;
	}
	public void run(){
		lock.lock();
		try {
			System.out.println(P2PForm.chatwins.containsKey(msg.getHostName()));
			System.out.println(P2PForm.chatwins.size());
			if(P2PForm.chatwins.containsKey(msg.getHostName())){
				ChatWindow chatWindow = (ChatWindow)P2PForm.chatwins.get(msg.getHostName());
				if(chatWindow != null)
					chatWindow.disableInputAndSend(msg.getHostName());
			}
		} finally {
			lock.unlock();
		}
	}
}
