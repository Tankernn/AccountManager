package eu.tankernn.accounts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JFileChooser;

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
			File f = new File(lastFilePath);
			FILE_CHOOSER.setSelectedFile(f);
			return f;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void writeLastFileToCache(File file) {
		if (file == null)
			lastFilenameCache.delete();
		// Remember this filename
		if (!file.equals(lastFilenameCache)) { // Don't remember the cache file
			try {
				writeStringToFile(lastFilenameCache, file.getAbsolutePath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			FILE_CHOOSER.setSelectedFile(file);
		}
		
	}

	public static String readFileAsString(File file) throws IOException {
		if (file == null) {
			if (FILE_CHOOSER.showOpenDialog(null) == JFileChooser.CANCEL_OPTION)
				throw new FileNotFoundException();
			else
				file = FILE_CHOOSER.getSelectedFile();
		}
		
		System.out.println("Opening file " + file.getAbsolutePath());

		writeLastFileToCache(file);

		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder builder = new StringBuilder();

		while (reader.ready()) {
			builder.append(reader.readLine());
		}
		reader.close();

		return builder.toString();
	}

	public static void writeStringToFile(boolean saveAs, String contents) throws FileNotFoundException {
		if (getLastFileFromCache() == null)
			saveAs = true;
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
