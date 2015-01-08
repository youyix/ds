package client.encryption;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
	private final static Encryption encryption = new Encryption();
	private byte[] enCodeFormat;
	
	private Encryption() {
		enCodeFormat = null;
	}
	
	public static Encryption getInstance() {
		return encryption;
	}
	
	public void importKey(byte[] enCodeFormat) {
		this.enCodeFormat = enCodeFormat;
	}
			
	
	public byte[] encrypt(byte [] content) {
	
		try {
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");  
			Cipher cipher = Cipher.getInstance("AES");  
			byte[] byteContent = content;  
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] result = cipher.doFinal(byteContent);  
			return result;
		} catch (Exception e) {
			System.err.println("Cannot encrypt file.");
			e.printStackTrace();
			return content;
		}
		
	}
	
	public byte[] decrypt(byte [] content) {
		
		try {
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");              
			Cipher cipher = Cipher.getInstance("AES");  
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] result = cipher.doFinal(content);  
			return result; 
		} catch(Exception e) {
			System.err.println("Cannot decrypt file.");
			e.printStackTrace();
			return content;
		}
	}
	
	public static void main(String args[]) throws Exception {
//		Encryption e = new Encryption();
//		byte[] bbb = {0, 0, 1, 0};
//		
//		byte[] en = e.encrypt(bbb);
//		System.out.println(Arrays.toString(en));
//		byte[] de = e.decrypt(en);
//		System.out.println(Arrays.toString(de));
		
		Encryption e = Encryption.getInstance();
		byte[] bbb = new String("sdaadsadsdadasddsd").getBytes();
		byte[] en = e.encrypt(bbb);
		System.out.println(new String(en));
		byte[] de = e.decrypt(en);
		System.out.println(new String(de));
	}
}
