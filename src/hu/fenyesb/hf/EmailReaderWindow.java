package hu.fenyesb.hf;

import java.awt.BorderLayout;

import javax.mail.Message;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

/**
 * Egy Message osztály tartalmát olvasásra megjelenítő ablak.
 * @author bal
 *
 */
@SuppressWarnings("serial")
public class EmailReaderWindow extends JFrame {

	private JPanel contentPane;
	private JTextField txtFrom;
	private JTextField txtSubject;
	private JEditorPane editorPane;
	private Message message;
	
	/**
	 * A megjelenítendő üzenet beállítása.
	 * @param msg az üzenet
	 */
	public void SetMessage(Message msg)
	{
		message = msg;
		try {
			txtFrom.setText(message.getFrom()[0].toString());
			txtSubject.setText(message.getSubject());
			editorPane.setText(MessageTextParser.getTextFromMessage(message));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create the frame.
	 */
	public EmailReaderWindow() {
		setTitle("Read message");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		JLabel lblFrom = new JLabel("From:");
		
		txtFrom = new JTextField();
		txtFrom.setColumns(5);
		txtFrom.setEditable(false);
		
		JLabel lblSubject = new JLabel("Subject:");
		
		txtSubject = new JTextField();
		txtSubject.setColumns(5);
		txtSubject.setEditable(false);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblFrom)
						.addComponent(lblSubject))
					.addGap(28)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(txtSubject)
						.addComponent(txtFrom, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE))
					.addContainerGap(16, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblFrom)
						.addComponent(txtFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtSubject, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblSubject))
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
		
		editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		editorPane.setEditable(false);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(editorPane);
		contentPane.add(scrollPane, BorderLayout.CENTER);
	}
}
