package hu.fenyesb.hf;

import java.security.Key;
import java.util.Base64;
import java.util.prefs.Preferences;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * A fiókinformációkat tároló osztály. Tartalmazza az email címet, a titkosított
 * jelszót és a szerverbeállításokat.
 * 
 * @author bal
 *
 */
public class AccountInfo {

	/**
	 * Visszaadja az email címet.
	 */
	@Override
	public String toString() {
		return EmailAddress;
	}

	public String EmailAddress, EncryptedPassword, ImapServer, SmtpServer;
	public int ImapPort, SmtpPort;
	private static final String PREFENCES_ID = "emailclient.key";

	/**
	 * A titkosításhoz használt AES kulcs lekérdezése a Preferences-ből. Ha még
	 * nincs ilyen, akkor generál egyet és eltárolja.
	 * 
	 * @return a titkosítási kulcs
	 */
	private static Key GetAESKey() throws Exception {
		
		Preferences preferences = Preferences.userRoot();
		byte[] prefkey = preferences.getByteArray(PREFENCES_ID, null);
		
		if (prefkey == null) {
			
			KeyGenerator gen = KeyGenerator.getInstance("AES");
			gen.init(128); // 128-bites AES
			SecretKey secret = gen.generateKey();
			preferences.putByteArray(PREFENCES_ID, secret.getEncoded());
			prefkey = preferences.getByteArray(PREFENCES_ID, null);
			if (prefkey == null)
				throw new RuntimeException("Preferences error!");
			
		}
		return new SecretKeySpec(prefkey, "AES");
	}

	/**
	 * Titkosítja a jelszót AES segítségével.
	 * 
	 * @param password
	 *            a plaintext jelszó
	 * @return a titkosított jelszó
	 */
	public static String EncryptPassword(String password) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, GetAESKey());
			byte[] encrypted = cipher.doFinal(password.getBytes());
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			return "<error>";
		}
	}

	/**
	 * Visszafejti a jelszót AES segítségével.
	 * 
	 * @param encryptedpwbase64
	 *            a titkosított jelszó
	 * @return a plaintext jelszó
	 */
	public static String DecryptPassword(String encryptedpwbase64) {
		try {
			Cipher cipher = Cipher.getInstance("AES");

			cipher.init(Cipher.DECRYPT_MODE, GetAESKey());
			byte[] encrypted = Base64.getDecoder().decode(encryptedpwbase64);
			return new String(cipher.doFinal(encrypted));
		} catch (Exception e) {
			e.printStackTrace();
			return "<error>";
		}
	}
}
