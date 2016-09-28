package eu.tankernn.accounts.frame;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class NewAccountDialog {

	JLabel lFirstName = new JLabel("First name:"), lLastName = new JLabel("Last name:");
	JTextField firstName = new JTextField(20), lastName = new JTextField(20);
	
	final JComponent[] components = {
			lFirstName, firstName, lLastName, lastName,
	};
	
	int result;

	/**
	 * Create the dialog.
	 */
	public NewAccountDialog() {
		result = JOptionPane.showConfirmDialog(null, components, "New Account", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	public String getFirstName() {
		return firstName.getText().trim();
	}

	public String getLastName() {
		return lastName.getText().trim();
	}

	public int getResult() {
		return result;
	}

	public boolean validate() {
		if (result != JOptionPane.OK_OPTION)
			return false;
		
		if (firstName.getText().isEmpty() || lastName.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please fill in all the fields.");
			return false;
		}
		
		return true;
	}

}