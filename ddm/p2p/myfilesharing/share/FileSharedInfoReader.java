package ddm.p2p.myfilesharing.share;

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;



public class FileSharedInfoReader {
	private JTable table=null;
	public FileSharedInfoReader(JTable table){
		this.table=table;
	}
	
	public void readSharedFilesFromConfig(){
		List<ddm.p2p.myfilesharing.model.File> sharedfiles = ShareXMLHelper
		.getInstance().getSharedFilesInfo();
		for (int i = 0; i < sharedfiles.size(); i++) {
			ddm.p2p.myfilesharing.model.File file = sharedfiles.get(i);
		}

		String[] titles = {"文件名",	"文件大小(Byte)", "MD5", "文件路径"};

		DefaultTableModel TableModel1 = new DefaultTableModel(titles,sharedfiles.size()) {
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false;
			}
		};
		table.setModel(TableModel1);
		for (int i = 0; i < sharedfiles.size(); i++){
			table.setValueAt(sharedfiles.get(i).getFilename(), i, 0);
			table.setValueAt(sharedfiles.get(i).getFilesize(), i, 1);
			table.setValueAt(sharedfiles.get(i).getMD5(), i, 2);
			table.setValueAt(sharedfiles.get(i).getFilePath(), i, 3);
		}
	} 
}
