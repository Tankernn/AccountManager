package eu.tankernn.accounts.frame.menu;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import eu.tankernn.accounts.Account;
import eu.tankernn.accounts.AccountManager;
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

	private JMenu accountMenu = new JMenu("Accounts");
	private JMenuItem newAccount = new JMenuItem("New account..."), refresh = new JMenuItem("Refresh accounts");

	public MainMenuBar(MainFrame frame) {
		this.frame = frame;
		
		// File menu
		addMenuWithItems(fileMenu, newFile, openFile, saveFile, saveFileAs);

		newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		saveFileAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK));

		// Accounts menu
		addMenuWithItems(accountMenu, newAccount, refresh);

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
		} else if (src.equals(newAccount)) {
			NewAccountDialog dialog = new NewAccountDialog();
			if (dialog.validate()) {
				AccountManager
						.addAccount(new Account(dialog.getFirstName(), dialog.getLastName()));
			}
		} else if (src.equals(refresh)) {
			frame.refresh();
		}
	}

}
