package ddm.p2p.myfilesharing.ddm;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import net.jxta.socket.JxtaSocket;
import ddm.p2p.myfilesharing.chat.model.OnlineMessage;
import ddm.p2p.myfilesharing.chat.service.JxtaServerSocketService;
import ddm.p2p.myfilesharing.chat.util.PipeUtil;
import ddm.p2p.myfilesharing.chat.util.XmlSender;
import ddm.p2p.myfilesharing.constants.Constants;
import ddm.p2p.myfilesharing.utils.AddressUtil;
import ddm.p2p.myfilesharing.view.P2PForm;

public class DDMService implements Runnable{

	private HashMap<String,Rule> assosiationRules=new HashMap<String,Rule>(); 
	
	private int level;
	private boolean shouldFininsh;
	private DDMConfidenceThread confidenceThread;
	private DDMSupportThread supportThread;
	
	private Vector<OnlineMessage> leafNeighbors;
	private OnlineMessage rootNeighbor;
	
	private static DDMService instance;//for singleton
	
	private DDMService()
	{
		this.level = -1;
		this.shouldFininsh = false;
		leafNeighbors = new Vector();
		rootNeighbor = null;
	}
	
	public static synchronized DDMService getInstance()
	{
	  if (instance == null)
		  instance = new DDMService();
	  return instance;
	}

	
	
	
	@Override
	public void run() {

		
		this.sendLevelQuery();
		initLocalAssosiationRule();
		updateLocalAssosiationRule(assosiationRules);
		// TODO Auto-generated method stub
		confidenceThread = new DDMConfidenceThread(this);
		supportThread = new DDMSupportThread(this);
		
		confidenceThread.run();
		supportThread.run();
		while(!shouldFininsh)
		{
			
		}
	}
	
	
	public int getLevel()
	{
		return this.level;
	}
	
	public synchronized void setLevel(int level)
	{
		this.level = level;
	}

	public synchronized void setRootNeighbor(String hostName)
	{
		Vector<OnlineMessage> peers = P2PForm.peers;
		for(int i = 0;i<peers.size();++i)
		{
			if(peers.elementAt(i).getHostName().equals(hostName))
			{
				this.rootNeighbor = peers.elementAt(i);
			}
		}
	}
	
	public synchronized void addLeafNeighbor(String hostName)
	{
		Vector<OnlineMessage> peers = P2PForm.peers;
		for(int i = 0;i<peers.size();++i)
		{
			if(peers.elementAt(i).getHostName().equals(hostName))
			{
				this.leafNeighbors.add(peers.elementAt(i));
			}
		}
	}
	
	
	
	private void sendLevelQuery()
	{
		
		//所有在线节点都在P2PForm的peers中
		//本用户向用户A发送DDMessage的过程类似于发送聊天消息的过程
		//首先建立Socket连接，点对点
		//下面时间里点对点通信的过程
		//处理接收的过程在类JxtaServerSocketService中内部类ChatMessageHandler中，
		//					
		// else if(type == Constants.MESSAGE_DDM){
	    //	
	    //}
		OnlineMessage msg=null;
		String hostname = null;
		JxtaSocket socket=null;
		
		msg = this.pickAOnlineMessage(P2PForm.peers);		
		
		if(msg==null)//no other node available, set the level of this node to 0
			return;
		
		try{
			socket = new JxtaSocket(P2PForm.pg,
			        null,
			        PipeUtil.findPipeAdv(P2PForm.pg, JxtaServerSocketService.JxtaServerSocketPipeAdvPrefix + msg.getHostName()),
			        60000,
			        true);
			OutputStream out = (OutputStream) socket.getOutputStream();
			DataOutput dos = new DataOutputStream(out);
			dos.writeInt(Constants.MESSAGE_LEVELQUERY);	
			dos.writeChars(AddressUtil.getHostName());
			out.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private OnlineMessage pickAOnlineMessage(Vector<OnlineMessage> peers)
	{
		Random random = new Random();
		int size = peers.size();
		if(size==0)
		{
			this.setLevel(0);
			return null;
		}
		
		int pos = (int) (size * random.nextDouble());
		return peers.elementAt(pos);
	}
	
	
	public void initLocalAssosiationRule(){
		
	}
	
	public void updateLocalAssosiationRule(HashMap<String,Rule> assosiationRules){
		
	}
}
