package eu.tankernn.accounts.util.encryption;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class EncryptedComplex {
	private byte[] salt, iv, data;

	public EncryptedComplex(byte[] salt, byte[] iv, byte[] data) {
		this.salt = salt;
		this.iv = iv;
		this.data = data;
	}
	
	/**
	 * Convert the legacy format into the new one.
	 * @param b The old encrypted complex format.
	 */
	public EncryptedComplex(byte[][] b) {
		this(b[0], b[1], b[2]);
	}

	public EncryptedComplex(String salt, String iv, String data) {
		this.salt = decode(salt);
		this.iv = decode(iv);
		this.data = decode(data);
	}

	private String encode(byte[] b) {
		// Easily switch to other encoding types
		return Base64.encodeBase64String(b);
	}

	private byte[] decode(String str) {
		try {
			return Hex.decodeHex(str.toCharArray());
		} catch (DecoderException e) {
			return Base64.decodeBase64(str);
		}
	}

	public byte[] getSalt() {
		return salt;
	}

	public byte[] getIV() {
		return iv;
	}

	public byte[] getData() {
		return data;
	}

	public String getEncodedSalt() {
		return encode(salt);
	}

	public String getEncodedIv() {
		return encode(iv);
	}

	public String getEncodedData() {
		return encode(data);
	}
}
