package ddm.p2p.myfilesharing.download;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import ddm.p2p.myfilesharing.model.File;

/**
 * 读取已下载的文件信息
 * @author 刘浩
 *
 */
public class FileLoadedInfoReader extends FileDownloadInfoReader{
	
	public FileLoadedInfoReader(JTable table){
		super(table);
	}
	
	public void readFilesFromConfig(){
		int row=-1;	
		List fileList=DownLoadXMLHelper.getInstance().getFilesInfo();
    	System.out.println("LoadedFiles Info");
    	
		String[] titles = {"文件名", "文件大小(KB)", "已下载", "MD5", "状态", "完成时间","文件路径"};
        for(int i=0; i < fileList.size();i++){
        	File file=(File)fileList.get(i);
        	if(file.getStatus().equals("finished")){
        		row++;
        		System.out.println(file.getFilename());
        	}
        }
		DefaultTableModel tableModel = new DefaultTableModel(titles, row+1){
			private static final long serialVersionUID = 1L;
			boolean[] canEdit = new boolean[] { false, false, false, false,
					false, false, false };

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
	    };
		myTable.setModel(tableModel);	

		row=-1;
        for(int i=0; i < fileList.size();i++){
        	File file=(File)fileList.get(i);
        	if(file.getStatus().equals("finished")){
        		row++;
        		System.out.println(file.getFilename());
        		myTable.setValueAt(file.getFilename(),row,0);
        		myTable.setValueAt(file.getFilesize(),row,1);
        		myTable.setValueAt(file.getPercentageLoaded(),row,2);
        		myTable.setValueAt(file.getMD5(),row,3);
        		myTable.setValueAt(file.getStatus(),row,4);
        		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");      		
        		myTable.setValueAt(dateFormat.format(file.getFinishDate()), row, 5);
        		myTable.setValueAt(file.getFilePath(), row, 6);
        	}
        }
	} 
}
