package eu.tankernn.accounts;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

public class CachedFileChooser {
	public static final JFileChooser FILE_CHOOSER = new JFileChooser("data/");

	private static final File lastFilenameCache = new File(System.getProperty("user.home") + File.separator + "accountmanager" + File.separator + "lastFile.txt");

	public static File selectFile(boolean saveAs) {
		if (getLastFileFromCache() == null)
			saveAs = true;
		if (saveAs)
			FILE_CHOOSER.showSaveDialog(null);
		return saveAs ? FILE_CHOOSER.getSelectedFile() : getLastFileFromCache();
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
			String lastFilePath = FileManager.readFileAsString(lastFilenameCache);
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
			FileManager.saveFile(lastFilenameCache, file.getAbsolutePath());
			FILE_CHOOSER.setSelectedFile(file);
		}
	}
	
	static void clearCache() {
		writeLastFileToCache(null);
	}
}
