package client.kkk;

import java.io.File;
import java.io.FileOutputStream;
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

import sun.misc.IOUtils;

//import client.mount.mountClient;
import client.mount.fhstatus;
import client.mount.mountClient;
import client.mount.mount;
import client.mount.dirpath;

import client.nfs.attrstat;
import client.nfs.createargs;
import client.nfs.diropargs;
import client.nfs.diropres;
import client.nfs.entry;
import client.nfs.fattr;
import client.nfs.filename;
import client.nfs.nfs;
import client.nfs.nfsClient;
import client.nfs.nfscookie;
import client.nfs.nfsdata;
import client.nfs.readargs;
import client.nfs.readdirargs;
import client.nfs.readdirres;
import client.nfs.readres;
import client.nfs.renameargs;
import client.nfs.sattr;
import client.nfs.sattrargs;
import client.nfs.stat;
import client.nfs.timeval;
import client.nfs.writeargs;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws OncRpcException 
	 */
	public static void main(String[] args) throws OncRpcException, IOException {
		tryKO();
//		try0();
	}
	
	public static void tryKO() throws OncRpcException, IOException {
		InetAddress ia = InetAddress.getByName("192.168.0.12");
		mountClient mnt = new mountClient(ia,OncRpcProtocols.ONCRPC_UDP);
		
		OncRpcClientAuth auth = new OncRpcClientAuthUnix("zhenfeinie",501,20);
		mnt.getClient().setAuth(auth);
		
		String mntPoint = "/Users/cici/nfss/";
		fhstatus fh = null;
		try {
			fh = mnt.MOUNTPROC_MNT_1(new dirpath(mntPoint));
		} catch (OncRpcAuthenticationException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		if ( fh.status != 0 ) {
			System.out.println("ZZZZZZZ " + fh.status);
			System.exit(0);
		} else {
		  byte[] hh = fh.directory.value;
		  client.nfs.fhandle root = new client.nfs.fhandle(hh);
		  nfsClient nfsc = new nfsClient(ia, OncRpcProtocols.ONCRPC_UDP);
		  nfsc.getClient().setAuth(auth);
		  System.out.println("YY " + nfsc.toString());
		  
		  readdirargs rda = new readdirargs();
		  rda.dir = root;
		  byte b[] = new byte[nfs.COOKIESIZE];
		  rda.cookie = new nfscookie(b);
		  rda.count = 100000;
		  
		  readdirres rdr = new readdirres();
		  rdr = nfsc.NFSPROC_READDIR_2(rda);
		  
		  if ( rdr.status != stat.NFS_OK ) {
			  System.out.println("NNN " + rdr.status);
			  System.exit(0);
		  } else {
			  entry entries = rdr.readdirok.entries;
			  System.out.println("VV " + rdr.readdirok.eof);
			  entry e = entries;
			  if ( rdr.readdirok.entries != null ) {
				  do { 
					  System.out.println(e.name.value);
					  if ( e.nextentry != null ) {
						  e = e.nextentry;
					  } else {
						  e = null;
					  }
				  } while (e != null);
			  } else {
				  System.out.println("EMPTY??");
			  }
		  }
		}
		
		System.out.println("\n--END--");
	}
	
	public static void try0() throws OncRpcException, IOException {
		InetAddress ia = InetAddress.getByName("192.168.0.12");
		mountClient mnt = new mountClient(ia,OncRpcProtocols.ONCRPC_UDP);
		
		OncRpcClientAuth auth = new OncRpcClientAuthUnix("zhenfeinie",501,20);
		mnt.getClient().setAuth(auth);
		
		String mntPoint = "/Users/cici/nfss/";
		fhstatus fh = null;
		try {
			fh = mnt.MOUNTPROC_MNT_1(new dirpath(mntPoint));
		} catch (OncRpcAuthenticationException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		if ( fh.status != 0 ) {
			System.out.println("ZZZZZZZ " + fh.status);
			System.exit(0);
		} else {
		  byte[] hh = fh.directory.value;
		  client.nfs.fhandle root = new client.nfs.fhandle(hh);
		  nfsClient nfsc = new nfsClient(ia, OncRpcProtocols.ONCRPC_UDP);
		  nfsc.getClient().setAuth(auth);
		  System.out.println("YY " + nfsc.toString());
		  
		  
		  
		  // get attr
//		  attrstat as = nfsc.NFSPROC_GETATTR_2(root);
//		  if ( as.status == stat.NFS_OK ) {
//			  System.out.printf("%d    %7o\n---\n", as.attributes.mode, as.attributes.mode);
//		  } else {
//			  System.out.println("CCC");
//			  System.exit(0);
//		  }
		  
		  // look up
//		  diropargs dp = new diropargs();
//		  dp.dir = root;
//		  dp.name = new filename("ko.txt");
//		  
//		  diropres dr = nfsc.NFSPROC_LOOKUP_2(dp);
//		  if ( dr.status != stat.NFS_OK ) {
//			  System.out.println("Not Fine");
//		  } else {
//			  System.out.println("Fine");
//			  client.nfs.fhandle f = dr.diropok.file;
//			  fattr f_a = dr.diropok.attributes; 
//			  System.out.println(f_a.size);
//		  }
		  
		  
		  //rename
//		  diropargs from = new diropargs();
//		  from.dir = root;
//		  from.name = new filename("kkk");
//		  
//		  diropargs to = new diropargs();
//		  to.dir = from.dir;
//		  to.name = new filename("dirkkk");
//		  
//		  renameargs ra = new renameargs();
//		  ra.from = from;
//		  ra.to = to;
//		  
//		  int status = nfsc.NFSPROC_RENAME_2(ra);
//		  if ( status != stat.NFS_OK ) {
//			  System.out.println("Not Fine");
//		  } else {
//			  System.out.println("Fine");
//			  
//		  }
		  
		  
//		  diropargs from = new diropargs();
//		  from.dir = root;
//		  from.name = new filename("emdir");
//		  
//		  int status = nfsc.NFSPROC_RMDIR_2(from);
//		  if ( status != stat.NFS_OK ) {
//			  System.out.println("3 Not Fine");
//		  } else {
//			  System.out.println("3 Fine");
//			  
//		  }
		  //create new dir
//		  diropargs where = new diropargs();
//		  where.dir = root;
//		  where.name = new filename("newc");
//		  
//		  sattr sa = new sattr();
//		  sa.atime = new timeval();
//		  sa.mtime = new timeval();
//		  sa.mode = 16877;
//		  sa.gid = 20;
//		  sa.uid = 501;
//		  
//		  createargs ca = new createargs();
//		  ca.where = where;
//		  ca.attributes = sa;
//		  
//		  diropres dp = nfsc.NFSPROC_MKDIR_2(ca);
//		  if ( dp.status != stat.NFS_OK ) {
//			  System.out.println("4 Not Fine");
//		  } else {
//			  System.out.println("4 Fine");
//		  }
		  
		  //create file
//		  diropargs where = new diropargs();
//		  where.dir = root;
//		  where.name = new filename("newfile.txt");
//		  
//		  sattr sa = new sattr();
//		  sa.atime = new timeval();
//		  sa.mtime = new timeval();
//		  sa.mode = 33261;
//		  sa.gid = 20;
//		  sa.uid = 501;
//		  
//		  createargs ca = new createargs();
//		  ca.where = where;
//		  ca.attributes = sa;
//		  
//		  diropres dp = nfsc.NFSPROC_CREATE_2(ca);
//		  if ( dp.status != stat.NFS_OK ) {
//			  System.out.println("5 Not Fine " + dp.status);
//		  } else {
//			  System.out.println("5 Fine");
//		  }
		  
		  
		  //read file
//		  String fn = "newfile.txt";
//		  diropargs dp = new diropargs();
//		  dp.dir = root;
//		  dp.name = new filename(fn);
//		  
//		  diropres dr = nfsc.NFSPROC_LOOKUP_2(dp);
//		  if ( dr.status != stat.NFS_OK ) {
//			  System.out.println("Not Fine");
//		  } else {
//			  System.out.println("Fine");
//			  client.nfs.fhandle f = dr.diropok.file;
//			  fattr f_a = dr.diropok.attributes; 
//			  System.out.println(f_a.size);
//			  
//			  //
//			  readargs ra = new readargs();
//			  ra.file = f;
//			  ra.offset = 0;
//			  ra.count = f_a.size;
//			  
//			  readres rr = nfsc.NFSPROC_READ_2(ra);
//			  if ( rr.status != stat.NFS_OK ) {
//				  System.out.println("6 Not Fine " + rr.status);
//			  } else {
//				  System.out.println("6 Fine");
//				  fattr fa = rr.read.attributes;
//				  nfsdata data = rr.read.data;
//				  FileOutputStream output = new FileOutputStream(new File(fn));
//				  output.write(data.value);
//			  }
//		  }
		  
		  // write file
//		  String fn = "newfile.txt";
//		  diropargs dp = new diropargs();
//		  dp.dir = root;
//		  dp.name = new filename(fn);
//		  
//		  diropres dr = nfsc.NFSPROC_LOOKUP_2(dp);
//		  if ( dr.status != stat.NFS_OK ) {
//			  System.out.println("Not Fine");
//		  } else {
//			  System.out.println("Fine");
//			  client.nfs.fhandle f = dr.diropok.file;
//			  fattr f_a = dr.diropok.attributes; 
//			  //
//			  String s = "hello ghouan!\ndasdasdasd\nasdasd\ts\nddsjglj\nasdsdsfad";
//			  byte[] dd = s.getBytes();
//			  
//			  writeargs wa = new writeargs();
//			  wa.file = f;
//			  wa.offset = 0;
//			  wa.data = new nfsdata(dd);
//			  
//			  attrstat as = nfsc.NFSPROC_WRITE_2(wa);
//			  if ( as.status != stat.NFS_OK ) {
//				  System.out.println("7 Not Fine " + as.status);
//			  } else {
//				  System.out.println("7 Fine");
//			  }
//		  }
		  
		  
		  
		}
	}
	
}