package ddm.p2p.myfilesharing.mp3;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

import javax.activation.UnsupportedDataTypeException;

public class Mp3TagID3v1 extends Mp3Tag {
 String sTitle = "";
 String sArtist = "";
 String sAlbum = "";
 String sYear = "";
 String sComment = "";
 int iTrack = 0;
 int iGenre = 0;
 
 public Mp3TagID3v1(File f) {
  super(f);
  // TODO Auto-generated constructor stub
 }

 private String ReadFixSizeString(byte[] in,int off,int size,String Decoder) throws UnsupportedEncodingException{
  int length = size+off;
  int i = off;
  for(;i<length;i++){
   if(in[i] == 0)
    break;
  }
  return new String(in,off,i-off,Decoder);
 }
 
 @Override
 public void Decode(String Decoder) throws FileNotFoundException,
   UnsupportedEncodingException, UnsupportedDataTypeException {
  // TODO Auto-generated method stub
  bTagInfo = false;
  sTitle = "";
  sArtist = "";
  sAlbum = "";
  sYear = "";
  sComment = "";
  iTrack = 0;
  iGenre = 0;
  
  byte[] iD3v1 = new byte[128];
  RandomAccessFile fOpera = new RandomAccessFile(fSour, "r");
  try {
   fOpera.seek(fSour.length()-128);
   fOpera.read(iD3v1);
   fOpera.close();
  } catch (IOException e) {
   // TODO Auto-generated catch block
   try {
    fOpera.close();
   } catch (IOException e1) {}
   throw new UnsupportedDataTypeException();
  }

  String id3v1Sign = new String(iD3v1,0,3);
  if(id3v1Sign.equalsIgnoreCase("TAG")){
   sTitle =  ReadFixSizeString(iD3v1, 3, 30, Decoder).trim();
   sArtist = ReadFixSizeString(iD3v1, 33,30, Decoder).trim();
   sAlbum = ReadFixSizeString(iD3v1, 63,30, Decoder).trim();
   sYear = ReadFixSizeString(iD3v1, 93, 4, Decoder).trim();
   if(iD3v1[125] == 0){
    sComment = ReadFixSizeString(iD3v1, 97,28, Decoder).trim();
    iTrack = iD3v1[126];
   }
   else{
    sComment = ReadFixSizeString(iD3v1, 97,30, Decoder).trim();
    iTrack = 0;
   }
   iGenre = iD3v1[127]&0xFF;
   bTagInfo = true;
  }
 }

  
 public void SetTitle(String Title){
  if(Title == null)
   sTitle = "";
  else
   sTitle = Title;
 }
 public String getTitle(){
  return sTitle;
 }
 
 public void setArtist(String Artist){
  if(Artist == null)
   sArtist ="";
  else
   sArtist = Artist;
 }
 public String getArtist(){
  return sArtist;
 }
 
 public void setAlbum(String Album){
  if(Album == null)
   sAlbum = "";
  else
   sAlbum = Album;
 }
 public String getAlbum(){
  return sAlbum;
 }
 
 public void setYear(String Year){
  if(Year == null)
   sYear = "";
  else
   sYear = Year; 
 }
 public String getYear(){
  return sYear;
 }
 
 public void setComment(String Comment){
  if(Comment == null)
   sComment ="";
  else
   sComment = Comment;
 }
 public String getComment(){
  return sComment;
 }
 
 public void setTrack(int Track){
  iTrack = Math.abs(Track);
 }
 public int getTrack(){
  return iTrack;
 }
 
 public void setGenre(String Genre){
  iGenre = 0;
 }
 public String getGenre(){
  return String.valueOf(iGenre);
 }
}