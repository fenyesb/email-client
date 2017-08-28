package hu.fenyesb.hf;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

/**
 * Ebben az ablakban egy email üzenet írására van lehetőség.
 * A felhasználó megadhatja a címzett, tárgy és szöveg mezőket.
 * Ha a felhasználó fájlokat húz a szöveg mezőre, akkor az üzenethez csatolmányokat ad hozzá.
 * Az üzenetet az ablak alján található gombbal tudja elküldeni.
 * @author bal
 *
 */
@SuppressWarnings("serial")
public class EmailWriterWindow extends JFrame implements DropTargetListener {

	private JPanel contentPane;
	private JTextField txtTo;
	private JTextField txtSubject;
	private JTextArea textArea;
	private EmailManager emailManager;
	private List<File> attachments;
	
	/**
	 * A megadott beviteli mezők és a hozzáadott csatolmányok alapján az üzenet összeállítása.
	 * @return az elküldendő üzenet
	 */
	public Message GetMessage() {
		Message message = new MimeMessage(emailManager.GetSession());
		try {
			message.setFrom(new InternetAddress(emailManager.GetAccountInfo().EmailAddress));
			message.setSubject(txtSubject.getText());
			message.addRecipient(RecipientType.TO, new InternetAddress(txtTo.getText()));
			
			Multipart multipart = new MimeMultipart();
			BodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setText(txtSubject.getText());
			multipart.addBodyPart(textBodyPart);
			
			for (File file : attachments) {
				BodyPart aBodyPart = new MimeBodyPart();
				DataSource dataSource = new FileDataSource(file);
				aBodyPart.setDataHandler(new DataHandler(dataSource));
				aBodyPart.setFileName(file.getName());
				multipart.addBodyPart(aBodyPart);
			}
			
			message.setContent(multipart);
			return message;
		} catch (MessagingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Create the application.
	 */
	public EmailWriterWindow(EmailManager emailManager) {
		setTitle("Create email");
		this.emailManager = emailManager;
		initialize();
		new DropTarget(textArea, this);
		attachments = new ArrayList<File>();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);

		JLabel lblFrom = new JLabel("Címzett:");

		txtTo = new JTextField();
		txtTo.setColumns(5);
		txtTo.setEditable(true);

		JLabel lblSubject = new JLabel("Tárgy:");

		txtSubject = new JTextField();
		txtSubject.setColumns(5);
		txtSubject.setEditable(true);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel
				.createSequentialGroup().addContainerGap()
				.addGroup(
						gl_panel.createParallelGroup(Alignment.LEADING).addComponent(lblFrom).addComponent(lblSubject))
				.addGap(28)
				.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false).addComponent(txtSubject)
						.addComponent(txtTo, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE))
				.addContainerGap(16, Short.MAX_VALUE)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel
				.createSequentialGroup()
				.addGroup(gl_panel.createParallelGroup(Alignment.LEADING).addComponent(lblFrom).addComponent(txtTo,
						GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(txtSubject,
						GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblSubject))
				.addContainerGap()));
		panel.setLayout(gl_panel);

		textArea = new JTextArea();
		textArea.setEditable(true);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(textArea);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				emailManager.SendMessage(GetMessage());
			}
		});
		panel_1.add(btnSend);
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
	}
	
	/**
	 * Ha a szöveg beviteli mezőre fájlt dob a felhasználó, akkor az
	 * csatolmányként hozzáadódik majd az üzenethez.
	 */
	@Override
	public void drop(DropTargetDropEvent dtde) {
		System.out.println("drop");
		dtde.acceptDrop(DnDConstants.ACTION_COPY);
		Transferable transferable = dtde.getTransferable();
		DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();
		try {
			for (DataFlavor dataFlavor : dataFlavors) {
				if (dataFlavor.isFlavorJavaFileListType()) {
					@SuppressWarnings("unchecked")
					List<File> files = (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);
					attachments.addAll(files);
					setTitle("Levélírás - " + attachments.size() + " csatolmány");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
