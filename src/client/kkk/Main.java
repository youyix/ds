package client.kkk;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.acplt.oncrpc.OncRpcAuthenticationException;
import org.acplt.oncrpc.OncRpcClient;
import org.acplt.oncrpc.OncRpcClientAuth;
import org.acplt.oncrpc.OncRpcClientAuthUnix;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.OncRpcProtocols;
import org.acplt.oncrpc.XdrVoid;

//import client.mount.mountClient;
import client.mount.*;
import client.nfs.nfsClient;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws OncRpcException 
	 */
	public static void main(String[] args) throws OncRpcException, IOException {
		tryKO();
	}
	
	public static void tryKO() throws OncRpcException, IOException {
		InetAddress ia = InetAddress.getByName("192.168.0.12");
		mountClient mnt = new mountClient(ia,OncRpcProtocols.ONCRPC_UDP);
		
		OncRpcClientAuth auth = new OncRpcClientAuthUnix("zhenfeinie",501,20);
		mnt.getClient().setAuth(auth);
		
		String mntPoint = "/Users/cici/nfss";
		fhstatus fh = null;
		try {
			fh = mnt.MOUNTPROC_MNT_1(new dirpath(mntPoint));
		} catch (OncRpcAuthenticationException e) {
			e.printStackTrace();
			System.exit(0);
		}
		if(fh.status == 0){
		  byte[] fhandle = fh.directory.value;
		  fhandle root = new fhandle(fhandle);
		  nfsClient nfs = new nfsClient(ia, OncRpcProtocols.ONCRPC_UDP);
		  nfs.getClient().setAuth(auth);
		  System.out.println("YY " + nfs.toString());
		} else {
			System.out.println("ZZZZZZZ " + fh.status);
		}

		
		
		System.out.print("pinging server: ");
	
		System.out.println("\n--END--");
	}
	
	public static void try0() throws OncRpcException, IOException {
		InetAddress ia = InetAddress.getByName("192.168.0.12");
		mountClient mc = new mountClient(ia, 0, OncRpcProtocols.ONCRPC_UDP);
	
		System.out.print("pinging server: ");
		System.out.flush();
		mc.MOUNTPROC_NULL_1();
		System.out.println("server is alive.");
		
//		System.out.print("ttt server: ");
//		dirpath p = new dirpath("/Users/cici/nfss");
//		fhstatus fhs = new fhstatus();
//		try {
//			mc.MOUNTPROC_NULL_1();
//		} catch(OncRpcAuthenticationException e) {
//			int s = e.getAuthStatus();
//			String m = e.getMessage();
//			System.err.println("XX " + s + " " + m);
//			System.err.println(e.getReason() + "  ");
//			System.exit(0);
//		}
		
//		exportlist el = mc.MOUNTPROC_EXPORT_1();
		
		mc.close();
		System.out.println("\n--END--");
	}
	
	public static void try3() throws OncRpcException, IOException {
		InetAddress ia = InetAddress.getByName("192.168.0.12");
		mountClient mc = new mountClient(ia, 0, OncRpcProtocols.ONCRPC_UDP);
	
		int groups[] = {20, 12, 61, 79, 80, 81, 98, 33, 100, 204, 398, 399};
		OncRpcClient client = mc.getClient();
		OncRpcClientAuth auth = new OncRpcClientAuthUnix("zhenfei-MBP", 501, 20, groups);
		client.setAuth(auth);
		mountClient mc2 = new mountClient(client);
		
		System.out.print("pinging server: ");
		System.out.flush();
		dirpath p = new dirpath("/");
		fhstatus fhs = new fhstatus();
		try {
			mc.MOUNTPROC_MNT_1(p);
			System.out.println("server is alive.");
		} catch(OncRpcAuthenticationException e) {
			int s = e.getAuthStatus();
			String m = e.getMessage();
			System.err.println("error " + s + " " + m);
			System.err.println(e.getReason() + "  ");
			System.exit(0);
		}
		
		mc.close();
		mc2.close();
		System.out.println("\n--END--");
	}
	
	public static void try1() throws OncRpcException, IOException {
		InetAddress ia = InetAddress.getByName("192.168.0.12");
		mountClient mc = new mountClient(ia, 0, OncRpcProtocols.ONCRPC_UDP);
		
		OncRpcClient client = mc.getClient();
		System.out.print("pinging server: ");
		System.out.flush();
		try {
			client.call(0, XdrVoid.XDR_VOID, XdrVoid.XDR_VOID);
		} catch ( OncRpcException e ) {
		    System.out.println("method call failed unexpectedly:");
		    e.printStackTrace(System.out);
		    System.exit(1);
		}
		System.out.println("server is alive.");
		
		
		mc.close();
	}
	
	public static void try2() throws UnknownHostException, OncRpcException {
		InetAddress ia = InetAddress.getByName("192.168.0.12");
		mountClient mc = null;
		try {
			mc = new mountClient(ia, 0, OncRpcProtocols.ONCRPC_UDP);
		} catch (OncRpcException e1) {
			System.err.println("OncRpcException");
			e1.printStackTrace();
		} catch (IOException e1) {
			System.err.println("IOException");
			e1.printStackTrace();
		}
		
//		OncRpcClient client = mc.getClient();
		
		dirpath p = new dirpath("/");
		fhstatus fhs = new fhstatus();
		
		try {
			fhs = mc.MOUNTPROC_MNT_1(p);
		} catch (IOException e) {
			System.out.println("kkk");
			e.printStackTrace();
		}
		
		mc.close();
	}
}
