package ddm.p2p.myfilesharing.utils;
import java.util.List;

import ddm.p2p.myfilesharing.download.DownLoadXMLHelper;
import ddm.p2p.myfilesharing.model.File;
import ddm.p2p.myfilesharing.share.ShareXMLHelper;
public class ConfigCheck {
	/**
	 * 核对系统磁盘和下载配置文件的一致性
	 * 只需比对下载配置文件中的文件在当前磁盘状态下是否还存在
	 */
	static public void checkDowloadConfig(){
		List<File> downloadFiles=DownLoadXMLHelper.getInstance().getFilesInfo();
		for(int i=0;i<downloadFiles.size();i++){
			File file=downloadFiles.get(i);
			java.io.File iofile = new java.io.File(file.getFilePath());
			if(!iofile.exists()){//文件已经在系统磁盘上进行了删除，download.xml文件中对应删除
				DownLoadXMLHelper.getInstance().getInstance().removeFileInfo(file.getMD5());
			}
		}	
	}
	
	/**
	 * 核对系统磁盘和共享文件的一致性
	 * 1、核对当前共享配置文件中的文件在当前磁盘状态下是否还存在
	 * 2、核对共享文件夹中是否增加了新的共享文件
	 */
	static public void checkShareConfig(){
		ShareXMLHelper helper=ShareXMLHelper.getInstance().getInstance();
		List<File> sharedFiles=helper.getSharedFilesInfo();
		for(int i=0;i<sharedFiles.size();i++){
			File file=sharedFiles.get(i);
			java.io.File iofile = new java.io.File(file.getFilePath());
			if(!iofile.exists()){//文件已经在系统磁盘上进行了删除，download.xml文件中对应删除
				helper.removeSharedfilesInfo(file.getMD5());
				System.out.print("删除"+file.getFilePath());
			}
		}	
		java.io.File myPath = new java.io.File(FirsttimeChecker.SharedPath);
		java.io.File [] list = myPath.listFiles();
		for(int i=0;i<list.length;i++){
			if(!helper.isExistByFilepath(list[i].getAbsolutePath())){
				String md5=MD5Generator.getMD5(list[i]);
				helper.addSharedfilesInfo(list[i].getName(),list[i].getPath(), md5,FileHelper.getFileSizes(list[i])+"");
			}
		}		
	}
}
