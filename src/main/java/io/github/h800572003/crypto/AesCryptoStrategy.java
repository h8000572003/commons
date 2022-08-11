package io.github.h800572003.crypto;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 加密方式
 * 
 * @author Andy
 *
 */
public class AesCryptoStrategy implements ICryptoStrategy {

	private static final String AES = "AES";

	private final String salt;
	private final String charsetName;

	public AesCryptoStrategy(String salt, String charsetName) {
		super();
		this.salt = Objects.requireNonNull(salt);
		this.charsetName = Objects.requireNonNull(charsetName);

		if (this.salt.length() != 16) {
			throw new CryptoExcepton("SALT's lenght must be 16.");
		}
	}

	public AesCryptoStrategy(String salt) {
		this(salt, Charset.defaultCharset().name());
	}

	@Override
	public String toEnCode(String orgValue) {

		try {
			byte[] raw = salt.getBytes(charsetName);
			SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
			Cipher cipher = Cipher.getInstance(AES);// "演算法/模式/補碼方式"
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(orgValue.getBytes(charsetName));
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception e) {
			throw new CryptoExcepton("toEnCode fails:" + orgValue, e);
		}

	}

	@Override
	public String toDeCode(String enCode) {
		try {
			byte[] raw = salt.getBytes(charsetName);
			SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
			Cipher cipher = Cipher.getInstance(AES);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted1 = Base64.getDecoder().decode(enCode);// 先用base64解密
			byte[] original = cipher.doFinal(encrypted1);
			return new String(original, charsetName);
		} catch (Exception e) {
			throw new CryptoExcepton("toDeCode fails:" + enCode, e);
		}
	}

}
