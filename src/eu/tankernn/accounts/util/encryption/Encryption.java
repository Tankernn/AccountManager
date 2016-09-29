package eu.tankernn.accounts.util.encryption;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Encryption {
	
	private static final String CIPHER_TYPE = "AES/GCM/PKCS5Padding";
	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(EncryptedComplex.class, new EncryptedComplexSerializer()).setPrettyPrinting().create();

	public static String decryptEncoded(String dataString, char[] password) throws InvalidPasswordException {
		EncryptedComplex ec = GSON.fromJson(dataString, EncryptedComplex.class);
		return decrypt(ec, password);
	}

	/**
	 * Decrypts an encrypted string of data.
	 * 
	 * @param ec
	 *            An object containing all the data needed to restore the
	 *            original
	 * @param password
	 *            The password used when the string was encrypted
	 * @return The decrypted string
	 * @throws InvalidPasswordException
	 *             if the password is incorrect
	 */
	public static String decrypt(EncryptedComplex ec, char[] password) throws InvalidPasswordException {
		try {
			SecretKey secret = composeKey(ec.getSalt(), password);
			Cipher cipher = Cipher.getInstance(CIPHER_TYPE);

			GCMParameterSpec params = new GCMParameterSpec(128, ec.getIV());
			cipher.init(Cipher.DECRYPT_MODE, secret, params);
			String plainText = new String(cipher.doFinal(ec.getData()), "UTF-8");
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
		EncryptedComplex ec = encrypt(data, password);
		return GSON.toJson(ec);
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
	public static EncryptedComplex encrypt(String data, char[] password) {
		return encrypt(new SecureRandom().generateSeed(8), data, password);
	}

	/**
	 * Encrypts the data using the key made from the salt and password.
	 * 
	 * @param salt
	 * @param data
	 * @param password
	 * @return An object containing all information, except the password, needed
	 *         to restore the original data.
	 */
	private static EncryptedComplex encrypt(byte[] salt, String data, char[] password) {
		try {
			SecretKey secret = composeKey(salt, password);
			Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			AlgorithmParameters params = cipher.getParameters();
			byte[] iv = params.getParameterSpec(GCMParameterSpec.class).getIV();
			byte[] cipherText = cipher.doFinal(data.getBytes());

			return new EncryptedComplex(salt, iv, cipherText);
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
		EncryptedComplex encrypted = encrypt("asd", "password".toCharArray());
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
