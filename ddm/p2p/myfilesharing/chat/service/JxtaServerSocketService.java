package ddm.p2p.myfilesharing.chat.service;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.swing.JList;
import javax.swing.JTextArea;

import ddm.p2p.myfilesharing.chat.model.ChatMessage;
import ddm.p2p.myfilesharing.chat.model.OnlineMessage;
import ddm.p2p.myfilesharing.chat.threads.ReceiveChatMessageThread;
import ddm.p2p.myfilesharing.chat.util.PipeUtil;
import ddm.p2p.myfilesharing.chat.util.XmlReceiver;
import ddm.p2p.myfilesharing.constants.Constants;
import ddm.p2p.myfilesharing.ddm.DDMService;
import ddm.p2p.myfilesharing.utils.AddressUtil;
import ddm.p2p.myfilesharing.utils.ThreadPoolUtil;
import ddm.p2p.myfilesharing.view.ChatWindow;
import ddm.p2p.myfilesharing.view.P2PForm;

//import jxtamessenger.ChatWindow;
//import jxtamessenger.MainApplicationWindow;

import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeService;
import net.jxta.socket.JxtaServerSocket;

public class JxtaServerSocketService implements Service {
	
	
	private static final Logger LOG = Logger.getLogger(JxtaServerSocketService.class.getName());

	private JList peerList;
	private PeerGroup pg;
	
	public static final String JxtaServerSocketPipeAdvPrefix = "JxtaServerSocketPipeAdv.";
	
    private JxtaServerSocket serverSocket = null;
	private final ExecutorService pool;
	
	
	public JxtaServerSocketService(PeerGroup pg, JList peerList) {
        try {
        	this.peerList = peerList;
        	this.pg = pg;
            this.serverSocket = new JxtaServerSocket(pg, 
            		PipeUtil.getPipeAdv(pg, 
            				JxtaServerSocketPipeAdvPrefix + AddressUtil.getHostName(), 
            				PipeService.UnicastType, 
            				true));
        } catch (IOException ioe) {
            ioe.printStackTrace(System.out);
        }
        
        if (this.serverSocket != null) {
            try {
                this.serverSocket.setSoTimeout(0);
            } catch (SocketException se) {
                se.printStackTrace(System.out);
            }
        }
        
		pool = Executors.newCachedThreadPool();
	}
	
	public void shutdownAndAwaitTermination() {
		ThreadPoolUtil.shutdownAndAwaitTermination(pool);

		if (this.serverSocket != null) {
            try {
                this.serverSocket.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
		}
	}

	public void run() {
		try {
			for (;;) {
				pool.execute(new ChatMessageHandler(serverSocket.accept()));
			}
		} catch(IOException e) {
			pool.shutdown();
		} catch (Exception e) {
			
		}
	}
	
	class ChatMessageHandler implements Runnable {
	    private final Socket socket;
	    ChatMessageHandler(Socket socket){
	    	this.socket = socket; 
	    }
	    public void run() {
			try {

				while(true) {
					InputStream in = socket.getInputStream();
					DataInput dis = new DataInputStream(in);
					int type = dis.readInt();
					if(type == Constants.MESSAGE_EXITWINDOW) {
						socket.shutdownInput();
						socket.shutdownOutput();
						socket.close();
						break;
					} else if(type == Constants.MESSAGE_CHAT) {
						System.out.println("这一个聊天消息");
						int length = dis.readInt();
						StringBuffer sb = new StringBuffer();
						for(int i=0; i<length; i++)
							sb.append(dis.readUTF());
						final ChatMessage msg = (ChatMessage)XmlReceiver.getObject(sb.toString());
						ReceiveChatMessageThread receiveChatMessageThread=new ReceiveChatMessageThread(peerList,msg,pg);
						receiveChatMessageThread.run();
					} else if(type == Constants.MESSAGE_FILE) {
						
					} else if(type == Constants.MESSAGE_DIRECTORY) {
						
					} else if(type == Constants.MESSAGE_DDM){
						
					}
					else if(type == Constants.MESSAGE_LEVELQUERY){
						String hostName = dis.readLine();
						DDMService ddmservice = DDMService.getInstance();
						ddmservice.addLeafNeighbor(hostName);
						OutputStream out = (OutputStream) socket.getOutputStream();
						DataOutput dos = new DataOutputStream(out);
						dos.writeInt(Constants.MESSAGE_LEVELRESPONSE);
						dos.writeInt(DDMService.getInstance().getLevel());
						dos.writeChars(AddressUtil.getHostName());
						
					}else if(type == Constants.MESSAGE_LEVELRESPONSE){
						int level = dis.readInt();
						String hostName = dis.readLine();
						System.out.println("weiermo, the line is "+hostName);
						DDMService ddmservice = DDMService.getInstance();
						ddmservice.setLevel(level + 1);//set the level
						ddmservice.setRootNeighbor(hostName);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
}