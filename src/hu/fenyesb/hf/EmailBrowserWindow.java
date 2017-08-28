package hu.fenyesb.hf;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;

/**
 * A felhasználó a beérkezett leveleket láthatja egy görgethető listában.
 * Kattintásra a levél egy új ablakban elolvasható.
 * @author bal
 *
 */
@SuppressWarnings("serial")
public class EmailBrowserWindow extends JFrame {

	private JFrame frame;
	private AccountInfo accountInfo;
	private EmailManager emailManager;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EmailBrowserWindow window = new EmailBrowserWindow();
					if (window.accountInfo != null) {
						window.frame.setVisible(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public EmailBrowserWindow() {

		AccountSelectionWindow accountSelectionWindow = new AccountSelectionWindow();
		accountSelectionWindow.setModal(true);
		accountSelectionWindow.setVisible(true);
		accountInfo = accountSelectionWindow.GetAccountInfo();
		if (accountInfo != null) {
			emailManager = new EmailManager(accountInfo);
			initialize();
			frame.setTitle("Email client -- Balázs Fényes -- f.balazs96@gmail.com");
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(getContentPane());

		JLabel lblNewLabel = new JLabel("Choose a folder to load the emails!");
		getContentPane().add(lblNewLabel, BorderLayout.NORTH);

		JList<Message> list = new JList<Message>();
		list.setModel(emailManager.GetListModel());
		list.setCellRenderer(new MessageListCellRenderer());
		list.setFixedCellHeight(55);
		list.setFixedCellWidth(frame.getWidth());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				try {
					System.out.println("selected: " + list.getSelectedValue().getSubject());
					EmailReaderWindow emailReader = new EmailReaderWindow();
					emailReader.SetMessage(list.getSelectedValue());
					emailReader.setVisible(true);
				} catch (MessagingException e1) {
					e1.printStackTrace();
				}
			}
		});
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(list);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(emailManager.GetAllFolderNames()));
		comboBox.setSelectedItem(null);
		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				emailManager.SetFolder(comboBox.getSelectedItem().toString());
				list.revalidate();
				scrollPane.revalidate();
			}

		});
		panel.add(comboBox);

		JButton btnRefresh = new JButton("Refresh folder");
		btnRefresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				list.revalidate();
				scrollPane.revalidate();
			}
		});
		panel.add(btnRefresh);
		

		JButton btnCreate = new JButton("Create email");
		btnCreate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				EmailWriterWindow emailWriterWindow = new EmailWriterWindow(emailManager);
				emailWriterWindow.setVisible(true);
			}
		});
		panel.add(btnCreate);
	}

}
