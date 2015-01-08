package clients;

import java.nio.file.Paths;

import client.encryption.KeyOperator;
import client.kkk.InteractiveControl;
import client.kkk.InteractiveControlM;

/**
 * 
 * @author Zhenfei Nie<zhen.fei.nie@usi.ch>
 * @author Xiyu Lyu<xi.yu.lyu@usi.ch>
 *
 */
public class Main {
	public static void main(String args[]) {
		if ( args.length == 0 ) {
			System.out.println(usage());
			System.exit(0);
		}
		
		parseOption(args, 0);
	}
	
	public static void parseOption(String args[], int h) {
		if ( args[h].equals("-import") ) {
			String keyfile = args[h + 1];
			KeyOperator.importKey(Paths.get(keyfile));
			System.out.println("Key file imported.");
			mode1(args, h+2);
		} else if (args[h].equals("-export") ) {
			String password = args[h+1];
			KeyOperator.exportKey(password);
			System.out.println("Key file exported.");
			System.exit(0);
		} else if (args[h].equals("-mode2") ) {
			mode2(args, h+1);
		} else {
			System.out.println(usage());
		}
	}
	
	public static void mode1(String args[], int hop) {
		System.out.println("### Running at mode 1(with encryption). ###");
		String host = "";
		String remoteDir = "";
		String localDir = "";
		try {
			host = args[0 + hop];
			remoteDir = args[1 + hop];
			localDir = args[2 + hop];
		} catch(Exception e) {
			System.out.println(usage());
			System.exit(0);
//			host = "192.168.0.12";
//			remoteDir = "/Users/cici/nfss";
//			localDir = "/Users/niezhenfei/kkk";
		}
		
		InteractiveControl ic = new InteractiveControl(host, remoteDir, Paths.get(localDir));
		ic.run();
	}
	
	public static void mode2(String args[], int hop) {
		System.out.println("### Running at mode 2(Two nfs servers, without encryption). ###");
		String host = "";
		String host2 = "";
		String remoteDir = "";
		String remoteDir2 = "";
		String localDir = "";
		try {
			host = args[0 + hop];
			remoteDir = args[1 + hop];
			host2 = args[2 + hop];
			remoteDir2 = args[3 + hop];
			localDir = args[4 + hop];
		} catch(Exception e) {
			System.out.println(usage());
			System.exit(0);
//			host = "192.168.0.12";
//			host2 = "192.168.0.15";
//			remoteDir = "/Users/cici/nfss";
//			remoteDir2 = "/Users/niezhenfei/nfss";
//			localDir = "/Users/niezhenfei/kkkk";
		}
		
		InteractiveControlM ic = new InteractiveControlM(host, host2, remoteDir, remoteDir2, Paths.get(localDir));
		ic.run();
		
	}
	
	public static String usage() {
		String str = "Usage: java -cp .:../lib/oncrpc.jar clients.Main options parameters\n";
		String option = "Option:\n  -import keyfilepath -- import key file\n";
		String option2 = "  -export password -- export keyfile\n";
	
		str += option;
		str += option2;
		str += "  Paramters: \n";
		str += "    server_ip   shared_folder_path   loca_folder_path\n\n";
		
		String option3 = "Option2:\n  -mode2 -- use two nfs server\n";
		option3 += "  Paramters: \n";
		option3 += "    #1server_ip   #1shared_folder_path   #2server_ip   #2shared_folder_path   loca_folder_path\n\n";
		
		str += option3;
		return str;
	}

}
