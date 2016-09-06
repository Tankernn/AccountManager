package eu.tankernn.accounts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.tankernn.accounts.frame.MainFrame;
import eu.tankernn.accounts.util.Encryption;

public class AccountManager {
	private static String lastJSONString = "[]";
	private static String lastPassword;
	private static boolean saveWithEncryption = false;

	private static List<Account> accounts;

	private static MainFrame window;

	/**
	 * Initializes the account list using the last file opened, if available.
	 * Otherwise creates an empty list.
	 * 
	 * @param window
	 *            The <code>MainFrame</code> instance that will be updated once
	 *            the file has been loaded
	 */
	public static void init(MainFrame window) {
		AccountManager.window = window;
		accounts = new ArrayList<Account>();
		openFile(FileManager.getLastFileFromCache());
	}

	public static void openFile() {
		openFile(null);
	}

	/**
	 * Loads the account file specified.
	 * 
	 * @param file
	 *            The file to load
	 */
	public static void openFile(File file) {
		String rawString = null;
		try {
			rawString = FileManager.readFileAsString(file);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		if (rawString != null) {
			String password = null;
			String jsonString = new String(rawString);
			while (true) {
				try {
					jsonString = password == null ? new String(rawString) : Encryption.decrypt(rawString, password);
					accounts = parseJSON(jsonString);
					lastPassword = password;
					break;
				} catch (JSONException e) {
					if (e.getMessage().startsWith("A JSONArray text must start with '['")) {
						e.printStackTrace();
						password = JOptionPane.showInputDialog("Invalid JSON, try decrypting it with a password.");
					} else {
						e.printStackTrace();
						break;
					}
				}
			}
			lastJSONString = jsonString;
		} else
			accounts = new ArrayList<Account>();
		window.refresh();
	}

	/**
	 * Clears the account list and creates a new file to write the new accounts
	 * to.
	 */
	public static void newFile() {
		if (!closeFile())
			return;

		try {
			FileManager.newEmptyJSONFile();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		accounts.clear();
		lastJSONString = exportJSON();
	}

	/**
	 * Saves the current list of accounts to a file.
	 * 
	 * @param saveAs
	 *            Determines whether the user should be prompted to specify a
	 *            new filename, or if the last filename should be used.
	 */
	public static void saveFile(boolean saveAs) {
		try {
			String newData = exportJSON();
			String encryptedData = null;
			if (saveWithEncryption) {
				while (lastPassword == null || lastPassword.isEmpty()) {
					lastPassword = JOptionPane.showInputDialog("Select a password to encrypt the account file with.");
				}
				encryptedData = Encryption.encrypt(newData, lastPassword);
			}

			FileManager.writeStringToFile(saveAs, encryptedData == null ? newData : encryptedData);
			lastJSONString = newData;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Asks the user to save the current file if any changes have been made.
	 * 
	 * @return <code>false</code> if the user clicked cancel. True otherwise.
	 */
	public static boolean closeFile() {
		if (!AccountManager.hasUnsavedChanges()) {
			return true;
		} else {
			int option = JOptionPane.showOptionDialog(null, "Would you like to save changes before exit?",
					"Save changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 0);

			switch (option) {
			case JOptionPane.YES_OPTION:
				try {
					FileManager.writeStringToFile(false, AccountManager.exportJSON());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			case JOptionPane.NO_OPTION:
				return true;
			default:
				return false;
			}
		}
	}

	private static List<Account> parseJSON(String jsonString) throws JSONException {
		List<Account> parsedAccounts = new ArrayList<Account>();

		JSONArray array = new JSONArray(jsonString);

		for (int i = 0; i < array.length(); i++)
			parsedAccounts.add(Account.fromJSON(array.getJSONObject(i)));

		return parsedAccounts;
	}

	/**
	 * Encodes the current list of accounts into JSON.
	 * 
	 * @return The string containing the JSON-encoded string representing the
	 *         current list of accounts.
	 */
	public static String exportJSON() {
		JSONArray jsonArr = new JSONArray();
		for (Account a : accounts)
			jsonArr.put(new JSONObject(a));
		System.out.println(jsonArr.toString());
		return jsonArr.toString();
	}

	public static boolean hasUnsavedChanges() {
		return !exportJSON().equals(lastJSONString);
	}

	/**
	 * Adds the specified account to the list and refreshes the window instance.
	 * 
	 * @param account
	 *            The <code>Account</code> to be added to the list
	 */
	public static void addAccount(Account account) {
		accounts.add(account);
		window.refresh();
	}

	public static List<Account> search(String s) {
		return Arrays.asList(accounts.stream()
				.filter(a -> a.getAccountNumber().toLowerCase().contains(s) || a.toString().toLowerCase().contains(s))
				.toArray(Account[]::new));
	}

	public static List<Account> getAccounts() {
		return accounts;
	}

	/**
	 * Searches the list of accounts for one with the specified account number.
	 * 
	 * @param accountNumber
	 * @return The account, if it was found
	 */
	public static Optional<Account> getAccountByNumber(String accountNumber) {
		return accounts.stream().filter(a -> a.getAccountNumber().equals(accountNumber)).findFirst();
	}

}
