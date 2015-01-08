package client.kkk;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import client.encryption.KeyOperator;
import client.watcher.Watcher;


public class InteractiveControl {
	private NfsDir nfsc;
	private String host;
	private String remoteDir;
	private Path currentPath;
	private Path local;
	private Watcher watcher;
	
	public InteractiveControl(String host, String remoteDir, Path dir) {
		this.host = host;
		this.remoteDir = remoteDir;
		this.local = dir;
		nfsc = new NfsDir(host, remoteDir, dir);
		nfsc.mount();
		currentPath = Paths.get(this.remoteDir);
		
		try {
			watcher = new Watcher(host, remoteDir, dir);
			watcher.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void listFiles(boolean verbose) {
		List<EntryWrapper> ews = nfsc.readDir(currentPath, "");
		if ( ews == null ) {
			System.out.println("Empty dir");
			return;
		}
		if ( ! verbose ) {
			for ( int i=0; i<ews.size(); i++  ) {
				EntryWrapper entry = ews.get(i);
				System.out.printf("%2d [%3s] %20s\n", i, entry.getType(), entry.filename.value );
			}
		} else {
			for ( int i=0; i<ews.size(); i++  ) {
				EntryWrapper entry = ews.get(i);
				if ( entry.attribute != null ) {
					System.out.printf("%2d %20s %s\n", i, entry.filename.value, Utils.FAttr2String(entry.attribute) );
				} else {
					System.out.printf("%2d %20s %s\n", i, entry.filename.value, "" );
				}
				
			}
		}
		
	}
	
	public void open(int i) {
		if ( ! download(i) ) {
			return;
		}
		List<EntryWrapper> ews = nfsc.readDir(currentPath, "");
		EntryWrapper e = ews.get(i);
		String filepath = Utils.convertPath(currentPath, local, e.filename.value).toString();
		String command = "subl " + filepath;
		try {
			Process p = Runtime.getRuntime().exec(command);
		} catch (IOException e1) {
			System.err.println("Cannot open file " + e.filename.value);
			System.err.println("Please have sublime installed and have `subl` short-cut for it. ");
			e1.printStackTrace();
		}
	}
	
	public boolean rename(int i, String toFilename) {
		List<EntryWrapper> ews = nfsc.readDir(currentPath, "");
		EntryWrapper e = ews.get(i);
		return nfsc.rename(currentPath, e.filename.value, toFilename);
	}
	
	public boolean mkDir(String dirname) {
		return nfsc.mkDir(currentPath, dirname);
	}
	
	public boolean createFile(String filename) {
		return nfsc.createFile(currentPath, filename);
	}
	
	
	public boolean download(int i) {
		List<EntryWrapper> ews = nfsc.readDir(currentPath, "");
		EntryWrapper e = ews.get(i);
		return nfsc.readFile(currentPath, e.filename.value, e);
	}
	
	public boolean delete(int i) {
		List<EntryWrapper> ews = nfsc.readDir(currentPath, "");
		EntryWrapper e = ews.get(i);
		return nfsc.deleteFile(currentPath, e.filename.value);
	} 
	
	public void getAttr(int i) {
		List<EntryWrapper> ews = nfsc.readDir(currentPath, "");
		EntryWrapper e = ews.get(i);
		System.out.println(Utils.FAttr2String(e.attribute));
	} 
	
	public boolean cd(int i) {
		List<EntryWrapper> ews = nfsc.readDir(currentPath, "");
		EntryWrapper e = ews.get(i);
		currentPath = currentPath.resolve(e.filename.value);
		return true;
	}
	
	public void run() {
		System.out.println(host + ":" + remoteDir + " has mounted.");
		System.out.println(commands());
		System.out.print("> ");
		Scanner in = new Scanner(System.in);
		String s = "";
		while((s = in.next()) != null) {
			if ( s.equals("ls") ) {
				listFiles(false);
			} else if (s.equals("ll")) {
				listFiles(true);
			} else if (s.equals("mkdir")) {
				try {
					String param = in.next();
					if ( mkDir(param) ) {
						System.out.printf("Dir %s is created.\n", param);
					} else {
						System.out.println("Error to mkdir " + param);
					}
				} catch(Exception e) {
					continue;
				}
			} else if ( s.equals("touch") ) {
				try {
					String param = in.next();
					if ( createFile(param) ) {
						System.out.printf("File %s is created.\n", param);
					} else {
						System.out.println("Error to create new file " + param);
					}
				} catch(Exception e) {
					continue;
				}

			} else if ( s.equals("show") ) {
				try {
					int i = in.nextInt();
					getAttr(i);
				} catch(Exception e) {
					continue;
				}
			} else if ( s.equals("download") ) {
				try {
					int i = in.nextInt();
					if ( download(i) ) {
						System.out.printf("File  #%d has been downloaded.\n", i);
					} else {
						System.out.println("Error to download file #" + i);
					}
				} catch(Exception e) {
					continue;
				}
			} else if (s.equals("rename")) {
				try {
					int i = in.nextInt();
					String to = in.next();
					if ( rename(i, to) ) {
						System.out.printf("File has been renamed to %s.\n", to );
					} else {
						System.out.println("Error to rename file #" + i);
					}
				} catch(Exception e) {
					continue;
				}
			} else if(s.equals("remove")) {
				try {
					int i = in.nextInt();
					if ( delete(i) ) {
						System.out.printf("File #%d has been deleted\n", i );
					} else {
						System.out.println("Error to delete file #" + i);
					}
				} catch(Exception e) {
					continue;
				}
			} else if (s.equals("cd")) {
				try {
					int i = in.nextInt();
					if ( cd(i) ) {
						System.out.printf("now in folder: %s\n", currentPath.toString() );
					} else {
						System.out.println("Error to navigate folder #" + i);
					}
				} catch(Exception e) {
					continue;
				}
			} else if (s.equals("open")) {
				try {
					int i = in.nextInt();
					open(i);
				} catch(Exception e) {
					continue;
				}
			}
 			else if (s.equals("exit")) {
				watcher.terminates();
				System.exit(0);
			}
			else {
				System.out.println("Unkown command " + s);
				in.reset();
			}
			System.out.print("> ");
		}
		
	}
	public String commands() {
		String str = "|-----------------------|\nAvailable Commands\n";
		String cm1 = "01. ls\n";
		String cm2 = "02. ll\n";
		String cm3 = "03. show numberOfEntry\n";
		String cm4 = "04. open numberOfEntry(not dir)\n";
		String cm5 = "05. download numberOfEntry(not dir)\n";
		String cm6 = "06. cd numberOfEntry(dir only)\n";
		String cm7 = "07. mkdir dirname\n";
		String cm8 = "08. touch filename\n";
		String cm9 = "09. rename numberOfEntry newFilename(not dir)\n";
		String cm10 = "10. remove numberOfEntry(not dir)\n";
		String cm0 = "00. exit\n|-----------------------|\n";
		
		
		return str + cm1 + cm2 + cm3 + cm4 + cm5 + cm6 + cm7 + cm8 + cm9 + cm10 + cm0;
	}
	
	public static void main(String args[]) {
		if ( args.length == -1  ) {
			System.out.println(usage());
			System.exit(0);
		}
		
		int hop = parseOption(args, 0);
		
		String host = "";
		String remoteDir = "";
		String localDir = "";
		try {
			host = args[0 + hop];
			remoteDir = args[1 + hop];
			localDir = args[2 + hop];
		} catch(Exception e) {
//			System.out.println(usage());
//			System.exit(0);
			host = "192.168.0.12";
			remoteDir = "/Users/cici/nfss";
			localDir = "/Users/niezhenfei/kkk";
		}
		
		InteractiveControl ic = new InteractiveControl(host, remoteDir, Paths.get(localDir));
		ic.run();
		
	}
	
	public static int parseOption(String args[], int h) {
		if ( args[h].equals("-i") ) {
			String keyfile = args[h + 1];
			KeyOperator.importKey(Paths.get(keyfile));
			System.out.println("Key file imported.");
			h = h+2;
		} else if (args[h].equals("-e") ) {
			String password = args[h+1];
			KeyOperator.exportKey(password);
			System.out.println("Key file exported.");
			System.exit(0);
		}
		return h;
	}
	
	public static String usage() {
		String str = "Usage: java -cp .:../lib/oncrpc.jar client/kkk/InteractiveControl options parameters\n";
		String option = "Options\n-i keyfileath: import key file\n";
		String option2 = "-e password: export key file\n";
		
		str += option;
		str += option2;
		str += "Paramters: \n";
		str += "server_ip shared_folder_path loca_folder_path\n";
		return str;
	}
}
