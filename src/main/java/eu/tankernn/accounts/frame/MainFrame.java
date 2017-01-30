package eu.tankernn.accounts.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import eu.tankernn.accounts.Account;
import eu.tankernn.accounts.AccountManager;
import eu.tankernn.accounts.frame.menu.MainMenuBar;
import eu.tankernn.accounts.util.GUIUtils;

public class MainFrame implements ListSelectionListener, DocumentListener {

	private JFrame frame;
	private LayoutManager manager;

	// GUI components

	private MainMenuBar menubar;

	private JPanel listPanel;
	private JTextField search;
	private JList<Account> accounts;
	private JScrollPane accountScrollPane;

	public final AccountPanel accountPanel = new AccountPanel();;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// Weird hack to make the save dialog display on Cmd + q
		System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
		
		EventQueue.invokeLater(() -> {
				try {
					MainFrame window = new MainFrame();
					AccountManager.init(window::refresh, true);
					window.refresh();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
		});
	}

	/**
	 * Create the application.
	 */
	public MainFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		manager = new BorderLayout();
		menubar = new MainMenuBar(this);
		search = new JTextField("Search...");
		accounts = new JList<Account>();

		frame = new JFrame();
		frame.setLayout(manager);
		frame.setJMenuBar(menubar);

		listPanel = new JPanel();
		listPanel.setLayout(new BorderLayout());
		search.getDocument().addDocumentListener(this);
		listPanel.add(search, BorderLayout.NORTH);
		accounts.addListSelectionListener(this);
		accountScrollPane = new JScrollPane(accounts);
		accountScrollPane.setPreferredSize(new Dimension(100, 100));
		listPanel.add(accountScrollPane, BorderLayout.CENTER);
		frame.add(listPanel, BorderLayout.WEST);
		
		frame.add(accountPanel, BorderLayout.EAST);

		frame.setTitle("Account Management System");
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// Ask the user to save changes before quitting
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				if (AccountManager.closeFile()) {
					frame.dispose();
				}
			}
		});
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (accounts.getSelectedValue() != null)
			accountPanel.updatePanel(accounts.getSelectedValue());
	}

	public void refresh() {
		accounts.setModel(GUIUtils.listModelFromList(AccountManager.getAccounts()));
		accountPanel.updatePanel(null);
	}

	private void search() {
		String s = search.getText().trim();
		accounts.setModel(GUIUtils.listModelFromList(AccountManager.search(s)));
	}

	// Update list on search field change

	@Override
	public void insertUpdate(DocumentEvent e) {
		search();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		search();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		search();
	}

}
