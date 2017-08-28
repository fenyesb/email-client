package hu.fenyesb.hf;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

/**
 * A felhasználó itt módosíthatja a fiók adatait (cím, jelszó, szerver, port).
 * @author bal
 *
 */
@SuppressWarnings("serial")
public class AccountEditDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblEmailAddress;
	private JTextField txtEmailAddress;
	private JPasswordField passwordField;
	private boolean ready = false;
	private JTextField txtImapServer;
	private JTextField txtImapPort;
	private JLabel lblSmtpServerPort;
	private JTextField txtSmtpServer;
	private JTextField txtSmtpPort;
	
	/**
	 * Az ablakban megjelenítendő fiókinformációk beállítása.
	 * @param accountInfo a fiókinformáció
	 */
	public void SetAccountInfo(AccountInfo accountInfo)
	{
		txtEmailAddress.setText(accountInfo.EmailAddress);
		passwordField.setText("");
		txtImapServer.setText(accountInfo.ImapServer);
		txtImapPort.setText(accountInfo.ImapPort+"");
		txtSmtpServer.setText(accountInfo.SmtpServer);
		txtSmtpPort.setText(accountInfo.SmtpPort+"");
	}
	
	/**
	 * Az ablakban beállított fiókinformációk lekérdezése. Csak az ablak bezárása után kérdezhető le.
	 * @return a fiókinformáció, vagy hiba esetén null (ha üres a cím vagy a jelszó)
	 */
	public AccountInfo GetAccountInfo()
	{
		String str_email = txtEmailAddress.getText();
		String str_pw = new String(passwordField.getPassword());
		if(!ready|| str_email.isEmpty()||str_pw.isEmpty())
			return null;
		AccountInfo accountInfo = new AccountInfo();
		accountInfo.EmailAddress = txtEmailAddress.getText();
		accountInfo.EncryptedPassword = AccountInfo.EncryptPassword(new String(passwordField.getPassword()));
		accountInfo.ImapServer = txtImapServer.getText();
		accountInfo.ImapPort = Integer.parseInt(txtImapPort.getText());
		accountInfo.SmtpServer = txtSmtpServer.getText();
		accountInfo.SmtpPort = Integer.parseInt(txtSmtpPort.getText());
		return accountInfo;
	}

	/**
	 * Create the dialog.
	 */
	public AccountEditDialog() {
		setTitle("Edit account details");
		setBounds(100, 100, 428, 194);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			lblEmailAddress = new JLabel("Email address:");
			lblEmailAddress.setBounds(17, 19, 104, 15);
		}
		
		txtEmailAddress = new JTextField();
		txtEmailAddress.setBounds(164, 17, 219, 19);
		txtEmailAddress.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(17, 50, 75, 15);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(164, 48, 219, 19);
		JLabel lblImap = new JLabel("IMAP server, port:");
		lblImap.setBounds(17, 81, 129, 15);
		txtImapServer = new JTextField();
		txtImapServer.setBounds(164, 79, 159, 19);
		txtImapServer.setColumns(10);
		contentPanel.setLayout(null);
		
		txtImapPort = new JTextField();
		txtImapPort.setBounds(335, 79, 48, 19);
		txtImapPort.setColumns(10);
		contentPanel.add(txtImapPort);
		contentPanel.add(lblImap);
		contentPanel.add(txtImapServer);
		contentPanel.add(lblEmailAddress);
		contentPanel.add(txtEmailAddress);
		contentPanel.add(lblPassword);
		contentPanel.add(passwordField);
		
		lblSmtpServerPort = new JLabel("SMTP server, port:");
		lblSmtpServerPort.setBounds(17, 112, 139, 15);
		contentPanel.add(lblSmtpServerPort);
		
		txtSmtpServer = new JTextField();
		txtSmtpServer.setColumns(10);
		txtSmtpServer.setBounds(164, 110, 159, 19);
		contentPanel.add(txtSmtpServer);
		
		txtSmtpPort = new JTextField();
		txtSmtpPort.setColumns(10);
		txtSmtpPort.setBounds(335, 110, 48, 19);
		contentPanel.add(txtSmtpPort);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						ready = true;
						setVisible(false);
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				buttonPane.add(cancelButton);
			}
		}
	}
}
