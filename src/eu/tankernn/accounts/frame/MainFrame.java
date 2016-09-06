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

public class MainFrame implements ListSelectionListener, DocumentListener {
	
	private JFrame frame;
	private LayoutManager manager;
	
	// Graphics components
	
	private MainMenuBar menubar;
	
	private JPanel listPanel;
	private JTextField search;
	private JList<Account> accounts;
	private JScrollPane accountScrollPane;
	
	private AccountPanel accountPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					AccountManager.init(window);
					window.refresh();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
		search = new JTextField();
		accounts = new JList<Account>();
		
		frame = new JFrame();
		frame.setLayout(manager);
		frame.setJMenuBar(menubar);
		
		search.getDocument().addDocumentListener(this);
		accounts.addListSelectionListener(this);
		accountScrollPane = new JScrollPane(accounts);
		accountScrollPane.setPreferredSize(new Dimension(100, 100));
		frame.add(accountScrollPane, BorderLayout.WEST);
		
		
		accountPanel = new AccountPanel();
		frame.add(accountPanel, BorderLayout.EAST);
		
		frame.setTitle("Account Management System");
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
		String s = search.getText();
		accounts.setModel(GUIUtils.listModelFromList(AccountManager.search(s)));
	}

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
