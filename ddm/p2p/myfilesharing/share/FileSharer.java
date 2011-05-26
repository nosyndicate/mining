package ddm.p2p.myfilesharing.share;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JTextArea;

import ddm.p2p.myfilesharing.utils.ConfigCheck;
import ddm.p2p.myfilesharing.utils.FileHelper;
import ddm.p2p.myfilesharing.utils.MD5Generator;

import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.share.CMS;
import net.jxta.share.Content;
import net.jxta.share.ContentManager;
import net.jxta.share.SearchListener;

/**
 * @author 刘浩
 * 
 * 用来设置共享文件夹
 * 
 */
public class FileSharer extends Thread implements SearchListener 
{
	
	//视图变量-----------------------------------------------
    private JTextArea log=null;
    
    //JXTA变量-----------------------------------------------
    private PeerGroup DDMGroup =null;
    
    //文件共享变量-------------------------------------------
    private File myPath = null;
    
    //CMS服务变量--------------------------------------------
    private CMS cms =null;
    
    private ContentManager contentManager = null;
    
    private FileSharedInfoReader sharedInfoReader=null;
    
    /**
     * 构造函数
     * @param group 	   要参与的共享组
     * @param log   	  日志
     * @param givenPath  共享的文件本地路径
     */
    public FileSharer(PeerGroup group, JTextArea log, File givenPath,FileSharedInfoReader sharedinfoReader){
        this.log = log;
        this.DDMGroup = group;
        this.myPath = givenPath;
        this.sharedInfoReader=sharedinfoReader;
        launchCMS();
    }
    /**
     * 建立CMS服务
     * 
     */
    private void launchCMS(){
        log.append("[+]校验共享配置文件...\n");
        ConfigCheck.checkShareConfig();
        sharedInfoReader.readSharedFilesFromConfig();
        log.append("[+]初始化 JXTA CMS 库...\n");
        
        cms = new CMS();//初始化CMS
        try {
            cms.init(DDMGroup,null,null);//绑定服务到DDM组
            if(cms.startApp(myPath) == -1){
                log.append("[-]建立CMS对象失败！！\n");
                System.exit(-1);
            }else{
                log.append("[+]CMS对象成功建立！！\n");
            }
            
            //添加listener
            cms.addSearchListener(this);
            //共享文件夹中的所有文件
            
            contentManager = cms.getContentManager();
            
            //共享--------------------------------------
            log.append("==================== 共享文件列表 ===================\n");
            List<ddm.p2p.myfilesharing.model.File> sharedfiles=ShareXMLHelper.getInstance().getSharedFilesInfo();
            for(int i=0;i<sharedfiles.size();i++){
            	ddm.p2p.myfilesharing.model.File file=sharedfiles.get(i);
                 contentManager.share(new File(file.getFilePath()),file.getMD5());    
                 log.append("[*]" + file.getFilename()+  "\n   MD5码: " +file.getMD5()+"\n");
            }
            log.append("==================== 共享文件列表 ===================\n");            
        } catch (PeerGroupException ex){
            ex.printStackTrace();
            System.exit(-1);
        }catch(IOException e){
            log.append("[-]系统异常： " + e.getMessage()+ "\n[!]请确认开启CMS服务前\"Shares.ser\"是否已经被删除\n");
            System.out.println("[-]系统异常： " + e.getMessage());            
        }       
    }
    /**
     * 停止CMS服务
     */
    public void stopCMS(){
        log.append("[+]停止CMS服务\n");
        cms.stopApp();
        log.append("[+]正在删除CMS广告内容...\n");
        File temp = new File(myPath.getAbsolutePath()+ File.separator +"shares.ser");        
        if(temp.delete())
        {   //also deletes the CMS data file
            log.append("[+]文件 \""+ myPath.getAbsolutePath()+ File.separator + "shares.ser\" 成功删除\n");
            System.out.println("[+]文件 shares.ser 被成功删除.");
        }else{
            log.append("[-]文件 shares.ser 找不到！\n");
        }
    }

    public void queryReceived(String query){
        System.out.println("[接收到搜索请求]： " + query);
    }
    
    public void addSharedFiles(File[] files){
    	for(int i=0;i<files.length;i++){
    		String md5=MD5Generator.getMD5(files[i]);
    		try {
				contentManager.share(files[i],md5);
				ShareXMLHelper.getInstance().addSharedfilesInfo(files[i].getName(),files[i].getAbsolutePath(), md5,FileHelper.getFileSizes(files[i])+"");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }   
    
    public void removeShareFiles(int index){
    	try {
			contentManager.unshare(contentManager.getContent()[index]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
