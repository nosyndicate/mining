package ddm.p2p.myfilesharing.mp3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.activation.UnsupportedDataTypeException;

import ddm.p2p.myfilesharing.utils.FirsttimeChecker;

public class Mp3TagGetter {
	
	private static Mp3TagGetter mp3TagGetter=null;
	
	public static Mp3TagGetter getInstance(){
		if(mp3TagGetter==null)
			mp3TagGetter = new Mp3TagGetter(); 
		return mp3TagGetter;
	}
	
	public Mp3Tag getMp3TagInfo(String filename) throws UnsupportedDataTypeException, FileNotFoundException, UnsupportedEncodingException{	
		File file=new File(FirsttimeChecker.SharedPath+"\\"+filename);
		//File file=new File(filename);
		Mp3TagID3v1 tagID3v1=new Mp3TagID3v1(file);
		tagID3v1.Decode("gbk");
		return tagID3v1;	
	} 
}
