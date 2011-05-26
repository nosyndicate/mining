package ddm.p2p.myfilesharing.chat.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.logging.Logger;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeID;
import net.jxta.protocol.PipeAdvertisement;

import org.apache.commons.lang.StringUtils;

/**
 * @author 刘浩
 * 管道工具类
 */
public class PipeUtil {
	
	private static final Logger LOG = Logger.getLogger(PipeUtil.class.getName());
	
	private static final int WaitTime = 500;
	private static final int MAXRETRIES = 20;
	
	/**
	 * 得到管道广告
	 * @param pg
	 * @param name
	 * @param type
	 * @param pipeId
	 * @param remote
	 * @return
	 */
	public static PipeAdvertisement getPipeAdv(PeerGroup pg, String name, String type, String pipeId, boolean remote) {
		PipeAdvertisement myAdv = null;
		try {
			myAdv = findPipeAdv(pg, name);
			
			if(myAdv == null) {
				//找不到这个管道广告，建立该广告
				LOG.info("Could not find the Pipe Advertisement");
				
				//建立通道广告
				myAdv = createAdv(pg, name, type, pipeId);
				
				//把该广告发布到本地并发布到远程用户
				publish(pg, myAdv, remote);
				
				LOG.info("Created the Pipe Advertisement");
			}
		} catch(Exception e) {
			LOG.severe("Could not get pipe Advertisement");
			return null;
		}
		
		return myAdv;
	}
	
	/**
	 * 查找管道广告
	 * @param pg
	 * @param name
	 * @return
	 */
	public static PipeAdvertisement findPipeAdv(PeerGroup pg, String name) {
		
		DiscoveryService discovery = pg.getDiscoveryService();//发现服务
		
		int count = MAXRETRIES;//发现服务最多尝试的发现次数
		
		PipeAdvertisement myAdv = null;
		
		try {
			LOG.info("寻找管道广告中...");
			
			while(count-- > 0) {
				//先查看本地是否已经在缓存里存储了该通道的广告
				myAdv = searchLocal(pg, name);
				
				//如果在本地发现了管道广告，结束搜索
				if(myAdv != null)
					break;

				//没有在本地发现广告，尝试远程搜索
				discovery.getRemoteAdvertisements(null, DiscoveryService.ADV, PipeAdvertisement.NameTag, name, 1, null);
				
				//休眠以接收peers的反馈
				try {
					Thread.sleep(WaitTime);
				} catch(InterruptedException e) {
					// ignored
				}
			}
		} catch(Exception e) {
			LOG.severe("无法得到通道广告");
			return null;
		}
		
		if(myAdv != null) {
			LOG.info(myAdv.toString());
		} else {
			LOG.info("myAdv is null.");
		}	
		return myAdv;
	}
	
	public static PipeAdvertisement getPipeAdv(PeerGroup pg, String name, String type, boolean remote) {
		return getPipeAdv(pg, name, type, null, remote);
	}
	
	/**
	 * 得到本地广告
	 * @param pg
	 * @param name
	 * @param type
	 * @param pipeId
	 * @param remote
	 * @return
	 */
	public static PipeAdvertisement getPipeAdvWithoutRemoteDiscovery(PeerGroup pg, String name, String type, String pipeId, boolean remote) {
        PipeAdvertisement pa = searchLocal(pg, name);

        if (pa == null) {
            pa = createAdv(pg, name, type, pipeId);

            publish(pg, pa, remote);
        }

        return pa;
	}
	
	/**
	 * 得到本地广告
	 * @param pg
	 * @param name
	 * @param type
	 * @param remote
	 * @return
	 */
	public static PipeAdvertisement getPipeAdvWithoutRemoteDiscovery(PeerGroup pg, String name, String type, boolean remote) {
		return getPipeAdvWithoutRemoteDiscovery(pg, name, type, null, remote);
	}
	
    /**
     * 建立管道广告
     * @param pg
     * @param name
     * @param type
     * @param pipeId
     * @return
     */
    public static PipeAdvertisement createAdv(PeerGroup pg, String name, String type, String pipeId) {
    	PipeAdvertisement pa = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());

    	try {
			pa.setPipeID(StringUtils.isEmpty(pipeId)?IDFactory.newPipeID(pg.getPeerGroupID()):(PipeID) IDFactory.fromURI(new URI(pipeId)));
	    	pa.setName(name);
	    	pa.setType(type);
		} catch (URISyntaxException e) {
			LOG.warning("a string could not be parsed as a URI reference");
			e.printStackTrace();
		}

    	return pa;
    }

    
    /**
     * 本地搜索管道广告
     * @param pg
     * @param name
     * @return
     */
    public static PipeAdvertisement searchLocal(PeerGroup pg, String name) {

        DiscoveryService discoveryService = pg.getDiscoveryService();
        Enumeration<Advertisement> pas = null;
        try {
            pas = discoveryService.getLocalAdvertisements(DiscoveryService.ADV, PipeAdvertisement.NameTag, name);
        } catch (IOException e) {
            return null;
        }
        PipeAdvertisement pa = null;
        while (pas.hasMoreElements()) {
            pa = (PipeAdvertisement) pas.nextElement();

            if (pa.getName().equals(name)) {
                return pa;
            }
        }
        return null;
    }
	
    /**
     * 发布本地广告
     * @param pg
     * @param pa
     */
    public static void publish(PeerGroup pg, PipeAdvertisement pa) {
        publish(pg, pa, false);
    }

    /**
     * 发布远程广告
     * @param pg
     * @param pa
     * @param remote
     */
    public static void publish(PeerGroup pg, PipeAdvertisement pa, boolean remote) {
        DiscoveryService ds = pg.getDiscoveryService();
        try {
            ds.publish(pa);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        if (remote) {
             ds.remotePublish(pa, DiscoveryService.ADV);
        }
    }
}
