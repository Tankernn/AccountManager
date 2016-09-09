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
import eu.tankernn.accounts.frame.PasswordDialog;
import eu.tankernn.accounts.util.Encryption;
import eu.tankernn.accounts.util.InvalidPasswordException;

public class AccountManager {
	public static final String CURRENCY = "SEK";

	private static String lastJSONString = "[]";
	private static char[] lastPassword;
	private static boolean saveWithEncryption = true;

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
		FileManager.FILE_CHOOSER.showOpenDialog(null);
		openFile(FileManager.FILE_CHOOSER.getSelectedFile());
	}

	/**
	 * Loads the accounts in the file specified.
	 * 
	 * @param file
	 *            The file to load
	 */
	public static void openFile(File file) {
		// Open plain text file
		try {
			String jsonString = FileManager.readFileAsString(file);
			//If '[' is first, the JSON is *probably* valid
			if (jsonString.startsWith("[")) {
				accounts = parseJSON(jsonString);
				lastJSONString = jsonString;
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		// Open encrypted file
		byte[][] data = null; 
		try {
			data = FileManager.readObjectFromFile(file, byte[][].class);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
		String jsonString = new String();
		do {
			try {
				char[] password = PasswordDialog
						.showPasswordDialog("The data is encrypted, please enter the password to decrypt it.");
				if (password == null) {
					// Start the program without loading a file
					return;
				}
				jsonString = Encryption.decrypt(data, password);
				lastPassword = password;
				break;
			} catch (InvalidPasswordException e) {
				continue;
			}
		} while (jsonString.toCharArray()[0] != '[');
		accounts = parseJSON(jsonString);
		lastJSONString = jsonString;
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
			byte[][] encryptedData = null;
			if (saveWithEncryption) {
				while (lastPassword == null || lastPassword.length < 5) {
					lastPassword = PasswordDialog
							.showPasswordDialog("Select a password to encrypt the account file with. (At least 5 characters, preferrably longer)");
				}
				encryptedData = Encryption.encrypt(newData, lastPassword);
			}
			if (encryptedData == null)
				FileManager.writeStringToFile(saveAs, newData);
			else
				FileManager.writeObjectToFile(saveAs, encryptedData);
			lastJSONString = newData;
		} catch (ClassNotFoundException | IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Asks the user to save the current file if any changes have been made.
	 * 
	 * @return <code>false</code> if the user clicked cancel. <code>true</code>
	 *         otherwise.
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

	/**
	 * Searches the list of accounts for ones matching the search string by name
	 * or account number.
	 * 
	 * @param s
	 *            The search string
	 * @return The list of matching accounts
	 */
	public static List<Account> search(String s) {
		return Arrays.asList(accounts.stream().filter(a -> a.getAccountNumber().toLowerCase().contains(s.toLowerCase())
				|| a.toString().toLowerCase().contains(s.toLowerCase())).toArray(Account[]::new));
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

	public static boolean isSavingWithEncryption() {
		return saveWithEncryption;
	}

	public static void setSaveWithEncryption(boolean saveWithEncryption) {
		AccountManager.saveWithEncryption = saveWithEncryption;
	}

}
