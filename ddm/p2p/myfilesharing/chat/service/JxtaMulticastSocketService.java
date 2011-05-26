package ddm.p2p.myfilesharing.chat.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;

import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaMulticastSocket;

import org.apache.commons.lang.ClassUtils;
import ddm.p2p.myfilesharing.chat.model.OfflineMessage;
import ddm.p2p.myfilesharing.chat.model.OnlineMessage;
import ddm.p2p.myfilesharing.chat.threads.OfflineChatThread;
import ddm.p2p.myfilesharing.chat.threads.OnlineChatThread;
import ddm.p2p.myfilesharing.chat.util.PipeUtil;
import ddm.p2p.myfilesharing.chat.util.XmlReceiver;
import ddm.p2p.myfilesharing.chat.util.XmlSender;
import ddm.p2p.myfilesharing.utils.AddressUtil;
import ddm.p2p.myfilesharing.utils.ThreadPoolUtil;
import ddm.p2p.myfilesharing.view.MyCellRenderer;
import ddm.p2p.myfilesharing.view.MyDownloadInfoPop;
import ddm.p2p.myfilesharing.view.P2PForm;

/**
 * @author 刘浩
 *
 * 处理接收到的广播信息，负责广播服务
 * 
 */
public class JxtaMulticastSocketService implements Service {
	
	private static final Logger LOG = Logger.getLogger(JxtaMulticastSocketService.class.getName());

	//视图部分----------------------------------------------------
	private JList peerList=new JList();
	
	//Jxta变量部分------------------------------------------------
	private JxtaMulticastSocket mcastSocket;
	private final static String SOCKETIDSTR = "urn:jxta:uuid-59616261646162614E5047205032503386E7C7AE38954620A595F809548D680304";
	private static final int TIMEOUT = 0;
	private static final int BUFFERSIZE = 16384;
	
	//线程池部分--------------------------------------------------
	private final ExecutorService pool;
	
	/**
	 * 构造函数
	 * @param pg
	 * @param peerList
	 */
	public JxtaMulticastSocketService(PeerGroup pg, JList peerList) {
		try {
			System.out.println("构造JxtaMulticastSocket服务");
			this.mcastSocket = new JxtaMulticastSocket(pg, getSocketAdvertisement(pg));
			this.peerList = peerList;
		} catch (IOException e) {
			LOG.severe("JxtaMulticastSocket initialize failed!");
			e.printStackTrace();
			System.exit(-1);
		}
		
        if (this.mcastSocket != null) {
            try {
            	System.out.println("设置超时时间");
                this.mcastSocket.setSoTimeout(TIMEOUT);
            } catch (SocketException se) {
                se.printStackTrace(System.out);
            }
        }     
		pool = Executors.newCachedThreadPool();
	}
	
	/**
	 * 得到管道广告
	 * @param pg
	 * @return
	 */
	public static PipeAdvertisement getSocketAdvertisement(PeerGroup pg) {
		return PipeUtil.getPipeAdvWithoutRemoteDiscovery(pg,
				"JxtaMulticastSocket", PipeService.PropagateType, SOCKETIDSTR, true);
	}
	
	public void shutdownAndAwaitTermination() {
		ThreadPoolUtil.shutdownAndAwaitTermination(pool);
		if (this.mcastSocket != null) {
			this.mcastSocket.close();
		}
	}
	
	public void run() {
		try {
			System.out.println("开始监听");
			byte[] buffer = new byte[BUFFERSIZE];
			for (;;) {
				Arrays.fill(buffer,(byte)0);
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);			
				mcastSocket.receive(packet);
				System.out.println("收到消息");
				pool.execute(new MulticastMessageHandler(packet));//处理接收到的消息线程
			}
		} catch(IOException e) {
			pool.shutdown();
		} catch (Exception e) {

		}
	}

/**
 * @author 刘浩
 * 
 * 处理接收到的数据报文
 *
 */
class MulticastMessageHandler implements Runnable {
	
		private final DatagramPacket packet;
		private final ReentrantLock lock = new ReentrantLock();

		MulticastMessageHandler(DatagramPacket packet) {
			this.packet = packet;
		}

		public void run() {
			String sw = new String(packet.getData(), 0, packet.getLength());
			LOG.info("sw=" + sw);
			Object obj = XmlReceiver.getObject(sw);

            if(ClassUtils.isAssignable(obj.getClass(), OnlineMessage.class)) {
            	System.out.println("用户上线消息");
            	final OnlineMessage msg = (OnlineMessage)obj;
            	new MyDownloadInfoPop("用户提示","用户上线:" + msg.getHostName());
            	if(!isExist(P2PForm.peers, msg.getHostName())){
            		P2PForm.peers.add(msg);
            		refreshList();
                	 //告诉对方自己在线
                    if(!msg.getHostName().equals(AddressUtil.getHostName())) {
                    	try {
                    		String msgres = XmlSender.createOnlineMessage();
                    		DatagramPacket res = new DatagramPacket(msgres.getBytes(), msgres.length());
  							res.setAddress(res.getAddress());
  							mcastSocket.send(res);
                    	} catch (IOException e) {
                    		LOG.warning("Seng back OnlineMsg failed.");
                    		e.printStackTrace();
                    	}
                    }
                    OnlineChatThread onlineChatThread=new OnlineChatThread(lock,msg);//处理聊天对话框视图
                    onlineChatThread.run();
            	}
            } else if(ClassUtils.isAssignable(obj.getClass(), OfflineMessage.class)) {
            	System.out.println("用户下线消息");
            	final OfflineMessage msg = (OfflineMessage)obj;
            	if(!AddressUtil.getHostName().equals(msg.getHostName())){
            		new MyDownloadInfoPop("用户提示","用户离线:" + msg.getHostName());
            	}
            	removeMessage(msg.getHostName());//从list中删除该用户
            	OfflineChatThread offlineChatThread=new OfflineChatThread(lock,msg);//处理聊天对话框视图
            	offlineChatThread.run();
            } else {
            	//其他信息
            }
		}
	}
	
	/**
	 * 判断某用户是否存在
	 */
	public boolean isExist(Vector<OnlineMessage> messages,String hostname){
		boolean isExist=false;
		for(int i=0;i<messages.size();i++){
			if(messages.get(i).toString().equals(hostname)){
				isExist=true;
			}
		}
		return isExist;
	}
	
	/**
	 * 删除某个用户
	 * @param hostname
	 */
	public void removeMessage(String hostname){
    	int i=-1;
    	boolean isRemove=false;
    	for(i=0;i<P2PForm.peers.size();i++){
    		if(P2PForm.peers.get(i).toString().equals(hostname)){
    			isRemove=true;
    			break;
    		}
    	}
    	if(isRemove){
    		P2PForm.peers.remove(i);
    		refreshList();
    	}
	}
	
	/**
	 * 刷新List控件
	 */
	public void refreshList(){
		ArrayList<ImageIcon> icons=new ArrayList();
    	peerList.setListData(P2PForm.peers);
		DefaultListModel listModel = new DefaultListModel();
		for (int i = 0; i < P2PForm.peers.size(); i++) {
			listModel.add(i, P2PForm.peers.get(i));
			ImageIcon icon = new ImageIcon(
			".\\icons\\peer.png");
    		icons.add(icon);
		}
		peerList.setModel(listModel);
		peerList.setCellRenderer(new MyCellRenderer((ImageIcon[])icons.toArray(new ImageIcon[1])));
	}
}
