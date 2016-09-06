package eu.tankernn.accounts.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import eu.tankernn.accounts.Account;
import eu.tankernn.accounts.AccountEvent;
import eu.tankernn.accounts.AccountManager;

public class AccountPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Account currentAccount;

	private JPanel infoPanel = new JPanel();
	private JLabel lName = new JLabel("Name: "), lBalance = new JLabel("Balance: "),
			lAccountNumber = new JLabel("Account number: ");
	private JButton transferFrom = new JButton("Transfer from this account..."),
			deposit = new JButton("Deposit to this account..."),
			withdraw = new JButton("Withdraw from this account...");

	private JList<AccountEvent> history = new JList<AccountEvent>();
	private JScrollPane scrollPane = new JScrollPane(history);

	private JComboBox<Account> otherAccounts = new JComboBox<Account>();

	/**
	 * Create the panel.
	 */
	public AccountPanel() {
		this.setLayout(new BorderLayout());

		infoPanel.setLayout(new GridLayout(6, 1));
		add(infoPanel, BorderLayout.WEST);
		infoPanel.add(lName);
		infoPanel.add(lBalance);
		infoPanel.add(lAccountNumber);

		infoPanel.add(transferFrom);
		infoPanel.add(deposit);
		infoPanel.add(withdraw);

		transferFrom.addActionListener(this);
		deposit.addActionListener(this);
		withdraw.addActionListener(this);

		add(scrollPane, BorderLayout.EAST);

		this.setBorder(new TitledBorder("Account panel"));
	}

	public void updatePanel(Account a) {
		this.currentAccount = a;
		
		if (a == null) {
			lName.setText("Name: ");
			lBalance.setText("Balance: ");
			lAccountNumber.setText("Account number: ");
			history.setModel(new DefaultListModel<AccountEvent>());
			
			transferFrom.setEnabled(false);
			deposit.setEnabled(false);
			withdraw.setEnabled(false);
		} else {
			lName.setText("Name: " + a.toString());
			lBalance.setText("Balance: " + a.calculateBalance());
			lAccountNumber.setText("Account number: " + a.getAccountNumber());
			history.setModel(GUIUtils.listModelFromList(a.getHistory()));
			
			// "Clone" account list
			List<Account> accounts = new ArrayList<Account>(AccountManager.getAccounts());
			// Can't transfer to self
			accounts.remove(a);
			otherAccounts.setModel(GUIUtils.comboBoxModelFromList(accounts));
			
			transferFrom.setEnabled(true);
			deposit.setEnabled(true);
			withdraw.setEnabled(true);
		}
		
		// Fix history list width.
		Dimension d = scrollPane.getPreferredSize();
		d.width = infoPanel.getWidth(); // Same as infopanel
		scrollPane.setPreferredSize(d);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src.equals(transferFrom)) {
			double amount = showAmountDialog("transfer");

			if (amount <= 0 || amount > currentAccount.calculateBalance()) {
				JOptionPane.showMessageDialog(null, "Invalid amount.");
				return;
			}

			JOptionPane.showConfirmDialog(null,
					new JComponent[] { new JLabel("Please select receiver account."), otherAccounts }, "Select account.", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);

			Account sender = currentAccount, receiver = (Account) otherAccounts.getSelectedItem();

			sender.getHistory().add(new AccountEvent(-amount, "Transferred " + amount + " to " + receiver + "."));
			receiver.getHistory().add(new AccountEvent(amount, "Received " + amount + " from " + sender + "."));
		} else if (src.equals(deposit)) {
			double amount = showAmountDialog("deposit");

			if (amount < 0) {
				JOptionPane.showMessageDialog(this, "Please enter a positive value.");
				return;
			}

			currentAccount.getHistory().add(new AccountEvent(amount, "User deposited " + amount + "."));
		} else if (src.equals(withdraw)) {
			double amount = showAmountDialog("withdraw");

			if (amount < 0) {
				JOptionPane.showMessageDialog(this, "Please enter a positive value.");
				return;
			}

			currentAccount.getHistory().add(new AccountEvent(-amount, "User withdrew " + amount + "."));
		}
		
		this.updatePanel(currentAccount);
	}

	private double showAmountDialog(String action) {
		String amountStr = JOptionPane.showInputDialog("Amount to " + action + ":");

		double amount = -1;
		try {
			amount = Double.parseDouble(amountStr);
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(null, "Please enter a valid number value.");
		}
		return amount;
	}
}
