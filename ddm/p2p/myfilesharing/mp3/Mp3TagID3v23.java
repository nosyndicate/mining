package ddm.p2p.myfilesharing.mp3;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.activation.UnsupportedDataTypeException;

public class Mp3TagID3v23 extends Mp3Tag {
	 
	 Map<String,String> frameMap = new HashMap<String,String>();
	 
	 String decoder="uft-8";
	 public Mp3TagID3v23(File f) {
		 super(f);
	 }
	 
	 private int getTagLength() throws UnsupportedDataTypeException, FileNotFoundException{
		 long fLenght = fSour.length();
		 int TagLength = 0;
		 bTagInfo = false;
	
		 byte[] iD2v3Header = new byte[10];//标签头
		 RandomAccessFile fOpera = new RandomAccessFile(fSour, "r");
		 try{
			 fOpera.read(iD2v3Header);
			 String id3v2HeaderSign = new String(iD2v3Header,0,3);
			 if(id3v2HeaderSign.equalsIgnoreCase("ID3")){
				 TagLength = ((iD2v3Header[6]& 0x7F)<<24)
				 |((iD2v3Header[7]& 0x7F)<<16)
				 |((iD2v3Header[8]& 0x7F)<<8)
				 |(iD2v3Header[9]& 0x7F);
				 if (TagLength + 10 >= fLenght){
					 TagLength = 0;
				 }else{
					 bTagInfo = true;
					 TagLength += 10;
				 }
			 }
			 fOpera.close();
		 }
		 catch(IOException e){
			 try {
				 fOpera.close();
			 } catch (IOException e1){}
			 throw new UnsupportedDataTypeException();
		 }
		 return TagLength;
	 }
	
	 public boolean hasTagID3v2()throws UnsupportedDataTypeException, FileNotFoundException{
		 boolean hasTagID3v2=false;
		 byte[] iD2v3Header = new byte[10];//标签头
		 RandomAccessFile fOpera = new RandomAccessFile(fSour, "r");
		 try{
			 fOpera.read(iD2v3Header);
			 String id3v2HeaderSign = new String(iD2v3Header,0,3);
			 if(id3v2HeaderSign.equalsIgnoreCase("ID3"))
				 hasTagID3v2=true;
		 } catch(IOException e){
					 try {
						fOpera.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		 }
	 	 return hasTagID3v2;
	 }
	 
	 
	 @Override
	 public void Decode(String Decoder) throws FileNotFoundException,
	   UnsupportedEncodingException, UnsupportedDataTypeException {
		 decoder=Decoder;
		 int iD2v3Length = getTagLength()-10;  //去除Header的Tag的大小
		 frameMap.clear();
		 if(iD2v3Length > 0){
			 byte[] iD2v3Header = new byte[10];
			 RandomAccessFile fOpera = new RandomAccessFile(fSour, "r");
			 try {
				 fOpera.read(iD2v3Header);
	   
				 byte[] iD3v2Infos = null;
	    
				 if((iD2v3Header[5] & 0x40)!=0){ //判断是否有扩展头帧
					 fOpera.skipBytes(10);
					 iD2v3Length -= 10;
				 }
				 iD3v2Infos = new byte[iD2v3Length];
	     
				 bTagInfo = true;
				 fOpera.read(iD3v2Infos);
				 readID3v2Tags(iD3v2Infos);
				 fOpera.close();
			 }catch(IOException e){
				 try {
					 fOpera.close();
				 } catch (IOException e1) {}
				 throw new UnsupportedDataTypeException();
			 }
		 }
	 }
	 
	 private void readID3v2Tags(byte[] TagInfos) throws UnsupportedDataTypeException, UnsupportedEncodingException{
		 int index = 10;
		 int cLenght = 0;
		 int cEncoder = 0;
		 while(index < TagInfos.length){
			 String sID = new String(TagInfos,index-10,4);
			 cLenght = TagInfos[index-6]<<24 | TagInfos[index-5]<<16 | TagInfos[index-4]<<8 | TagInfos[index-3];
			 if(sID.isEmpty() || cLenght <=0)
				 break;
			 if (cLenght + index > TagInfos.length)
				 throw new UnsupportedDataTypeException();
			 cEncoder = TagInfos[index];
			 String sContent = null;
			 try {
				 switch(cEncoder){
					 case 0:
						 sContent = new String(TagInfos,index+1,cLenght-1,"GBK");
						 System.out.println("GBK");
						 break;
					 case 1:
						 sContent = new String(TagInfos,index+1,cLenght-1,"UTF-16LE");
						 System.out.println("UTF-16LE");
						 break;
					 case 2:
						 sContent = new String(TagInfos,index+1,cLenght-1,"UTF-16BE");
						 System.out.println("UTF-16BE");
						 break;
					 default:
						 sContent = new String(TagInfos,index+1,cLenght-1,"UTF-8"); 
						 System.out.println("UTF-8");
				 }
			 } catch (UnsupportedEncodingException e) {
				 sContent = new String(TagInfos,index+1,cLenght-1);
			 }
			 frameMap.put(sID,sContent);
			 index = index + cLenght +10;
		 }
	 }
	
	 @Override
	 public void SetTitle(String Title) {
		 frameMap.put("TIT2", Title);
	 }
	
	 @Override
	 public String getTitle() {
		 if(frameMap.get("TIT2")==null){
			 return "";
		 }
		 else{
			 try {
				 return new String(frameMap.get("TIT2").getBytes(),"UTF-8");
			 } catch (UnsupportedEncodingException e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
				 return frameMap.get("TIT2");
			 }
		 }
	 }
	
	 @Override
	 public void setArtist(String Artist) {
		 frameMap.put("TPE1", Artist);
	 }
	
	 @Override
	 public String getArtist() {
		 if(frameMap.get("TPE1")==null){
			 return "";
		 }
		 else{
			 try {
				 return new String(frameMap.get("TPE1").getBytes(),"UTF-8");
			 } catch (UnsupportedEncodingException e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
				 return frameMap.get("TPE1");
			 }
		}
			
	 }
	
	 @Override
	 public void setAlbum(String Album) {
		 frameMap.put("TALB",Album);
	 }
	
	 @Override
	 public String getAlbum() {	 
		 if(frameMap.get("TALB")==null){
				return "";
		}else{
			 try {
				 return new String(frameMap.get("TALB").getBytes(),"UTF-8");
			 } catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return frameMap.get("TALB");
		 	}
		}
	 }
	
	 @Override
	 public void setYear(String Year) {
		 frameMap.put("TYER", Year);
	 }
	
	 @Override
	 public String getYear() {	 
		 if(frameMap.get("TYER")==null){
			 return "";
		 }else{
			 try {
				 return new String(frameMap.get("TYER").getBytes(),"UTF-8");
			 } catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return frameMap.get("TYER");
			 }
		 }
	 }
	
	 @Override
	 public void setComment(String Comment) {
		 frameMap.put("COMM", "CHA\0" + Comment);
	 }
	
	 @Override
	 public String getComment() {
		 String sComment = frameMap.get("COMM");
	  	 if(sComment!= null){
		    sComment = sComment.substring(4);
	  	 	try {
	  	 			return new String(sComment.getBytes(),"UTF-8");
	  	 	} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return sComment;
			}
	  	 }else{
	  		 return "";
	  	 }
	 }
	
	 @Override
	 public void setTrack(int Track) {
	  	 frameMap.put("TRCK", String.valueOf(Track));
	 }
	
	 @Override
	 public int getTrack() {
	  	 int iTrck =0;
	  	 try{
	  	     iTrck = Math.abs(Integer.parseInt(frameMap.get("TRCK")));
	  	 }
	  	 catch(NumberFormatException e){
	  		 frameMap.put("TRCK", "0");
	  	 }
	  	 return iTrck;
	 }
	
	 @Override
	 public void setGenre(String Genre) {
	  	 frameMap.put("TCON", Genre);
	 }
	
	 @Override
	 public String getGenre() {
		 return frameMap.get("TCON");
	 }
}