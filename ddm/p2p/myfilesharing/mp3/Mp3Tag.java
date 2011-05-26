package ddm.p2p.myfilesharing.mp3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.activation.UnsupportedDataTypeException;

abstract public class Mp3Tag{
	 final static int MaxBuffSize = 2048;
	 File fSour = null;
	 boolean bTagInfo = false;
	 
	 public Mp3Tag(File f){
		 fSour = f;
	 }
	 
	 abstract public void Decode(String Decoder) throws FileNotFoundException, 
	 UnsupportedEncodingException, UnsupportedDataTypeException;
	 
	 abstract public void SetTitle(String Title);
	 abstract public String getTitle();
	 
	 abstract public void setArtist(String Artist);
	 abstract public String getArtist();
	 
	 abstract public void setAlbum(String Album);
	 abstract public String getAlbum();
	 
	 abstract public void setYear(String Year);
	 abstract public String getYear();
	 
	 abstract public void setComment(String Comment);
	 abstract public String getComment();
	 
	 abstract public void setTrack(int Track);
	 abstract public int getTrack();
	 
	 abstract public void setGenre(String Genre);
	 abstract public String getGenre();
}