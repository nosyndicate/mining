package ddm.p2p.myfilesharing.download;

import javax.swing.JTable;

/**
 * 读取下载配置文件并渲染DownloadTable
 * @author 刘浩
 *
 */
public class FileDownloadInfoReader {	
	JTable myTable; 
	public FileDownloadInfoReader(JTable table){
		this.myTable=table;
	}
	public void readFilesFromConfig() {}
}
