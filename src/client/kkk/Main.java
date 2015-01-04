package client.kkk;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.acplt.oncrpc.OncRpcClient;
import org.acplt.oncrpc.OncRpcClientAuth;
import org.acplt.oncrpc.OncRpcClientAuthUnix;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.OncRpcProtocols;
import org.acplt.oncrpc.XdrVoid;

//import client.mount.mountClient;
import client.mount.*;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws OncRpcException 
	 */
	public static void main(String[] args) throws OncRpcException, IOException {
		try2();
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
