package eu.tankernn.accounts.test;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import eu.tankernn.accounts.FileManager;
import eu.tankernn.accounts.util.encryption.InvalidPasswordException;

public class FileManagerTest {
	String testData = "[VeryNice2123..----__...'+<<><";
	File testFile = new File("test.txt");
	
	@Test
	public void testPlainReadWrite() {
		try {
			FileManager.saveFile(testFile, testData);
			String read = FileManager.openFile(testFile, null);
			Assert.assertEquals(testData, read);
		} catch (IOException | InvalidPasswordException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testEncryptedReadWrite() {
		char[] pass = "VerySafeANDSoundPassWord123<><<--..,,".toCharArray();
		try {
			FileManager.saveFile(testFile, testData, pass);
			String read = FileManager.openFile(testFile, pass);
			Assert.assertEquals(testData, read);
		} catch (IOException | InvalidPasswordException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
