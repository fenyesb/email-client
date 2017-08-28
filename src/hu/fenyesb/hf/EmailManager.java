package hu.fenyesb.hf;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.swing.AbstractListModel;

import com.sun.mail.imap.IMAPStore;
import com.sun.mail.smtp.SMTPTransport;

/**
 * Kezeli a levelek küldését és fogadását.
 * @author bal
 *
 */
public class EmailManager {
	private AccountInfo accountInfo;
	private Session session;
	private Store imapStore;
	private Transport smtpTransport;
	public Folder currentFolder;
	
	/**
	 * A JavaMail objektum beállításainak lekérdezése.
	 * @return a JavaMail beállításai
	 */
	public Session GetSession() {
		return session;
	}
	
	/**
	 * Az fiókinformációkat adja vissza.
	 * @return a fiókinformációk
	 */
	public AccountInfo GetAccountInfo() {
		return accountInfo;
	}
	
	/**
	 * Beállítja, hogy melyik IMAP mappából kérdezze le a leveleket.
	 * @param foldername a mappa neve, hiba esetén (pl. nem létező mappa) null
	 */
	public void SetFolder(String foldername) {
		try {
			currentFolder = imapStore.getFolder(foldername);
			currentFolder.open(Folder.READ_ONLY);
		} catch (MessagingException e) {
			currentFolder = null;
			e.printStackTrace();
		}
	}
	
	/**
	 * Az emailkezelő létrehozása a megadott belépési adatok és szerverbeállítások alapján.
	 * @param accountInfo a fiókadatok
	 */
	public EmailManager(AccountInfo accountInfo) {
		this.accountInfo = accountInfo;
		Properties props = System.getProperties();
		// http://stackoverflow.com/a/14089564
		props.put("mail.store.protocol", "imaps");
		props.put("mail.imaps.host", accountInfo.ImapServer);
		props.put("mail.imaps.user", accountInfo.EmailAddress);
		props.put("mail.imaps.password", AccountInfo.DecryptPassword(accountInfo.EncryptedPassword));
		props.put("mail.imaps.port", accountInfo.ImapPort);
		props.put("mail.imaps.auth", "true");

		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtps.starttls.enable", "true");
		props.put("mail.smtps.host", accountInfo.SmtpServer);
		props.put("mail.smtps.user", accountInfo.EmailAddress);
		props.put("mail.smtps.password", AccountInfo.DecryptPassword(accountInfo.EncryptedPassword));
		props.put("mail.smtps.port", accountInfo.SmtpPort);
		props.put("mail.smtps.auth", "true");
		try {
			session = Session.getDefaultInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(accountInfo.EmailAddress,
							AccountInfo.DecryptPassword(accountInfo.EncryptedPassword));
				}
			});

			imapStore = (IMAPStore) session.getStore();
			imapStore.connect(accountInfo.ImapServer, accountInfo.EmailAddress,
					AccountInfo.DecryptPassword(accountInfo.EncryptedPassword));

			smtpTransport = (SMTPTransport) session.getTransport();
			smtpTransport.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Az IMAP mappa adott sorszámú levelének lekérdezése.
	 * @param n
	 * @return hivatkozás a levélre, vagy hiba esetén null
	 */
	public Message GetMessage(int n) {
		try {
			if(currentFolder==null)
				throw new RuntimeException("currentFolder==null");
			return currentFolder.getMessage(currentFolder.getMessageCount() - n);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Az IMAP mappa méretének lekérdezése.
	 * @return a levelek száma, vagy hiba esetén 0
	 */
	public int GetMessageCount() {
		try {
			if(currentFolder==null)
				return 0;
			return currentFolder.getMessageCount();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Üzenet elküldése.
	 * @param message az üzenet
	 */
	public void SendMessage(Message message) {
		try {
			smtpTransport.sendMessage(message, message.getAllRecipients());
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Az IMAP mappák listájának lekérdezése.
	 * @return mappák nevei
	 */
	public String[] GetAllFolderNames() {
		try {
			Folder[] folders = imapStore.getDefaultFolder().list();
			String[] fnames = new String[folders.length];
			for (int i = 0; i < folders.length; i++) {
				fnames[i] = folders[i].getName();
			}
			return fnames;
		} catch (MessagingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Egy JList-ben használható ListModel.
	 * @return az üzeneteket reprezentáló lista
	 */
	@SuppressWarnings("serial")
	public AbstractListModel<Message> GetListModel() {
		return new AbstractListModel<Message>() {

			@Override
			public int getSize() {
				return GetMessageCount();
			}

			@Override
			public Message getElementAt(int index) {
				return GetMessage(index);
			}
		};

	}
}
