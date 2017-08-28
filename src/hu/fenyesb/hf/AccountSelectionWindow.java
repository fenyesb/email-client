package hu.fenyesb.hf;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

/**
 * A felhasználó ebben az ablakban az eltárolt fiókok listáját láthatja.
 * Ezekhez egy újat felvehet, egy meglévőt módosíthat, vagy törölhet.
 * A felhasználó a fiók kijelölése után beléphet a kiválasztott fiókba.
 * @author bal
 *
 */
@SuppressWarnings("serial")
public class AccountSelectionWindow extends JDialog {

	private AccountManager accountManager;
	private JList<AccountInfo> list;
	private boolean ready = false;
	
	/**
	 * A listában kijelölt fiók. Csak akkor ad vissza érvényeset, ha
	 * van kijelölt érték és az ablakot bezárták.
	 * @return a listában kijelölt fiók, vagy hiba esetén null
	 */
	public AccountInfo GetAccountInfo()
	{
		if(!ready||list.isSelectionEmpty())
			return null;
		return list.getSelectedValue();
	}
	
	/**
	 * Create the frame.
	 */
	public AccountSelectionWindow() {
		accountManager = new AccountManager("accounts.xml");
		
		this.setTitle("Account selection");
		this.setBounds(100, 100, 450, 300);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("Select an email account, or create a new one!");
		this.getContentPane().add(lblNewLabel, BorderLayout.NORTH);
		
		list = new JList<>();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setModel(accountManager.GetListModel());
		this.getContentPane().add(list);
		
		JPanel panel = new JPanel();
		this.getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnCreateNew = new JButton("Create new");
		btnCreateNew.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AccountEditDialog accountEditDialog = new AccountEditDialog();
				accountEditDialog.setModal(true);
				accountEditDialog.setVisible(true);
				AccountInfo accountInfo = accountEditDialog.GetAccountInfo();
				if(accountInfo != null){
					accountManager.GetListModel().addElement(accountInfo);
					accountManager.Save();
				}
			}	
		
		});
		panel.add(btnCreateNew);
		
		JButton btnLogIn = new JButton("Log in");
		btnLogIn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!list.isSelectionEmpty())
				{
					ready = true;
					setVisible(false);
				}
			}
		});
		panel.add(btnLogIn);
		
		JButton btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!list.isSelectionEmpty())
				{
					AccountEditDialog accountEditDialog = new AccountEditDialog();
					accountEditDialog.setModal(true);
					accountEditDialog.SetAccountInfo(list.getSelectedValue());
					accountEditDialog.setVisible(true);
					AccountInfo accountInfo = accountEditDialog.GetAccountInfo();
					if(accountInfo != null){
						accountManager.GetListModel().remove(list.getSelectedIndex());
						accountManager.GetListModel().addElement(accountInfo);
						accountManager.Save();
					}
					accountManager.Save();
				}
			}
		});
		panel.add(btnEdit);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!list.isSelectionEmpty())
				{
					accountManager.GetListModel().remove(list.getSelectedIndex());
					accountManager.Save();
				}
			}
			
		});
		panel.add(btnRemove);
	}
}

