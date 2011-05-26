package ddm.p2p.myfilesharing.chat.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author 刘浩
 * 解析XML文件
 */
public class XmlReceiver {
	private static final Logger LOG = Logger.getLogger(XmlReceiver.class.getName());
	
	@SuppressWarnings("unchecked")
	public static Object getObject(String xml) {
		Object o = null;
		try {
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();
			
			try {
				Class c = ClassUtils.getClass(root.attributeValue("class"));
				o = c.newInstance();
				
		        for (Iterator i = root.elementIterator(); i.hasNext(); ) {
		            Element element = (Element) i.next();
		            
		            Method method = c.getDeclaredMethod("set" + StringUtils.capitalize(element.getName()), new Class[] {String.class});
		            method.invoke(o, element.getText());
		        }
				
		        return o;
			} catch (Exception e) {
				LOG.warning("Class initialize failed!");
				e.printStackTrace();
				return null;
			}

		} catch (DocumentException e) {
			LOG.warning("getOnlineMsg() failed");
			e.printStackTrace();
		}
		
		return null;
	}
	
}
