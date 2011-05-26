package ddm.p2p.myfilesharing.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import ddm.p2p.myfilesharing.download.DownLoadXMLHelper;

import net.jxta.peergroup.PeerGroup;
import net.jxta.share.ContentAdvertisement;
import net.jxta.share.client.CachedListContentRequest;

/**
 * @author 刘浩
 *
 */
public class FileSearcher extends Thread{
	
    //视图变量-----------------------------------
    private JTextArea log=null;
    private JTable table = null;
    
    //JXTA变量-----------------------------------
    private PeerGroup DDMGroup=null;

    //搜索字符串---------------------------------
	private String searchKey =null;
	
	//搜索类地Listener---------------------------
	protected MyListContentRequest reqestor =null;   
    
    private boolean running = true;
    
    /**
     * 构造函数
     * @param group			组
     * @param searchKey		搜索字符串
     * @param log			日志
     * @param table			显示table
     */
    public FileSearcher(PeerGroup group,String searchKey, JTextArea log, JTable table){
        this.DDMGroup = group;
        this.searchKey = "*"+searchKey+"*";
        this.log = log;
        this.table = table;      
    }
    public void run(){ 
        	reqestor = new MyListContentRequest(DDMGroup,searchKey,log, table);        
        	reqestor.activateRequest();
        	log.append("[-]搜索关键字"+this.searchKey+"线程结束\n");
    }        
    
    /**
     * 停止线程
     */
    public void stopThread(){
    	running = false;
    	if (reqestor != null){
    		reqestor.cancel();
    	}
    }
    
    /**
     * 停止搜索线程
     */
    public void killThread(){ //This method will Terminate the Search Thread
        log.append("[-]搜索关键字"+this.searchKey+"线程被停止\n");
        running =false;
    }
    
    
    /**
     * 得到搜索结果广告的Map
     * @return
     */
    public Map<String,ArrayList<ContentAdvertisement>> getAdvsMap(){
    	return reqestor.advsMap;
    }
}

/**
 * 搜索内部类，单独一个进程
 * 
 */
class MyListContentRequest extends CachedListContentRequest{
	
	//视图变量------------------------------------------------
    private JTextArea log = null;
    private JTable table =null;
    
    //JXTA变量------------------------------------------------
    public static ContentAdvertisement [] searchedAdvs = null;
    
    public Map<String,ArrayList<ContentAdvertisement>> advsMap=null;
    
    /**
     * 构造函数
     * @param DDMGroup	组
     * @param keyWord		搜索字符串
     * @param log			日志
     * @param table			显示table
     */
    public MyListContentRequest(PeerGroup DDMGroup , String keyWord, JTextArea log,JTable table){
        super(DDMGroup,keyWord);
        this.log = log;
        this.table = table;
        advsMap=new HashMap<String,ArrayList<ContentAdvertisement>>();
    }

    /**
     * 当新的内容（共享文件）被发现时被调用
     */
    public void notifyMoreResults()
    {
        log.append("[+]新的资源被发现！！\n");
        
        searchedAdvs = getResults();//得到搜索结果
        
        ContentAdvertisement [] advs=getResults();
        
        System.out.println("-----"+searchedAdvs.length);
        
    	for(int i=0;i<advs.length;i++){
    		if(advsMap.containsKey(advs[i].getDescription())){
    			if(!advsMap.get(advs[i].getDescription()).contains(advs[i])){
    				advsMap.get(advs[i].getDescription()).add(advs[i]);
    			}
    			System.out.println(advs[i].getName());
    			System.out.println(advs[i].getAddress());
    		}
    		else{
    			ArrayList<ContentAdvertisement> keyAdvs=new ArrayList<ContentAdvertisement>();
    			keyAdvs.add(advs[i]);
    			advsMap.put(advs[i].getDescription(), keyAdvs);
    			System.out.println(advs[i].getName());
    			System.out.println(advs[i].getAddress());
    			System.out.println(advs[i].toString());
    		}
    	}
    	 	
        
        //显示搜索结果到Table上------------------------------------------------------------------
        String [] titles = {"文件名","文件大小(Byte)","MD5","资源数","状态"};
        
        //显示在table上
        DefaultTableModel TableModel1 = new DefaultTableModel(titles, advsMap.size());
        table.setModel(TableModel1);
        
//        for(int i=0; i < searchedAdvs.length;i++){
//            log.append("[*]Found: " + searchedAdvs[i].getName()+"\n" +
//                    "Size: " + searchedAdvs[i].getLength() + " Bytes\n");
//            table.setValueAt(searchedAdvs[i].getName(),i,0);
//            table.setValueAt(searchedAdvs[i].getLength(),i,1);
//            table.setValueAt(searchedAdvs[i].getDescription(),i,2); 
//            table.setValueAt("no", i, 3);
//        }
        
        
        
		Set keys = advsMap.keySet();
		Iterator it = keys.iterator();//遍历取出
		int i=-1;
		while (it.hasNext()) {
			i++;
			String key = (String)it.next();
			List<ContentAdvertisement> advList = (List<ContentAdvertisement>)advsMap
					.get(key);
			ContentAdvertisement adv=(ContentAdvertisement)advList.get(0);
			log.append("[*]发现资源，文件名为: " + adv.getName()+"\n" +
                    "文件大小: " + adv.getLength() + " Bytes\n");
            table.setValueAt(adv.getName(),i,0);
            table.setValueAt(adv.getLength(),i,1);
            table.setValueAt(adv.getDescription(),i,2); 
            table.setValueAt(advList.size(), i, 3);
            if(DownLoadXMLHelper.getInstance().isExist(adv.getDescription())){
            	table.setValueAt("已下载", i, 4);
            }else{
            	table.setValueAt("未下载", i, 4);
            }
		}
    }
}
