package ddm.p2p.myfilesharing.download;

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

import ddm.p2p.myfilesharing.utils.FirsttimeChecker;

/**
 * @author 刘浩
 * 用来操作XML文件工具类
 */
public class DownLoadXMLHelper {
	
	private static DownLoadXMLHelper xmlHelper;
	private static final String downLoadConfig="download.xml";
	private static Document document;
	private static Element root;
	
	public static DownLoadXMLHelper getInstance(){
		if(xmlHelper==null)
			xmlHelper = new DownLoadXMLHelper(); 
		return xmlHelper;
	}
	
	/**
	 * 构造函数
	 */
	public DownLoadXMLHelper(){
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		File folder = new File(downLoadConfig);
		if(!folder.exists()) {
		   System.out.println(downLoadConfig+"no exist,create new a download file");
		   try {
				builder = factory.newDocumentBuilder();
				document = builder.newDocument();
				root = document.createElement("downLoadfiles");
				document.appendChild(root);
		   } catch (ParserConfigurationException e) {
				e.printStackTrace();
		   }
		   save();
		}
		else{  
			try {
				builder = factory.newDocumentBuilder();
				document = (Document)builder.parse(new File(downLoadConfig));
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
	 * @param md5		md5码
	 */
	public synchronized void addFileInfo(String filename,String md5,String size){
		if(!isExist(md5)){
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				document = (Document)builder.parse(new File(downLoadConfig));
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
			Element fileNode = document.createElement("file");
			Element filenameNode=document.createElement("filename");
			filenameNode.appendChild(document.createTextNode(filename));
			Element md5Node=document.createElement("md5");
			md5Node.appendChild(document.createTextNode(md5));
			Element sizeNode=document.createElement("size");
			sizeNode.appendChild(document.createTextNode(size));
			Element percentageNode=document.createElement("percentage");
			percentageNode.appendChild(document.createTextNode("0%"));
			Element statusNode=document.createElement("status");
			statusNode.appendChild(document.createTextNode("paused"));
			Element connectstatusNode=document.createElement("connectstatus");
			connectstatusNode.appendChild(document.createTextNode("connected"));
			Element finishDateNode=document.createElement("finishDate");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			finishDateNode.appendChild(document.createTextNode(dateFormat.format(new Date())));
			Element filepathNode=document.createElement("filepath");
			filepathNode.appendChild(document.createTextNode(FirsttimeChecker.SharedPath+"\\"+filename));
			fileNode.appendChild(filenameNode);
			fileNode.appendChild(md5Node);
			fileNode.appendChild(percentageNode);
			fileNode.appendChild(statusNode);
			fileNode.appendChild(sizeNode);
			fileNode.appendChild(connectstatusNode);
			fileNode.appendChild(finishDateNode);
			fileNode.appendChild(filepathNode);
			root.appendChild(fileNode);
			save();
		}
	}
	
	
	public synchronized void removeFileInfo(String md5){
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				document = (Document)builder.parse(new File(downLoadConfig));
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
							System.out.println(removedNode.getNodeName()+"{}{}");
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
	 * 更新文件
	 * @param md5			MD5码
	 * @param nodeName		修改的节点名
	 * @param value			修改的值
	 */
	public synchronized void updateFileInfo(String md5,String nodeName,String value){
		try {  
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
			factory.setValidating(false);  
			DocumentBuilder builder = factory.newDocumentBuilder();  
			document = builder.parse(new File(downLoadConfig));  
			//document.getDocumentElement().normalize();  
			Node node = document.getFirstChild(); 
			System.out.println(node.getNodeName());
			NodeList list = node.getChildNodes();  
			for (int i = 0; i < list.getLength(); i++) {  
				Node nodeItem = list.item(i);
				dom(nodeItem,md5,nodeName,value);
			}  
		} catch (Exception exp) {  
			exp.printStackTrace();   
		} 
		save();
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
			PrintWriter pw = new PrintWriter(new FileOutputStream(downLoadConfig));
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
		} catch (TransformerException mye) {
			mye.printStackTrace();
		} catch (IOException exp) {
			exp.printStackTrace();
		}
	}
	
	/**
	 * 递归遍历节点
	 * @param rootNode	根节点
	 * @param md5		MD5码
	 * @param nodeName	要修改的节点名
	 * @param value		要修改的值
	 */
	private synchronized void dom(Node rootNode,String md5,String nodeName,String value){
		NodeList nodes=rootNode.getChildNodes();
		for(int i=0;i<nodes.getLength();i++){
			Node nodeItem = (Node)nodes.item(i); 
			if(nodeItem.getNodeName().equals("md5")){
				if(nodeItem.getTextContent().equals(md5)){
					Node parentNode=nodeItem.getParentNode();
					NodeList childNodes=parentNode.getChildNodes();
					for(int j=0;j<childNodes.getLength();j++){
						Node childNode=childNodes.item(j);
						if(childNode.getNodeName().equals(nodeName)){
							childNode.setTextContent(value);
							
						}
					}
					break;
				}
			}
			dom(nodeItem,md5,nodeName,value);
		}
	}
	
	public synchronized boolean isExist(String md5){
		boolean isExist=false;
		try {  
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
			factory.setValidating(false);  
			DocumentBuilder builder = factory.newDocumentBuilder();  
			document = builder.parse(new File(downLoadConfig));    
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
	public synchronized  int getUnloadedFileRowByMD5(String md5){
		int row=-1;
		ArrayList fileList=(ArrayList)this.getFilesInfo();
		for(int i=0;i<fileList.size();i++){
			ddm.p2p.myfilesharing.model.File file=(ddm.p2p.myfilesharing.model.File)fileList.get(i);
			if(file.getStatus().equals("paused")){
				row++;
				if(file.getMD5().equals(md5)){
					break;
				}
			}
		}
		return row;
	}
	
	
	/**
	 * 得到文件信息
	 * @return
	 */
	public synchronized  List getFilesInfo(){
		ArrayList list=new ArrayList();
		try {  
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
			factory.setValidating(false);  
			DocumentBuilder builder = factory.newDocumentBuilder();  
			document = builder.parse(new File(downLoadConfig));    
			Node node = document.getFirstChild(); 
			NodeList nodes = node.getChildNodes(); 
			for (int i = 0; i < nodes.getLength(); i++) {
				Node nodeItem = nodes.item(i);
				if(nodeItem.getNodeType()==Node.ELEMENT_NODE){
					ddm.p2p.myfilesharing.model.File file=new ddm.p2p.myfilesharing.model.File();
					NodeList nodesOfChild=nodeItem.getChildNodes();
					for(int j=0; j <nodesOfChild.getLength();j++){
						Node nodeItemOfChild=nodesOfChild.item(j);
						if(nodeItemOfChild.getNodeName().equals("percentage")){
							file.setPercentageLoaded(nodeItemOfChild.getTextContent());
						}
						if(nodeItemOfChild.getNodeName().equals("filename")){
							file.setFilename(nodeItemOfChild.getTextContent());
						}
						if(nodeItemOfChild.getNodeName().equals("md5")){
							file.setMD5(nodeItemOfChild.getTextContent());
						}
						if(nodeItemOfChild.getNodeName().equals("status")){
							file.setStatus(nodeItemOfChild.getTextContent());
						}
						if(nodeItemOfChild.getNodeName().equals("size")){
							file.setFilesize(nodeItemOfChild.getTextContent());
						}
						if(nodeItemOfChild.getNodeName().equals("connectstatus")){
							file.setConnectstatus(nodeItemOfChild.getTextContent());
						}
						if(nodeItemOfChild.getNodeName().equals("finishDate")){
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							file.setFinishDate(dateFormat.parse(nodeItemOfChild.getTextContent()));
						}
						if(nodeItemOfChild.getNodeName().equals("filepath")){
							file.setFilePath(nodeItemOfChild.getTextContent());
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
