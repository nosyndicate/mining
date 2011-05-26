package ddm.p2p.myfilesharing.search;

import javax.swing.JTable;

import ddm.p2p.myfilesharing.download.DownLoadXMLHelper;

import net.jxta.peergroup.PeerGroup;
import net.jxta.share.ContentAdvertisement;
import net.jxta.share.client.CachedListContentRequest;

/**
 * @author 刘浩
 * 续接资源类
 */
public class FileContSearcher extends Thread{
	
    //视图变量-----------------------------------
    private JTable table = null;
    
    //JXTA变量-----------------------------------
    private PeerGroup DDMGroup=null;

    //搜索字符串---------------------------------
	private String filename =null;
	private String md5=null;
    private int row=-1;

	//搜索类地Listener---------------------------
	protected MyContListContentRequest reqestor =null;  
    private ContentAdvertisement[] searchedAdvs=null;
  
    private boolean running = true;
    
    /**
     * 构造函数
     * @param group			组
     * @param searchKey		搜索字符串
     * @param log			日志
     * @param table			显示table
     */
    public FileContSearcher(PeerGroup group,String filename,String md5,JTable table,
    		int row){
        this.DDMGroup = group;
        this.filename="*"+filename+"*";
        this.md5=md5;
        this.table = table;
        this.row=row;  
    }
    
    
    public void setSearchedAdvs(ContentAdvertisement[] searchedAdvs) {
		this.searchedAdvs = searchedAdvs;
	}


	public void run(){ 
           	this.reqestor = new MyContListContentRequest(DDMGroup,filename,md5,table,row,this);
        	reqestor.activateRequest();     
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
        running =false;   
    }
    
    /**
     * 得到搜索到的广告
     * @return
     */
    public ContentAdvertisement[] getContentAdvs(){ //Accessor to show found contents
    	return this.searchedAdvs;
    } 
}

/**
 * 搜索内部类，单独一个进程
 * 
 */
class MyContListContentRequest extends CachedListContentRequest{
	
	//视图变量------------------------------------------------
    private JTable table =null;
    private String filename=null;
    private String md5=null;
    private int row=-1;
    private FileContSearcher contsearching=null;
    
    
    private DownLoadXMLHelper xmlHelper=new DownLoadXMLHelper();
    
    //JXTA变量------------------------------------------------
    public  ContentAdvertisement [] searchedAdvs = null;
    
    /**
     * 构造函数
     * @param DDMGroup	组
     * @param keyWord		搜索字符串
     * @param log			日志
     * @param table			显示table
     */
    public MyContListContentRequest(PeerGroup DDMGroup,String filename,String md5,JTable table,
    		int row,FileContSearcher contSearching){
        super(DDMGroup,filename);
        this.table = table;
        this.filename=filename;
        this.md5=md5;
        this.row=row;
        this.contsearching=contSearching;  
    }

    /**
     * 当新的内容（共享文件）被发现时被调用
     */
    public void notifyMoreResults(){      
        searchedAdvs = getResults();//得到搜索结果 
        this.contsearching.setSearchedAdvs(searchedAdvs);
        table.setValueAt(searchedAdvs.length+"",row,6); 
    }
}
