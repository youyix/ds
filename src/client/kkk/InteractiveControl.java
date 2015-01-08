package client.kkk;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import client.watcher.Watcher;


public class InteractiveControl {
	private NfsDir nfsc;
	private String host;
	private String remoteDir;
	private Path currentPath;
	
	private Watcher watcher;
	
	public InteractiveControl(String host, String remoteDir, Path dir) {
		this.host = host;
		this.remoteDir = remoteDir;
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
				System.out.printf("%2d [%5s] %20s\n", i, entry.getType(), entry.filename.value );
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
	
	public void open() {
		
	}
	
	public void rename() {
//		diropargs from = new diropargs();
//		from.dir = root;
//		from.name = new FileName("kkk");
//
//		diropargs to = new diropargs();
//		to.dir = from.dir;
//		to.name = new FileName("dirkkk");
//
//		renameargs ra = new renameargs();
//		ra.from = from;
//		ra.to = to;
//
//		int status = nfsc.NFSPROC_RENAME_2(ra);
//		if ( status != Stat.NFS_OK ) {
//		    System.out.println("Not Fine");
//		} else {
//		    System.out.println("Fine");
//		  
//		}
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
	
	public boolean delete() {
		boolean flag = false;
		return flag;
	} 
	
	public void getAttr(int i) {
		List<EntryWrapper> ews = nfsc.readDir(currentPath, "");
		EntryWrapper e = ews.get(i);
		System.out.println(Utils.FAttr2String(e.attribute));
	} 
	
	
	public void run() {
		System.out.println(host + ":" + remoteDir + " has mounted.");
		Scanner in = new Scanner(System.in);
		String s = "";
		while((s = in.next()) != null) {
			if ( s.equals("ls") ) {
				listFiles(false);
			} else if (s.equals("ll")) {
				listFiles(true);
			} else if (s.equals("mkdir")) {
				String param = in.next();
				if ( mkDir(param) ) {
					System.out.printf("Dir %s is created.\n", param);
				} else {
					System.out.println("Error to mkdir " + param);
				}
			} else if ( s.equals("touch") ) {
				String param = in.next();
				if ( createFile(param) ) {
					System.out.printf("File %s is created.\n", param);
				} else {
					System.out.println("Error to create new file " + param);
				}

			} else if ( s.equals("touchText") ) {
				String param = in.next();
				if ( createFile(param) ) {
					System.out.printf("File %s is created.\n", param);
				} else {
					System.out.println("Error to create new file " + param);
				}
				
			} else if ( s.equals("show") ) {
				int i = in.nextInt();
				getAttr(i);
			} else if ( s.equals("download") ) {
				int i = in.nextInt();
				if ( download(i) ) {
					System.out.printf("File  #%d has been downloaded.\n", i);
				} else {
					System.out.println("Error to download file #" + i);
				}
			} else if (s.equals("exit")) {
				watcher.terminates();
				System.exit(0);
			}
			else {
				System.out.println("Unkown command " + s);
			}
			
		}
		
	}
	
	public void watch() {
		watcher.start();
	}
	public void stopWatch() {
		watcher.terminates();
	}
	
	public static void main(String args[]) {
		InteractiveControl ic = new InteractiveControl("192.168.0.12", "/Users/cici/nfss", Paths.get("/Users/niezhenfei/kkk"));
		ic.run();
		
	}
}
