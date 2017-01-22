package eu.tankernn.accounts.util.encryption.test;

import org.junit.Assert;
import org.junit.Test;

import eu.tankernn.accounts.util.encryption.Encryption;
import eu.tankernn.accounts.util.encryption.InvalidPasswordException;

public class EncryptionTest {
	@Test
	public void encryptDecrypt() {
		char[] password = "Super safe and secret password.".toCharArray();
		String original = "Very secret string with some more characters added to it just for the sake of it.";
		String encrypted = null;
		try {
			encrypted = Encryption.decryptEncoded(Encryption.encryptEncoded(original, password), password);
		} catch (InvalidPasswordException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(original, encrypted);
	}
}
