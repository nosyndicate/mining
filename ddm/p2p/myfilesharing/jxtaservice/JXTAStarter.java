package ddm.p2p.myfilesharing.jxtaservice;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import javax.swing.JTextArea;

import net.jxta.credential.AuthenticationCredential;
import net.jxta.credential.Credential;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredTextDocument;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.membership.Authenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;

/**
 * @author 刘浩
 * 该类用来开启JXTA的相关平台和服务，包括建立对等组，获得相关服务等等，建立我们数据挖掘的JXTA网络，网络名是JxtaDDM
 */
public class JXTAStarter 
{   
	//视图变量------------------------------------------------------------------------------
    public JTextArea log;
    
    //Jxta应用变量--------------------------------------------------------------------------
    private final static int TIMEOUT = 5*1000;              //延迟时间
    private PeerGroup netPeerGroup = null,         			//初始对等组
                      DDMGroup   =null;            			//建立的DDM对等组（即DDM网络）
    DiscoveryService myDiscoveryService =null,     			//JXTA发现服务
                     DDMGroupDiscoveryService =null;		//DDM组发现服务
    PeerGroupAdvertisement DDMAdv =null;					//DDM组广告
    //DDM组别ID（唯一性）
    private final String stringID = "jxta:uuid-4E0742B0E54F4D0ABAC6809BB82A311E02";
    
    //Accessor
    public PeerGroup DDMGroup(){
    	return DDMGroup;   
    }
    
    public PeerGroupAdvertisement getDDMAdv(){
        return DDMAdv;
    }
    
    
    public DiscoveryService getMyDiscoveryService() {
		return myDiscoveryService;
	}

	/**
     * JXTAStarter构造函数
     * @param txt  log显示area
     */
    public JXTAStarter(JTextArea txt) 
    {
        this.log = txt;
        launchJXTA();
        getServices();
        searchForGroup();
    }
    
    
    /**
     *  建立JXTA环境
     */
    @SuppressWarnings("deprecation")
	private void launchJXTA()
    {
        log.append("[+]开启并进入JXTA网络......\n");
        try{
            netPeerGroup = PeerGroupFactory.newNetPeerGroup();            
        }catch(PeerGroupException e){
            System.out.println("[-]系统错误：" + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        
    }
    
    
    /**
     * 得到发现服务
     */
    private void getServices()
    {
        //Obtaining JXTA Services from JXTA Global group
        log.append("[+]获得JXTA发现服务\n");
        myDiscoveryService = netPeerGroup.getDiscoveryService();
    }
    
    
    /**
     * 寻找DDM组，若找不到，则建立DDM组，并加入 
     */
    private void searchForGroup() 
    {                             
    	Enumeration adv=null;//对等组广告       
        int count =0;
        log.append("[+]开始寻找DMM网络（进行5次尝试）\n");
        while(count < 5){
            try {
                log.append("[+]第" + count +"次尝试\n");
                //搜索组广告，分别从本地（local）和远程（remote）搜索
                adv = myDiscoveryService.getLocalAdvertisements(
                						 DiscoveryService.GROUP,
                						 "Name",
                						 "DDMGroup");//本地搜索
                if((adv != null) && adv.hasMoreElements()){
                    log.append("[+]在本地发现DDM网络广告\n");
                    DDMAdv = (PeerGroupAdvertisement)adv.nextElement();
                    DDMGroup = netPeerGroup.newGroup(DDMAdv);//得到DDM组（DDM网络）
                    joinToGroup(DDMGroup);//加入DDM组
                    break;
                }else{
                    log.append("[-]无法在本地找到DDM网络广告\n[+]开始远程搜索\n");
                    myDiscoveryService.getRemoteAdvertisements(
                    					null,
                    				    DiscoveryService.GROUP,
                    				    "Name",
                    				    "DDMGroup",
                    				    1);//远程搜索
                }
                Thread.sleep(TIMEOUT);
                
                //经过4次地搜索，在本地还是没有找到DDM组，则新建立DDM组（DDM网络）
                if((count == 4) && (adv == null || !adv.hasMoreElements())){
                log.append("[-]五次尝试搜索完毕，没有发现DDM网络，开始自行建立DDM网络\n");
                DDMGroup = createGroup();//新建立DDM组
                joinToGroup(DDMGroup);//加入DDM组
                break;
                }
                
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (PeerGroupException e){
            	System.out.println("[-]出错信息：" + e.getMessage());
            	e.printStackTrace();
            	System.exit(-1);
            } catch (InterruptedException e){
                System.out.println("[-]出错信息：" + e.getMessage());
                e.printStackTrace(); 
            }
            count++;
        }
    }
    
    /**
     * 建立DDM组
     * @return 新建立的组
     */
    private PeerGroup createGroup()
    {
        log.append("[+]建立DDM网络中...\n");
        PeerGroup myNewGroup = null;
        try{
            ModuleImplAdvertisement myMIA = netPeerGroup.getAllPurposePeerGroupImplAdvertisement();//要创建一个PeerGroup首先要创建一个通告
            myNewGroup = netPeerGroup.newGroup(getGID(),
                                               myMIA,
                                               "DDMGroup",
                                               "DDM P2P File Sharing");
            DDMAdv = myNewGroup.getPeerGroupAdvertisement();
            myDiscoveryService.publish(DDMAdv);
            myDiscoveryService.remotePublish(DDMAdv);
            log.append("[+]DDM网络成功建立！！\n");
            log.append("[+]发布DDM组别广告\n");
            log.append("[+]输出DDM网络信息：\n");
            log.append("[===========================]\n");
            log.append("[+]Group Name: " + DDMAdv.getName()+"\n");
            log.append("[+]Group ID:" + DDMAdv.getPeerGroupID().toString()+"\n");
            log.append("[+]Group Description: " + DDMAdv.getDescription()+"\n");
            log.append("[+]Group Module ID: " + DDMAdv.getModuleSpecID().toString()+"\n");
            log.append("[+]Advertisement Type: " + DDMAdv.getAdvertisementType()+"\n");
            log.append("[===========================]\n");
        }catch(Exception e){
            System.out.println("[*]错误信息：" + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        return myNewGroup;
    }

    /**
     * 得到对等组ID
     * @return PeerGroupID
     * @throws Exception
     */
    private PeerGroupID getGID() throws Exception{
        return (PeerGroupID) IDFactory.fromURL(new URL("urn","",stringID));
    }
    
    /**
     * 加入建立的或者找到的组
     * @param group
     */
    private void joinToGroup(PeerGroup group)
    {
        StructuredDocument creds = null;
        log.append("[===========================]\n");
        log.append("[+]加入DDM网络中....\n");
        
        try{
            //Athenticate and join to group
        AuthenticationCredential authCred = new AuthenticationCredential(group,null,creds);
        MembershipService membership = group.getMembershipService();
        Authenticator auth = membership.apply(authCred);
            if(auth.isReadyForJoin()){
                Credential myCred = membership.join(auth);
                System.out.println("[===== Group Details =====]");
                StructuredTextDocument doc = (StructuredTextDocument)myCred.getDocument(new MimeMediaType("text/plain"));
                StringWriter out = new StringWriter();
                doc.sendToWriter(out);
                
                System.out.println(out.toString());
                log.append("[+]Peer Name : " + group.getPeerName() + " 上线了！！！"+
                		"\n   Description:"+group.getPeerAdvertisement().getDescription()+
                		"\n   AdvType:"+group.getPeerAdvertisement().getAdvType()+
                		"\n   AdvertisementType:"+group.getPeerAdvertisement().getAdvertisementType()+
                		"\n   ID:"+group.getPeerAdvertisement().getID()+
                		"\n   PeerID:"+group.getPeerAdvertisement().getPeerID()+
                		"\n   PeerGroupID:"+group.getPeerAdvertisement().getPeerGroupID()+"\n");
                group.getPeerAdvertisement().getDescription();
                log.append("[+]获得DDM网络相关服务\n");
                //Publishing Peer Advertisements.
                DDMGroupDiscoveryService = group.getDiscoveryService();
                log.append("[+]发布Peer广告\n");
                DDMGroupDiscoveryService.publish(group.getPeerAdvertisement());
                DDMGroupDiscoveryService.remotePublish(group.getPeerAdvertisement());
                
                log.append("[===========================]\n");
            }
            else{
                System.out.println("[!!]系统错误：无法加入DDM网络，请重试或检查网络情况！");
                System.exit(-1);
            }            
        }catch(Exception e){
                System.out.println("[!]错误信息： " + e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            }
    }  
}
