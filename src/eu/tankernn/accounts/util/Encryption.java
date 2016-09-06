package eu.tankernn.accounts.util;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
	
	private static final String CIPHER_TYPE = "AES/GCM/PKCS5Padding";
	private static final String STRING_SEPARATOR = "#";
	
	/**
	 * Decrypts an encrypted string of data.
	 * 
	 * @param dataString
	 *            A hex-coded string with the format 'SALT#IV#DATA'
	 * @param password
	 *            The password used when the string was encoded
	 * @return The decrypted string
	 */
	public static String decrypt(String dataString, String password) {
		String[] splitted = dataString.split(STRING_SEPARATOR);
		
		byte[] salt = hexToBytes(splitted[0]);
		byte[] iv = hexToBytes(splitted[1]);
		byte[] data = hexToBytes(splitted[2]);

		return decrypt(salt, iv, data, password);
	}

	private static String decrypt(byte[] salt, byte[] iv, byte[] data, String key) {
		try {
			SecretKey secret = composeKey(salt, key.toCharArray());
			Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
			
			GCMParameterSpec params = new GCMParameterSpec(128, iv);
			cipher.init(Cipher.DECRYPT_MODE, secret, params);
			String plainText = new String(cipher.doFinal(data), "UTF-8");
			return plainText;
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | InvalidKeySpecException | UnsupportedEncodingException
				| InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Encrypts the data using a random salt.
	 * 
	 * @param data
	 *            The data to encrypt.
	 * @param password
	 *            The password to use when decrypting the data.
	 * @return All information needed to get the original data back, except the
	 *         password, compiled into a single string.
	 */
	public static String encrypt(String data, String password) {
		 byte[][] encryptedComplex = encrypt(new SecureRandom().generateSeed(8), data, password);
		
		String saltString = bytesToHex(encryptedComplex[0]);
		String ivString = bytesToHex(encryptedComplex[1]);
		String dataString = bytesToHex(encryptedComplex[2]);
		
		return String.join(STRING_SEPARATOR, saltString, ivString, dataString);
	}
	
	/**
	 * Encrypts the data using the key made from the salt and password.
	 * @param salt
	 * @param data
	 * @param password
	 * @return A two-dimensional byte array where [0] is the salt bytes, [1] is the IV bytes and [2] is the encrypted data bytes.
	 */
	private static byte[][] encrypt(byte[] salt, String data, String password) {
		try {
			SecretKey secret = composeKey(salt, password.toCharArray());
			Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			AlgorithmParameters params = cipher.getParameters();
			byte[] iv = params.getParameterSpec(GCMParameterSpec.class).getIV();
			byte[] cipherText = cipher.doFinal(data.getBytes());
			
			return new byte[][] {salt, iv, cipherText};
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | InvalidKeySpecException | InvalidParameterSpecException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static SecretKey composeKey(byte[] salt, char[] password)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		return secret;
	}
	
	/**
	 * Testing method for encryption functionality.
	 * @param args
	 */
	public static void main(String[] args) {
		String encrypted = encrypt("asd", "password");
		System.out.println(encrypted);
		String decrypted = decrypt(encrypted, "password");
		System.out.println(decrypted);
	}

	// Conversion methods, fetched from stackoverflow.com

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] hexToBytes(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
