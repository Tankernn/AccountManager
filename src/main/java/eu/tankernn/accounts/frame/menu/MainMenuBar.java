package eu.tankernn.accounts.frame.menu;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import eu.tankernn.accounts.Account;
import eu.tankernn.accounts.AccountManager;
import eu.tankernn.accounts.DBManager;
import eu.tankernn.accounts.frame.MainFrame;
import eu.tankernn.accounts.frame.NewAccountDialog;

public class MainMenuBar extends JMenuBar implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8702523319236773512L;

	private MainFrame frame;

	// Menu Items

	private JMenu fileMenu = new JMenu("File");
	private JMenuItem newFile = new JMenuItem("New..."), openFile = new JMenuItem("Open..."),
			saveFile = new JMenuItem("Save"), saveFileAs = new JMenuItem("Save As...");

	private JMenu database = new JMenu("Database");
	private JMenuItem importDatabase = new JMenuItem("Import from database"),
			exportDatabase = new JMenuItem("Export to database");

	private JMenu accountMenu = new JMenu("Accounts");
	private JMenuItem newAccount = new JMenuItem("New account..."), refresh = new JMenuItem("Refresh accounts");

	private JMenu optionsMenu = new JMenu("Options");
	private JCheckBoxMenuItem useEncryption = new JCheckBoxMenuItem("Use encryption when saving files");

	public MainMenuBar(MainFrame frame) {
		this.frame = frame;

		// File menu
		addMenuWithItems(fileMenu, newFile, openFile, saveFile, saveFileAs);

		newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		saveFileAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK));

		// Database menu
		addMenuWithItems(database, importDatabase, exportDatabase);

		// Accounts menu
		addMenuWithItems(accountMenu, newAccount, refresh);

		// Options menu
		addMenuWithItems(optionsMenu, useEncryption);
		useEncryption.setSelected(AccountManager.isSavingWithEncryption());
	}

	private void addMenuWithItems(JMenu menu, JMenuItem... items) {
		this.add(menu);

		for (JMenuItem item : items) {
			menu.add(item);
			item.addActionListener(this);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src.equals(newFile)) {
			AccountManager.newFile();
		} else if (src.equals(openFile)) {
			AccountManager.openFile();
		} else if (src.equals(saveFile)) {
			AccountManager.saveFile(false);
		} else if (src.equals(saveFileAs)) {
			AccountManager.saveFile(true);
		} else if (src.equals(importDatabase)) {
			if (AccountManager.closeFile()) {
				DBManager.init();
				for (Account a : DBManager.readAccounts())
					AccountManager.addAccount(a);
			}
		} else if (src.equals(exportDatabase)) {
			DBManager.init();
			DBManager.saveAccounts(AccountManager.getAccounts());
		} else if (src.equals(newAccount)) {
			NewAccountDialog dialog = new NewAccountDialog();
			if (dialog.validate()) {
				AccountManager.addAccount(new Account(dialog.getFirstName(), dialog.getLastName()));
			}
		} else if (src.equals(refresh)) {
			frame.refresh();
		} else if (src.equals(useEncryption)) {
			AccountManager.setSaveWithEncryption(useEncryption.isSelected());
		}
	}

}
