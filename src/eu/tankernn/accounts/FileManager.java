package eu.tankernn.accounts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FileManager {
	public static final JFileChooser FILE_CHOOSER = new JFileChooser("data/");

	private static final File lastFilenameCache = new File(
			System.getProperty("user.home") + File.separator + "accountmanager" + File.separator + "lastFile.txt");

	public static File getLastFileFromCache() {
		// Open last file
		try {
			// Create file to cache last filename
			if (!lastFilenameCache.exists()) {
				lastFilenameCache.getParentFile().mkdirs();
				lastFilenameCache.createNewFile();
				return null;
			}
			String lastFilePath = FileManager.readFileAsString(lastFilenameCache);
			return new File(lastFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String readFileAsString(File file) throws IOException {
		if (file == null) {
			FILE_CHOOSER.showOpenDialog(null);
			file = FILE_CHOOSER.getSelectedFile();
		}

		// Remember this filename
		if (!file.equals(lastFilenameCache))
			FileManager.writeStringToFile(lastFilenameCache, file.getAbsolutePath());

		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder builder = new StringBuilder();

		while (reader.ready()) {
			builder.append(reader.readLine());
		}
		reader.close();

		return builder.toString();
	}

	/**
	 * Creates a new file containing an empty JSON array.
	 * 
	 * @return The <code>File</code> object that represents the new file.
	 * @throws IOException
	 */
	public static File newEmptyJSONFile() throws IOException {
		int result = FILE_CHOOSER.showDialog(null, "Create file");
		if (result != JFileChooser.APPROVE_OPTION)
			return null;

		File newFile = FILE_CHOOSER.getSelectedFile();

		if (newFile.exists()) {
			JOptionPane.showMessageDialog(null, "That file already exists.");
			return null;
		}

		newFile.createNewFile();

		FileManager.writeStringToFile(lastFilenameCache, newFile.getAbsolutePath());
		writeStringToFile(newFile, "[]");

		return newFile;
	}

	public static void writeStringToFile(boolean saveAs, String contents) throws FileNotFoundException {
		if (saveAs)
			FILE_CHOOSER.showSaveDialog(null);
		writeStringToFile(saveAs ? FILE_CHOOSER.getSelectedFile() : getLastFileFromCache(), contents);
	}

	public static void writeStringToFile(File file, String contents) throws FileNotFoundException {
		writeBytesToFile(file, contents.getBytes());
	}

	public static void writeBytesToFile(File file, byte[] data) throws FileNotFoundException {
		if (file == null) {
			FILE_CHOOSER.showSaveDialog(null);
			file = FILE_CHOOSER.getSelectedFile();
		}

		FileOutputStream writer = new FileOutputStream(file);

		try {
			writer.write(data, 0, data.length);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static File latestFile() {
		return FILE_CHOOSER.getSelectedFile();
	}

	public static <T> T readObjectFromFile(File file, Class<T> class1) throws IOException, ClassNotFoundException {
		if (file == null) {
			FILE_CHOOSER.showOpenDialog(null);
			file = FILE_CHOOSER.getSelectedFile();
		}
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		T obj = class1.cast(in.readObject());
		in.close();
		return obj;
	}

	public static <T> void writeObjectToFile(boolean saveAs, T contents) throws ClassNotFoundException, IOException {
		if (saveAs)
			FILE_CHOOSER.showSaveDialog(null);
		writeObjectToFile(saveAs ? FILE_CHOOSER.getSelectedFile() : getLastFileFromCache(), contents);
	}

	public static <T> void writeObjectToFile(File file, T obj) throws IOException, ClassNotFoundException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
		out.writeObject(obj);
		out.close();
	}
}
