package client.kkk;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;


public class InteractiveControl {
	private NfsDir nfsc;
	private String host;
	private String remoteDir;
	private Path currentPath;
	
	
	public InteractiveControl(String host, String remoteDir, Path dir) {
		this.host = host;
		this.remoteDir = remoteDir;
//		nfsc = new NfsDir("192.168.0.12", "/Users/cici/nfss", dir);
		nfsc = new NfsDir(host, remoteDir, dir);
		nfsc.mount();
		currentPath = Paths.get(this.remoteDir);
	}
	
	public void listFiles() {
		List<EntryWrapper> ews = nfsc.readDir(currentPath, "");
		if ( ews == null ) {
			System.out.println("Empty dir");
			return;
		}
		for ( int i=0; i<ews.size(); i++  ) {
			EntryWrapper entry = ews.get(i);
			System.out.printf("%2d [%8s] %20s\n", i, entry.getType(), entry.filename.value );
		}
	}
	
	public void open() {
		
	}
	
	public void rename() {
		
	}
	
	public boolean mkDir() {
		boolean flag = false;
		return flag;
	}
	
	public boolean createTxtFile() {
		boolean flag = false;
		return flag;
	}
	
	public boolean download() {
		boolean flag = false;
		return flag;
	}
	
	public boolean delete() {
		boolean flag = false;
		return flag;
	} 
	
	public boolean getAttr() {
		boolean flag = false;
		
		return flag;
	} 
	
	
	public void run() {
		System.out.println(host + ":" + remoteDir + " has mounted.");
		Scanner in = new Scanner(System.in);
		String s = "";
		while((s = in.next()) != null) {
			if ( s.equals("ls") ) {
				listFiles();
			} else {
				System.out.println("Unkown command " + s);
			}
			
			
		}
		
	}
	
	
	public static void main(String args[]) {
		InteractiveControl ic = new InteractiveControl("192.168.0.12", "/Users/cici/nfss", Paths.get("/Users/niezhenfei/kkk"));
		ic.run();
	}
}
