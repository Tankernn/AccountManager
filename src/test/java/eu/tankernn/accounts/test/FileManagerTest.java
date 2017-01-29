package eu.tankernn.accounts.test;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import eu.tankernn.accounts.FileManager;
import eu.tankernn.accounts.util.encryption.InvalidPasswordException;

public class FileManagerTest {
	@Test
	public void readingStringShouldReturnEqualString() {
		String testData = "VeryNice2123..----__...'¨´+<<><";
		
		File testFile = new File("test.txt");
		
		try {
			FileManager.saveFile(testFile, testData);
			String read = FileManager.openFile(testFile, null);
			Assert.assertEquals(testData, read);
		} catch (IOException | InvalidPasswordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
