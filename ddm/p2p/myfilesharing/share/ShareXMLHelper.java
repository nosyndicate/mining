package ddm.p2p.myfilesharing.share;

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

import ddm.p2p.myfilesharing.mp3.Mp3Tag;


/**
 * @author 刘浩
 * 用来操作XML文件工具类
 */
public class ShareXMLHelper {
	
	private static ShareXMLHelper sharexmlHelper;
	private static final String shareConfig="share.xml";
	private Document document;
	private Element root;
	
	public static ShareXMLHelper getInstance(){
		if(sharexmlHelper==null)
			sharexmlHelper = new ShareXMLHelper(); 
		return sharexmlHelper;
	}
	
	/**
	 * 构造函数
	 */
	public ShareXMLHelper(){	
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		File folder = new File(shareConfig);
		if(!folder.exists()) {
		   System.out.println(shareConfig+" no exist,create new a share xml file");
		   try {
				builder = factory.newDocumentBuilder();
				document = builder.newDocument();
				root = document.createElement("sharefiles");
				document.appendChild(root);
		   } catch (ParserConfigurationException e) {
				e.printStackTrace();
		   }
		   save();
		}
		else{  
			try {
				builder = factory.newDocumentBuilder();
				document = (Document)builder.parse(new File(shareConfig));
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
	 * 保存修改结果到文件
	 */
	public synchronized  void save(){
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			PrintWriter pw = new PrintWriter(new FileOutputStream(shareConfig));
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
		} catch (TransformerException mye) {
			mye.printStackTrace();
		} catch (IOException exp) {
			exp.printStackTrace();
		}
	}
	
	/**
	 * 根据md5码判断文件的有无
	 * @param md5
	 * @return
	 */
	public synchronized boolean isExistByMD5(String md5){
		boolean isExist=false;
		try {  
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
			factory.setValidating(false);  
			DocumentBuilder builder = factory.newDocumentBuilder();  
			document = builder.parse(new File(shareConfig));    
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
	
	
	/**
	 * 根据md5码判断文件的有无
	 * @param md5
	 * @return
	 */
	public synchronized boolean isExistByFilepath(String filepath){
		boolean isExist=false;
		try {  
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
			factory.setValidating(false);  
			DocumentBuilder builder = factory.newDocumentBuilder();  
			document = builder.parse(new File(shareConfig));    
			Node node = document.getFirstChild(); 
			NodeList nodes = node.getChildNodes(); 
			System.out.println(node.getNodeName()+nodes.getLength());
			for (int i = 0; i < nodes.getLength(); i++) {
				Node nodeItem = nodes.item(i);
				if(nodeItem.getNodeType()==Node.ELEMENT_NODE){
					NodeList nodesOfChild=nodeItem.getChildNodes();
					for(int j=0; j <nodesOfChild.getLength();j++){
						Node nodeItemOfChild=nodesOfChild.item(j);
						if(nodeItemOfChild.getNodeName().equals("filepath")){
							if(nodeItemOfChild.getTextContent().equals(filepath)){
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
	
	/**
	 * 增加记录
	 * @param filename	文件名
	 * @param md5	    md5码
	 */
	public synchronized void addSharedfilesInfo(String filename,String filepath,String md5,String size){
		if((!isExistByMD5(md5))&&(!filename.equals("shares.ser"))&&(!filename.equals("shares.ser.tmp"))){
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				document = (Document)builder.parse(new File(shareConfig));
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
			Element fileNode = document.createElement("sharefile");
			
			Element filenameNode=document.createElement("filename");
			filenameNode.appendChild(document.createTextNode(filename));
			Element filepathNode=document.createElement("filepath");
			filepathNode.appendChild(document.createTextNode(filepath));
			Element md5Node=document.createElement("md5");
			md5Node.appendChild(document.createTextNode(md5));
			Element sizeNode=document.createElement("size");
			sizeNode.appendChild(document.createTextNode(size));

			fileNode.appendChild(filenameNode);
			fileNode.appendChild(filepathNode);
			fileNode.appendChild(md5Node);
			fileNode.appendChild(sizeNode);
			root.appendChild(fileNode);
			
			save();
		}
	}
	
	/**
	 * 根据md5码删除文件节点
	 * @param md5
	 */
	public synchronized void removeSharedfilesInfo(String md5){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			document = (Document)builder.parse(new File(shareConfig));
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
		Node removedNode=null;
		root=document.getDocumentElement();
		NodeList rootnodes=root.getChildNodes();
		for (int i = 0; i < rootnodes.getLength(); i++) {  
			Node rootItem = rootnodes.item(i);
			NodeList nodesOfChildren=rootItem.getChildNodes();
			for (int j = 0; j < nodesOfChildren.getLength(); j++) {
				Node childItem = nodesOfChildren.item(j);
				if(childItem.getNodeName().equals("md5")){
					if(childItem.getTextContent().equals(md5)){
						removedNode=childItem.getParentNode();	
					}
				}
			}
		}
		if(removedNode!=null){
			root.removeChild(removedNode);
			save();
		}	
	}
	
	
	/**
	 * 得到文件信息
	 * @return
	 */
	public synchronized  List getSharedFilesInfo(){
		ArrayList list=new ArrayList();
		try {  
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
			factory.setValidating(false);  
			DocumentBuilder builder = factory.newDocumentBuilder();  
			document = builder.parse(new File(shareConfig));    
			Node node = document.getFirstChild(); 
			NodeList nodes = node.getChildNodes(); 
			for (int i = 0; i < nodes.getLength(); i++) {
				Node nodeItem = nodes.item(i);
				if(nodeItem.getNodeType()==Node.ELEMENT_NODE){
					ddm.p2p.myfilesharing.model.File file=new ddm.p2p.myfilesharing.model.File();
					NodeList nodesOfChild=nodeItem.getChildNodes();
					for(int j=0; j <nodesOfChild.getLength();j++){
						Node nodeItemOfChild=nodesOfChild.item(j);
						if(nodeItemOfChild.getNodeName().equals("filename")){
							file.setFilename(nodeItemOfChild.getTextContent());
						}
						if(nodeItemOfChild.getNodeName().equals("md5")){
							file.setMD5(nodeItemOfChild.getTextContent());
						}
						if(nodeItemOfChild.getNodeName().equals("filepath")){
							file.setFilePath(nodeItemOfChild.getTextContent());
						}
						if(nodeItemOfChild.getNodeName().equals("size")){
							file.setFilesize(nodeItemOfChild.getTextContent());
						}
					}
					list.add(file);
				}
			}  
		} catch (Exception exp) {  
			exp.printStackTrace();   
		} 
		return list;		
	}
}
