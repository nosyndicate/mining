package ddm.p2p.myfilesharing.chat.util;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;


import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import ddm.p2p.myfilesharing.chat.model.ChatMessage;
import ddm.p2p.myfilesharing.chat.model.OfflineMessage;
import ddm.p2p.myfilesharing.chat.model.OnlineMessage;
import ddm.p2p.myfilesharing.ddm.DDMMessage;
import ddm.p2p.myfilesharing.utils.AddressUtil;

/**
 * 发送XML形式的消息
 */
public class XmlSender {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(XmlSender.class.getName());
	
	public static String createOnlineMessage() {
        Document document = DocumentHelper.createDocument();
        
        Element root = document.addElement("root").addAttribute("class", OnlineMessage.class.getCanonicalName());

        root.addElement("hostName").addText(AddressUtil.getHostName());
        root.addElement("userName").addText(AddressUtil.getUserName());
        root.addElement("computerName").addText(AddressUtil.getComputerName());
        root.addElement("ip").addText(AddressUtil.getIPAddress());

        return document.asXML();
	}
	
	public static String createOfflineMessage() {
        Document document = DocumentHelper.createDocument();
        
        Element root = document.addElement("root").addAttribute("class", OfflineMessage.class.getCanonicalName());

        root.addElement("hostName").addText(AddressUtil.getHostName());
        root.addElement("userName").addText(AddressUtil.getUserName());
        
        return document.asXML();
	}
	
	public static String createChatMessage(String message) {
        Document document = DocumentHelper.createDocument();
        
        Element root = document.addElement("root").addAttribute("class", ChatMessage.class.getCanonicalName());

        root.addElement("hostName").addText(AddressUtil.getHostName());
        root.addElement("userName").addText(AddressUtil.getUserName());
        root.addElement("message").addText(message);
        
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date=dateFormat.format(new Date());
        root.addElement("datetime").addText(date);
        
        return document.asXML();
	}    
	
	public static String createDDMMessage(String id,String sum,String count) {
	        Document document = DocumentHelper.createDocument();
	        
	        Element root = document.addElement("root").addAttribute("class", DDMMessage.class.getCanonicalName());

	        root.addElement("id").addText(id);
	        root.addElement("sum").addText(sum);
	        root.addElement("count").addText(count);
	                
	        return document.asXML();
	}
	
}
