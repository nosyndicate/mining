package ddm.p2p.myfilesharing.mp3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;




/**
 * @author 刘浩
 * 用来操作XML文件工具类
 */
public class Mp3XMLHelper {
	
	private static Mp3XMLHelper mp3xmlHelper;
	private static final String mp3Config="mp3.xml";
	private Document document;
	private Element root;
	
	public static Mp3XMLHelper getInstance(){
		if(mp3xmlHelper==null)
			mp3xmlHelper = new Mp3XMLHelper(); 
		return mp3xmlHelper;
	}
	
	/**
	 * 构造函数
	 */
	public Mp3XMLHelper(){	
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		File folder = new File(mp3Config);
		if(!folder.exists()) {
		   System.out.println(mp3Config+" no exist,create new a mp3 xml file");
		   try {
				builder = factory.newDocumentBuilder();
				document = builder.newDocument();
				root = document.createElement("mp3files");
				document.appendChild(root);
		   } catch (ParserConfigurationException e) {
				e.printStackTrace();
		   }
		   save();
		}
		else{  
			try {
				builder = factory.newDocumentBuilder();
				document = (Document)builder.parse(new File(mp3Config));
				root=document.getDocumentElement();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	
	
	/**
	 * 增加记录
	 * @param filename	文件名
	 * @param md5	    md5码
	 */
	public synchronized void addMP3Info(String filename,String md5,Mp3Tag mp3){
		if(!isExist(md5)){
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				document = (Document)builder.parse(new File(mp3Config));
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			root=document.getDocumentElement();
			Element fileNode = document.createElement("mp3file");
			Element filenameNode=document.createElement("filename");
			filenameNode.appendChild(document.createTextNode(filename));
			Element titleNode=document.createElement("title");
			titleNode.appendChild(document.createTextNode(mp3.getTitle()));
			Element artistNode=document.createElement("artist");
			artistNode.appendChild(document.createTextNode(mp3.getArtist()));
			Element albumNode=document.createElement("album");
			albumNode.appendChild(document.createTextNode(mp3.getAlbum()));
			Element yearNode=document.createElement("year");
			yearNode.appendChild(document.createTextNode(mp3.getYear()));
			Element genreNode=document.createElement("genre");
			genreNode.appendChild(document.createTextNode(mp3.getGenre()));
			Element md5Node=document.createElement("md5");
			md5Node.appendChild(document.createTextNode(md5));
			fileNode.appendChild(filenameNode);
			fileNode.appendChild(titleNode);
			fileNode.appendChild(artistNode);
			fileNode.appendChild(albumNode);
			fileNode.appendChild(yearNode);
			fileNode.appendChild(genreNode);
			fileNode.appendChild(md5Node);
			root.appendChild(fileNode);
			save();
		}
	}
	
	/**
	 * 保存修改结果到文件
	 */
	public synchronized  void save(){
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			PrintWriter pw = new PrintWriter(new FileOutputStream(mp3Config));
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
		} catch (TransformerException mye) {
			mye.printStackTrace();
		} catch (IOException exp) {
			exp.printStackTrace();
		}
	}
	
	public synchronized boolean isExist(String md5){
		boolean isExist=false;
		try {  
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
			factory.setValidating(false);  
			DocumentBuilder builder = factory.newDocumentBuilder();  
			document = builder.parse(new File(mp3Config));    
			Node node = document.getFirstChild(); 
			NodeList nodes = node.getChildNodes(); 
			System.out.println(node.getNodeName()+nodes.getLength());
			for (int i = 0; i < nodes.getLength(); i++) {
				Node nodeItem = nodes.item(i);
				if(nodeItem.getNodeType()==Node.ELEMENT_NODE){
					NodeList nodesOfChild=nodeItem.getChildNodes();
					for(int j=0; j <nodesOfChild.getLength();j++){
						Node nodeItemOfChild=nodesOfChild.item(j);
						if(nodeItemOfChild.getNodeName().equals("md5")){
							if(nodeItemOfChild.getTextContent().equals(md5)){
								isExist=true;
							}
						}
					}
				}
			}  
		} catch (Exception exp) {  
			exp.printStackTrace();   
		} 
		return isExist;
	}	
}
