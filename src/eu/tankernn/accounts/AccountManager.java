package eu.tankernn.accounts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.tankernn.accounts.frame.MainFrame;
import eu.tankernn.accounts.frame.PasswordDialog;
import eu.tankernn.accounts.util.encryption.EncryptedComplex;
import eu.tankernn.accounts.util.encryption.Encryption;
import eu.tankernn.accounts.util.encryption.InvalidPasswordException;

public class AccountManager {
	public static final String CURRENCY = "SEK";
	public static final Gson GSON = new Gson();

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
		Object data = null;
		try {
			try {
				// Try to read the file as a byte[][]
				data = FileManager.readObjectFromFile(file, byte[][].class);
			} catch (ObjectStreamException | ClassNotFoundException e1) {
				// Read the file as string
				data = FileManager.readFileAsString(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		String jsonString = new String();
		// If '[' is first, the JSON is *probably* valid
		if (data instanceof String && ((String) data).startsWith("[")) {
			lastPassword = null;
			jsonString = new String((String) data);
		} else {
			// Try to decrypt the string or byte[][]
			do {
				try {
					char[] password = PasswordDialog
							.showPasswordDialog("The data is encrypted, please enter the password to decrypt it.");
					jsonString = data instanceof String ? Encryption.decryptEncoded((String) data, password)
							: Encryption.decrypt(new EncryptedComplex((byte[][]) data), password);
					lastPassword = password;
					break;
				} catch (CancellationException ex) {
					// Start the program without loading a file
					return;
				} catch (InvalidPasswordException e) {
					continue;
				}
			} while (jsonString.toCharArray()[0] != '[');
		}

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
			String encryptedData = new String(newData);
			if (saveWithEncryption) {
				while (lastPassword == null || lastPassword.length < 5) {
					try {
						lastPassword = PasswordDialog.showPasswordDialog(
								"Select a password to encrypt the account file with. (At least 5 characters, preferrably longer)");
					} catch (CancellationException ex) {
						return;
					}

				}
				encryptedData = Encryption.encryptEncoded(newData, lastPassword);
			}
			FileManager.writeStringToFile(saveAs, encryptedData);
			lastJSONString = newData;
		} catch (IOException e1) {
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

	private static List<Account> parseJSON(String jsonString) {
		return GSON.fromJson(jsonString, new TypeToken<ArrayList<Account>>() {
		}.getType());
	}

	/**
	 * Encodes the current list of accounts into JSON.
	 * 
	 * @return The string containing the JSON-encoded string representing the
	 *         current list of accounts.
	 */
	public static String exportJSON() {
		return GSON.toJson(accounts);
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
