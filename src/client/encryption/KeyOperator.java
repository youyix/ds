package client.encryption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


public class KeyOperator {
	private static SecretKey secretKey;
	
	private static SecretKey createKey(String password) {
		KeyGenerator kgen = null;
		try {
			kgen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(-1);
		}  
		kgen.init(128, new SecureRandom(password.getBytes()));  
        secretKey = kgen.generateKey();  
		return secretKey;
	}
	
	public static void exportKey(String password) {
		createKey(password);
		
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(new File("secretKey.nfskey"));
			output.write(secretKey.getEncoded());
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static byte[] importKey(Path p) {
		byte[] key = null;
		try {
			key = Files.readAllBytes(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Encryption.getInstance().importKey(key);
		return key;
	}

	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.out.println("please give a password");
		}
		String password = args[0];
		KeyOperator.exportKey(password);
	}

}
