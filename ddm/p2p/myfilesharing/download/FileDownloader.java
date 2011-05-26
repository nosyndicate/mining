package ddm.p2p.myfilesharing.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.UnsupportedDataTypeException;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextArea;

import ddm.p2p.myfilesharing.mp3.Mp3TagGetter;
import ddm.p2p.myfilesharing.mp3.Mp3XMLHelper;

import net.jxta.peergroup.PeerGroup;
import net.jxta.share.ContentAdvertisement;
import net.jxta.share.client.GetContentRequest;

/**
 * @author 刘浩
 * 
 * 本类开始一个线程进行下载
 * 
 */
public class FileDownloader extends Thread
{
    protected MyGetContentRequest downLoader = null;
    //视图变量------------------
    private JTextArea log;
    /**
     * 构造函数
     * @param group				组
     * @param contentAdv		广告
     * @param destination		目的文件
     * @param log				日志
     * @param progress			进度
     */
    public FileDownloader(PeerGroup group,ContentAdvertisement[] contentAdvs, File destination , JTextArea log,
    			int row,JTable myDownloadTable,FileDownloadInfoReader unloadedFilesReader,FileDownloadInfoReader loadedFilesReader){
        this.log = log;
        this.log.append("[+]启动一个下载进程...\n");
        this.downLoader = new MyGetContentRequest(group, contentAdvs, destination, 
        this.log,row,myDownloadTable,unloadedFilesReader,loadedFilesReader);  
    }
    
    public boolean isDone(){
    	return this.downLoader.isDone();
    }
    
    public void cancel(){
    	this.downLoader.cancel();
    }
    
    public void stopLoad(){
    	this.downLoader.stop();
    }
    
    public void setRow(int row){
    	this.downLoader.setRow(row);
    }
}

/**
 * @author 刘浩
 * 处理下载请求的内部类
 */
class MyGetContentRequest extends GetContentRequest{
	
	//视图变量---------------------------------
    private JTable myDownloadTable=null;
    private JTextArea log =null;
    private FileDownloadInfoReader unloadedFilesReader=null;
    private FileDownloadInfoReader loadedFilesReader=null;
    
    //下载变量---------------------------------
    private int row;
    private DownLoadXMLHelper xmlHelper;
    private Mp3XMLHelper mp3xmlHelper;

    
    //JXTA变量---------------------------------
    ContentAdvertisement[] contentAdvs;
    
    /**
     * 构造函数
     * @param group				组
     * @param contentAdv		广告
     * @param destination		目的文件
     * @param log				日志
     * @param progress			进度
     */
    public MyGetContentRequest(PeerGroup group, ContentAdvertisement[] contentAdvs, File destination , JTextArea log,
    				int row,JTable myDownloadTable,FileDownloadInfoReader unloadedFilesReader,FileDownloadInfoReader loadedFilesReader){
        super(group, contentAdvs, destination);
        this.myDownloadTable=myDownloadTable;
        this.contentAdvs=contentAdvs;
        this.row=row;
        this.log = log;
        this.log.append("[+]"+contentAdvs[0].getName()+"开始进行下载\n");
        this.xmlHelper=DownLoadXMLHelper.getInstance();
        this.mp3xmlHelper=Mp3XMLHelper.getInstance();
        this.loadedFilesReader=loadedFilesReader;
        this.unloadedFilesReader=unloadedFilesReader;
    }
    
    /**
     * 下载更新
     * @param percentage 百分比
     */
    public void notifyUpdate(int percentage){ //this method will notify about download progress
     //this.row=xmlHelper.getUnloadedFileRowByMD5(contentAdvs[0].getDescription());
     this.myDownloadTable.setValueAt(percentage+"%",row,2);
     this.myDownloadTable.setValueAt("loading",row,4);
     this.myDownloadTable.setValueAt("connected",row,5);
     this.myDownloadTable.setValueAt(contentAdvs.length+"", row, 6);
    }
    
    /**
     * 下载完成
     */
    public void notifyDone(){//this method will return message about download process 
          log.append("[+]Donwloading Process is sucessfully finished.\n");
          this.myDownloadTable.setValueAt("0", row, 6);
          this.xmlHelper.updateFileInfo(contentAdvs[0].getDescription(), "percentage", "100%");
          this.xmlHelper.updateFileInfo(contentAdvs[0].getDescription(), "status", "finished");
          this.xmlHelper.updateFileInfo(contentAdvs[0].getDescription(), "connectstatus", "noconnected");
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          this.xmlHelper.updateFileInfo(contentAdvs[0].getDescription(), "finishDate",dateFormat.format(new Date()));
          this.unloadedFilesReader.readFilesFromConfig();       
          this.loadedFilesReader.readFilesFromConfig();
          
          try {
			mp3xmlHelper.addMP3Info(contentAdvs[0].getName(), contentAdvs[0].getDescription(), Mp3TagGetter.getInstance().getMp3TagInfo(contentAdvs[0].getName()));
		} catch (UnsupportedDataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * 下载失败
     */
    public void notifyFailure(){
    	this.myDownloadTable.setValueAt("paused",row,4);
    	this.myDownloadTable.setValueAt("disconnected", row, 5);    
    	this.myDownloadTable.setValueAt("0", row, 6);
    	this.xmlHelper.updateFileInfo(contentAdvs[0].getDescription(), "percentage",this.getPercentDone()+"%");
    	this.xmlHelper.updateFileInfo(contentAdvs[0].getDescription(), "connectstatus", "disconnected");
        log.append("[-]"+contentAdvs[0].getName()+"下载失败！！\n");
    }
    
    public void cancel(){
    	super.cancel();
    	this.myDownloadTable.setValueAt("paused",row,4);
    	this.myDownloadTable.setValueAt("disconnected", row, 5);   
    	this.myDownloadTable.setValueAt("0", row, 6);
    	this.xmlHelper.updateFileInfo(contentAdvs[0].getDescription(), "percentage",this.getPercentDone()+"%");
    	this.xmlHelper.updateFileInfo(contentAdvs[0].getDescription(), "connectstatus", "disconnected");
    	log.append("[-]"+contentAdvs[0].getName()+"暂停下载！！\n");
    }
    
    public void stop(){
    	super.cancel();
    	this.xmlHelper.updateFileInfo(contentAdvs[0].getDescription(), "percentage",this.getPercentDone()+"%");
    	this.xmlHelper.updateFileInfo(contentAdvs[0].getDescription(), "connectstatus", "disconnected");
    }
    
    public void setRow(int row){
    	this.row=row;
    }
}
