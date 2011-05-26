package ddm.p2p.myfilesharing.chat.threads;

import javax.swing.JList;
import javax.swing.SwingWorker;

import ddm.p2p.myfilesharing.view.ChatWindow;
import ddm.p2p.myfilesharing.view.P2PForm;

import net.jxta.peergroup.PeerGroup;

public class CreateChatsessionThread extends SwingWorker<Void, Void> {
	
	public PeerGroup peerGroup=null;
	public JList peerList=null;
	
	/**
	 * 构造函数
	 * @param pg
	 * @param peerList
	 */
	public CreateChatsessionThread(PeerGroup pg,JList peerList){
		this.peerGroup=pg;
		this.peerList=peerList;
	}
	
    @Override
    public Void doInBackground() {
      try {
    	  System.out.println("执行建立聊天会话和窗口的线程");
    	  if (P2PForm.chatwins.containsKey(peerList.getSelectedValue())) {
				ChatWindow chatWindow = (ChatWindow) P2PForm.chatwins.get(peerList
						.getSelectedValue());
				chatWindow.setVisible(true);
		  } else {
				ChatWindow chatWindow = new ChatWindow(peerList.getSelectedValue()
						.toString(), peerGroup, P2PForm.peers.get(peerList
						.getSelectedIndex()));
				if(chatWindow.getJxtaSocket()!=null){
					P2PForm.chatwins.put(peerList.getSelectedValue().toString(),chatWindow);
					chatWindow.setVisible(true);
				}
		  }
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      return null;
    }

    @Override
    public void done() {

    }
  }