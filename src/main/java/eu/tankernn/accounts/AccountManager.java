package eu.tankernn.accounts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.tankernn.accounts.frame.PasswordDialog;
import eu.tankernn.accounts.util.encryption.InvalidPasswordException;

public class AccountManager {
	public static final String CURRENCY = "SEK";
	public static final Gson GSON = new Gson();

	private static String lastJSONString = "[]";
	private static boolean saveWithEncryption = true;
	private static char[] lastPassword;

	private static List<Account> accounts = new ArrayList<Account>();

	/**
	 * Called when account list changes.
	 */
	private static Runnable refresh;

	/**
	 * Initializes the account list using the last file opened, if available.
	 * Otherwise creates an empty list.
	 * 
	 * @param refresh
	 *            A runnable that gets called when the account list changes.
	 */
	public static void init(Runnable refresh, boolean openLast) {
		AccountManager.refresh = refresh;
		if (openLast)
			openFile();
		else
			newFile();
	}

	/**
	 * Loads the accounts in the file specified.
	 */
	public static void openFile() {
		String jsonString;
		lastPassword = null;
		while (true)
			try {
				jsonString = FileManager.openFile(lastPassword);
				lastPassword = PasswordDialog
						.showPasswordDialog("The data is encrypted, please enter the password to decrypt it.");
				break;
			} catch (InvalidPasswordException e) {
				continue;
			} catch (IOException e) {
				newFile();
				return;
			}

		accounts = parseJSON(jsonString);
		lastJSONString = jsonString;
		refresh.run();
	}

	/**
	 * Clears the account list and creates a new file to write the new accounts
	 * to.
	 */
	public static void newFile() {
		if (!closeFile())
			return;

		FileManager.writeLastFileToCache(null);

		lastPassword = null;
		accounts.clear();
		lastJSONString = exportJSON();
		refresh.run();
	}

	/**
	 * Saves the current list of accounts to a file.
	 * 
	 * @param saveAs
	 *            Determines whether the user should be prompted to specify a
	 *            new filename, or if the last filename should be used.
	 */
	public static void saveFile(boolean saveAs) {
		String data = exportJSON();
		if (saveWithEncryption) {
			while (lastPassword == null || lastPassword.length < 5) {
				try {
					lastPassword = PasswordDialog.showPasswordDialog(
							"Select a password to encrypt the account file with. (At least 5 characters, preferrably longer)");
				} catch (CancellationException ex) {
					return;
				}
			}
			FileManager.saveFile(data, saveAs, lastPassword);
		} else {
			FileManager.saveFile(data, saveAs);
		}
		lastJSONString = data;
	}

	/**
	 * Asks the user to save the current file if any changes have been made.
	 * 
	 * @return <code>false</code> if the user clicked cancel. <code>true</code>
	 *         otherwise.
	 */
	public static boolean closeFile() {
		if (AccountManager.hasUnsavedChanges()) {
			int option = JOptionPane.showOptionDialog(null, "Would you like to save changes before exit?",
					"Save changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 0);

			switch (option) {
			case JOptionPane.YES_OPTION:
				saveFile(false);
				break;
			case JOptionPane.NO_OPTION:
				break;
			default:
				return false;
			}
		}
		accounts.clear();
		refresh.run();
		return true;
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
		refresh.run();
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
		return accounts.stream().filter(a -> a.getAccountNumber().toLowerCase().contains(s.toLowerCase())
				|| a.toString().toLowerCase().contains(s.toLowerCase())).collect(Collectors.toList());

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
