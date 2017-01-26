package eu.tankernn.accounts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import eu.tankernn.accounts.util.encryption.EncryptedComplex;
import eu.tankernn.accounts.util.encryption.Encryption;
import eu.tankernn.accounts.util.encryption.InvalidPasswordException;

public class FileManager {
	public static final JFileChooser FILE_CHOOSER = new JFileChooser("data/");

	private static final File lastFilenameCache = new File(
			System.getProperty("user.home") + File.separator + "accountmanager" + File.separator + "lastFile.txt");

	private static File selectFile(boolean saveAs) {
		if (getLastFileFromCache() == null)
			saveAs = true;
		if (saveAs)
			FILE_CHOOSER.showSaveDialog(null);
		return saveAs ? FILE_CHOOSER.getSelectedFile() : getLastFileFromCache();
	}

	/**
	 * Loads the accounts in the file specified.
	 * 
	 * @return A JSON-string containing the account information.
	 * @throws InvalidPasswordException 
	 */
	public static String openFile(char[] password) throws IOException, InvalidPasswordException {
		File file = getLastFileFromCache();
		Object data = null;
		try {
			// Try to read the file as a byte[][]
			data = readObjectFromFile(file, byte[][].class);
		} catch (ObjectStreamException | ClassNotFoundException e1) {
			// Read the file as string
			data = readFileAsString(file);
		}
		String jsonString;
		// If '[' is first, the JSON is *probably* valid
		if (data instanceof String && ((String) data).startsWith("[")) {
			jsonString = new String((String) data);
		} else {
			if (password == null)
				throw new InvalidPasswordException();
			// Try to decrypt the string or byte[][]
			jsonString = data instanceof String ? Encryption.decryptEncoded((String) data, password)
					: Encryption.decrypt(new EncryptedComplex((byte[][]) data), password);
		}

		return jsonString;
	}

	public static void saveFile(String data, boolean saveAs, char[] password) {
		saveFile(Encryption.encryptEncoded(data, password), saveAs);
	}

	/**
	 * Saves the current list of accounts to a file.
	 * 
	 * @param saveAs
	 *            Determines whether the user should be prompted to specify a
	 *            new filename, or if the last filename should be used.
	 */
	public static void saveFile(String data, boolean saveAs) {
		writeStringToFile(selectFile(saveAs), data);
	}

	private static File getLastFileFromCache() {
		// Open last file
		try {
			// Create file to cache last filename
			if (!lastFilenameCache.exists()) {
				lastFilenameCache.getParentFile().mkdirs();
				lastFilenameCache.createNewFile();
				return null;
			}
			String lastFilePath = readFileAsString(lastFilenameCache);
			File f = new File(lastFilePath);
			FILE_CHOOSER.setSelectedFile(f);
			return f;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	static void writeLastFileToCache(File file) {
		if (file == null)
			lastFilenameCache.delete();
		// Remember this filename
		if (!file.equals(lastFilenameCache)) { // Don't remember the cache file
			writeStringToFile(lastFilenameCache, file.getAbsolutePath());
			FILE_CHOOSER.setSelectedFile(file);
		}

	}

	private static String readFileAsString(File file) throws IOException {
		writeLastFileToCache(file);

		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder builder = new StringBuilder();

		while (reader.ready()) {
			builder.append(reader.readLine());
		}
		reader.close();

		return builder.toString();
	}

	private static void writeStringToFile(File file, String contents) {
		writeBytesToFile(file, contents.getBytes());
	}

	private static void writeBytesToFile(File file, byte[] data) {
		try {
			FileOutputStream writer = new FileOutputStream(file);
			writer.write(data, 0, data.length);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			JOptionPane.showOptionDialog(null, "Unable to create or write to file \'" + file.getAbsolutePath() + "\'.",
					"Error writing file", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
			e.printStackTrace();
		}

	}

	public static <T> T readObjectFromFile(File file, Class<T> class1)
			throws ClassNotFoundException, FileNotFoundException, IOException {
		if (file == null) {
			FILE_CHOOSER.showOpenDialog(null);
			file = FILE_CHOOSER.getSelectedFile();
		}
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		T obj = class1.cast(in.readObject());
		in.close();
		writeLastFileToCache(file);
		return obj;
	}
}
