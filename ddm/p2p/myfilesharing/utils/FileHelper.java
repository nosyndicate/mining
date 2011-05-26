package ddm.p2p.myfilesharing.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import ddm.p2p.myfilesharing.mp3.Mp3XMLHelper;

public class FileHelper {
	
	private static HashMap<String,String> FILE_TYPE_MAP=new HashMap<String,String>();
	private static FileHelper fileHelper=null;
	public static FileHelper getInstance(){
		if(fileHelper==null){
			fileHelper = new FileHelper(); 
			fileHelper.initFileTypes();
		}
		return fileHelper;
	}
	
	private void initFileTypes(){ 
        FILE_TYPE_MAP.put("jpg", "图像"); //JPEG (jpg) 
        FILE_TYPE_MAP.put("png", "图像");//PNG (png) 
        FILE_TYPE_MAP.put("gif", "图像");//GIF (gif) 
        FILE_TYPE_MAP.put("tif", "图像");//TIFF (tif) 
        FILE_TYPE_MAP.put("bmp", "图像");//Windows Bitmap (bmp) 
        FILE_TYPE_MAP.put("dwg", "CAD文件");//CAD (dwg) 
        FILE_TYPE_MAP.put("html", "文本文件");//HTML (html) 
        FILE_TYPE_MAP.put("rtf", "文本文件");//Rich Text Format (rtf) 
        FILE_TYPE_MAP.put("xml", "文本文件"); 
        FILE_TYPE_MAP.put("zip", "压缩文件"); 
        FILE_TYPE_MAP.put("rar", "压缩文件"); 
        FILE_TYPE_MAP.put("psd", "PS文件");//Photoshop (psd) 
        FILE_TYPE_MAP.put("dbx", "CFAD12FEC5FD746F"); //Outlook Express (dbx) 
        FILE_TYPE_MAP.put("pst", "2142444E");//Outlook (pst) 
        FILE_TYPE_MAP.put("xls", "微软表格");//MS Word 
        FILE_TYPE_MAP.put("doc", "微软文本");//MS Excel 注意：word 和 excel的文件头一样 
        FILE_TYPE_MAP.put("mdb", "微软数据库文件"); //MS Access (mdb)   
        FILE_TYPE_MAP.put("pdf", "Adobe文件");//Adobe Acrobat (pdf) 
        FILE_TYPE_MAP.put("wav", "音频");//Wave (wav) 
        FILE_TYPE_MAP.put("avi", "视频"); 
        FILE_TYPE_MAP.put("ram", "视频");//Real Audio (ram) 
        FILE_TYPE_MAP.put("rm", "视频");//Real Media (rm) 
        FILE_TYPE_MAP.put("mpg", "视频");// 
        FILE_TYPE_MAP.put("mov", "QuickTime文件");//Quicktime (mov) 
        FILE_TYPE_MAP.put("asf", "视频");//Windows Media (asf) 
        FILE_TYPE_MAP.put("mid", "音频");//MIDI (mid) 
        FILE_TYPE_MAP.put("mp3", "音频");//Windows Password (pwl) 
        FILE_TYPE_MAP.put("wma", "音频");//Windows Password (pwl) 
        FILE_TYPE_MAP.put("mp4", "视频");//Windows Password (pwl)         FILE_TYPE_MAP.put("pwl", "E3828596");//Windows Password (pwl) 
    } 
	
    static public long getFileSizes(File f){//取得文件大小
        long s=0;
        try{
        	if (f.exists()) {
        		FileInputStream fis = new FileInputStream(f);
        		s= fis.available();
        		System.out.println("()()"+s);
        	} else {
        		System.out.println("文件不存在");
        	}
        }catch(Exception ex){
        	ex.printStackTrace();
        }
        return s;
    }
    
    static public String getFileType(String filename){
    	String extString=filename.substring(filename.lastIndexOf(".") + 1, filename.length());
    	if(FILE_TYPE_MAP.containsKey(extString.toLowerCase())){
    		return  extString.toLowerCase()+"("+FILE_TYPE_MAP.get(extString.toLowerCase())+")";
    	}
    	else{
    		return extString.toLowerCase()+"(不可知文件类型)";
    	} 	
    }
}
