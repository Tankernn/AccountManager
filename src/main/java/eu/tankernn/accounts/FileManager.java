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

import javax.swing.JOptionPane;

import eu.tankernn.accounts.util.encryption.EncryptedComplex;
import eu.tankernn.accounts.util.encryption.Encryption;
import eu.tankernn.accounts.util.encryption.InvalidPasswordException;

public class FileManager {
	

	/**
	 * Reads and decrypts the JSON-string in the file specified.
	 * 
	 * @return A JSON-string containing the account information.
	 * @throws InvalidPasswordException 
	 */
	public static String openFile(File file, char[] password) throws IOException, InvalidPasswordException {
		if (file == null)
			throw new FileNotFoundException();
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

	public static void saveFile(File file, String data, char[] password) {
		saveFile(file, Encryption.encryptEncoded(data, password));
	}

	static String readFileAsString(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder builder = new StringBuilder();

		while (reader.ready()) {
			builder.append(reader.readLine());
		}
		reader.close();

		return builder.toString();
	}
	
	/**
	 * Writes a string to a file.
	 */
	public static void saveFile(File file, String contents) {
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
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		T obj = class1.cast(in.readObject());
		in.close();
		return obj;
	}
}
