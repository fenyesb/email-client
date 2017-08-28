package hu.fenyesb.hf;

import java.awt.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Kirajzolja a Message osztályt egy JList cellájaként.
 * @author bal
 *
 */
public class MessageListCellRenderer implements ListCellRenderer<Message>{

	@Override
	public Component getListCellRendererComponent(JList<? extends Message> list, Message value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JLabel label = new  JLabel();
		try {
			label.setText("<html><i><font size=3 color=#808080>"+value.getFrom()[0]+"</font></i><br><font size=4>"+value.getSubject()+"</font><br><font color=#b0b0b0 size=3>"+value.getSentDate().toString());
		//System.out.println(value.getSubject());
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		if (isSelected) {
			label.setBackground(list.getSelectionBackground());
			label.setForeground(list.getSelectionForeground());
        } else {
        	label.setBackground(list.getBackground());
        	label.setForeground(list.getForeground());
        }
		label.setEnabled(list.isEnabled());
		label.setFont(list.getFont());
		label.setOpaque(true);
		return label;
	}

}
