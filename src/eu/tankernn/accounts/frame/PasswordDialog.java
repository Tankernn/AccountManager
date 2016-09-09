package eu.tankernn.accounts.frame;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class PasswordDialog {
	
	private JLabel label = new JLabel();
	private JPasswordField password;
	private int result;

	/**
	 * Create the dialog.
	 */
	private PasswordDialog(String message) {
		this.label.setText(message);
		password = new JPasswordField();
		result = JOptionPane.showConfirmDialog(null, new JComponent[] {label, password}, "Enter password", JOptionPane.OK_CANCEL_OPTION);
	}
	
	public static char[] showPasswordDialog(String message) {
		PasswordDialog dialog = new PasswordDialog(message);
		if (dialog.result == JOptionPane.OK_OPTION)
			return dialog.password.getPassword();
		else
			return null;
	}

}
