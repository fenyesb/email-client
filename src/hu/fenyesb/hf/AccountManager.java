package hu.fenyesb.hf;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Kezeli a fiókok tárolását és betöltését egy XML fájl segítségével.
 * @author bal
 *
 */
public class AccountManager {
	private DefaultListModel<AccountInfo> defaultListModel;
	private static String XML_path;
	
	/**
	 * A fiókkezelő létrehozása, ami a megadott XML fájlt használja a fiókok tárolására.
	 * @param path az XML fájl útvonala
	 */
	public AccountManager(String path)
	{
		XML_path = path;
		defaultListModel = new DefaultListModel<>();
		Load();
	}
	
	/**
	 * Fiókadatok betöltése az XML fájlból.
	 */
	public void Load()
	{
		defaultListModel.clear();
		List<AccountInfo> list = new ArrayList<>();
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(new FileInputStream(XML_path));
			NodeList nodeList = document.getElementsByTagName("accountinfo");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if(node.getNodeType()==Node.ELEMENT_NODE)
				{
					Element element = (Element)node;
					AccountInfo accountInfo = new AccountInfo();
					accountInfo.EmailAddress = element.getElementsByTagName("address").item(0).getTextContent();
					accountInfo.EncryptedPassword = element.getElementsByTagName("password").item(0).getTextContent();
					accountInfo.SmtpServer = element.getElementsByTagName("smtp_server").item(0).getTextContent();
					accountInfo.ImapServer = element.getElementsByTagName("imap_server").item(0).getTextContent();
					accountInfo.SmtpPort = Integer.parseInt(element.getElementsByTagName("smtp_port").item(0).getTextContent());
					accountInfo.ImapPort = Integer.parseInt(element.getElementsByTagName("imap_port").item(0).getTextContent());
					list.add(accountInfo);
					defaultListModel.addElement(accountInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			defaultListModel.clear();
		}
	}
	
	/**
	 * Fiókadatok elmentése az XML fájlba.
	 */
	public void Save()
	{
		try{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			Element rootelement = document.createElement("accountlist");
			for(int i = 0; i < defaultListModel.size(); i++)
			{
				Element element = document.createElement("accountinfo");
				Element subelement = document.createElement("address");
				Text textnode = document.createTextNode(defaultListModel.get(i).EmailAddress);
				subelement.appendChild(textnode);
				element.appendChild(subelement);
				subelement = document.createElement("password");
				textnode = document.createTextNode(defaultListModel.get(i).EncryptedPassword);
				subelement.appendChild(textnode);
				element.appendChild(subelement);
				subelement = document.createElement("smtp_server");
				textnode = document.createTextNode(defaultListModel.get(i).SmtpServer);
				subelement.appendChild(textnode);
				element.appendChild(subelement);
				subelement = document.createElement("smtp_port");
				textnode = document.createTextNode(defaultListModel.get(i).SmtpPort+"");
				subelement.appendChild(textnode);
				element.appendChild(subelement);
				subelement = document.createElement("imap_server");
				textnode = document.createTextNode(defaultListModel.get(i).ImapServer);
				subelement.appendChild(textnode);
				element.appendChild(subelement);
				subelement = document.createElement("imap_port");
				textnode = document.createTextNode(defaultListModel.get(i).ImapPort+"");
				subelement.appendChild(textnode);
				element.appendChild(subelement);
				rootelement.appendChild(element);
			}
			document.appendChild(rootelement);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.transform(new DOMSource(document), new StreamResult(new File(XML_path)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Egy JList-ben használható ListModel-t ad vissza.
	 * @return a fiókokat reprezentáló lista
	 */
	public DefaultListModel<AccountInfo> GetListModel()
	{
		return defaultListModel;
	}
}

