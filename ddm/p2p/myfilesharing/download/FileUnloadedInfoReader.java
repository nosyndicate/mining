package ddm.p2p.myfilesharing.download;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import ddm.p2p.myfilesharing.model.File;
import ddm.p2p.myfilesharing.view.P2PForm;

/**
 * 读取未完成下载的文件信息
 * @author 刘浩
 *
 */
public class FileUnloadedInfoReader extends FileDownloadInfoReader{
	
	public FileUnloadedInfoReader(JTable table){
		super(table);
	}
	
	public void readFilesFromConfig(){
		int row=-1;
		List fileList=DownLoadXMLHelper.getInstance().getFilesInfo();
		System.out.println("UnloadedFiles Info");
		
		String[] titles = {"文件名", "文件大小(KB)", "已下载", "MD5", "状态","连接状态","资源数"	};		
        for(int i=0; i < fileList.size();i++){
        	File file=(File)fileList.get(i);
        	if(!file.getStatus().equals("finished")){
        		row++;
        		System.out.println(file.getFilename());
        	}
        }
		DefaultTableModel tableModel = new DefaultTableModel(titles, row+1);
		myTable.setModel(tableModel);
        
		row=-1;
        for(int i=0; i < fileList.size();i++){
        	File file=(File)fileList.get(i);
        	if(!file.getStatus().equals("finished")){
        		row++;
        		System.out.println(file.getFilename());
        		myTable.setValueAt(file.getFilename(),row,0);
        		myTable.setValueAt(file.getFilesize(),row,1);
        		myTable.setValueAt(file.getPercentageLoaded(),row,2);
        		myTable.setValueAt(file.getMD5(),row,3);
        		myTable.setValueAt(file.getStatus(),row,4);
        		myTable.setValueAt(file.getConnectstatus(),row,5);
        	}        	
        }
        refreshUnloadedFilesRow();
	} 
	
	
	public void refreshUnloadedFilesRow(){
		Set keys = P2PForm.downloadThreads.keySet();
		Iterator it = keys.iterator();//遍历取出
		while (it.hasNext()) {
			String key = (String) it.next();
			FileDownloader downloader = (FileDownloader)P2PForm.downloadThreads.get(key);
			downloader.setRow(DownLoadXMLHelper.getInstance().getUnloadedFileRowByMD5(key));
		}	
	}	
}
