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
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class Encryption {

	private static final String CIPHER_TYPE = "AES/GCM/PKCS5Padding";
	public static final String STRING_SEPARATOR = "#";

	public static String decryptEncoded(String dataString, char[] password) throws InvalidPasswordException {
		String[] splitted = dataString.split(STRING_SEPARATOR);

		byte[][] encryptedComplex = (byte[][]) Arrays.asList(splitted).stream().map(s -> {
			try {
				return Hex.decodeHex(s.toCharArray());
			} catch (DecoderException e) {
				return Base64.decodeBase64(s);
			}
		}).toArray();

		return decrypt(encryptedComplex, password);
	}

	/**
	 * Decrypts an encrypted string of data.
	 * 
	 * @param data
	 *            A Base64-encoded string with the format 'SALT#IV#DATA'
	 * @param password
	 *            The password used when the string was encrypted
	 * @return The decrypted string
	 * @throws InvalidPasswordException
	 *             if the password is incorrect
	 */
	public static String decrypt(byte[][] data, char[] password) throws InvalidPasswordException {
		return decrypt(data[0], data[1], data[2], password);
	}

	private static String decrypt(byte[] salt, byte[] iv, byte[] data, char[] password)
			throws InvalidPasswordException {
		try {
			SecretKey secret = composeKey(salt, password);
			Cipher cipher = Cipher.getInstance(CIPHER_TYPE);

			GCMParameterSpec params = new GCMParameterSpec(128, iv);
			cipher.init(Cipher.DECRYPT_MODE, secret, params);
			String plainText = new String(cipher.doFinal(data), "UTF-8");
			return plainText;
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| InvalidKeySpecException | UnsupportedEncodingException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return null;
		} catch (BadPaddingException e) {
			throw new InvalidPasswordException();
		}
	}

	public static String encryptEncoded(String data, char[] password) {
		byte[][] encryptedComplex = encrypt(data, password);

		String saltString = Base64.encodeBase64String(encryptedComplex[0]);
		String ivString = Base64.encodeBase64String(encryptedComplex[1]);
		String dataString = Base64.encodeBase64String(encryptedComplex[2]);

		return String.join(STRING_SEPARATOR, saltString, ivString, dataString);
	}

	/**
	 * Encrypts the data using a random salt.
	 * 
	 * @param data
	 *            The data to encrypt.
	 * @param password
	 *            The password to use when decrypting the data.
	 * @return All information needed to get the original data back, except the
	 *         password.
	 */
	public static byte[][] encrypt(String data, char[] password) {
		return encrypt(new SecureRandom().generateSeed(8), data, password);
	}

	/**
	 * Encrypts the data using the key made from the salt and password.
	 * 
	 * @param salt
	 * @param data
	 * @param password
	 * @return A two-dimensional byte array where [0] is the salt bytes, [1] is
	 *         the IV bytes and [2] is the encrypted data bytes.
	 */
	private static byte[][] encrypt(byte[] salt, String data, char[] password) {
		try {
			SecretKey secret = composeKey(salt, password);
			Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			AlgorithmParameters params = cipher.getParameters();
			byte[] iv = params.getParameterSpec(GCMParameterSpec.class).getIV();
			byte[] cipherText = cipher.doFinal(data.getBytes());

			return new byte[][] { salt, iv, cipherText };
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
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		byte[][] encrypted = encrypt("asd", "password".toCharArray());
		System.out.println(encrypted);
		String decrypted = "";
		try {
			decrypted = decrypt(encrypted, "password".toCharArray());
		} catch (InvalidPasswordException e) {
			System.out.println("Wrong password");
		}
		System.out.println(decrypted);
	}
}
