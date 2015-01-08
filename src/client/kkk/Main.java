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
import client.mount.FHStatus;
import client.mount.MountClient;
import client.mount.Mount;
import client.mount.DirPath;

import client.nfs.AttrStat;
import client.nfs.CreateArgs;
import client.nfs.DirOpArgs;
import client.nfs.DirOpRes;
import client.nfs.Entry;
import client.nfs.FAttr;
import client.nfs.FileName;
import client.nfs.Nfs;
import client.nfs.NfsClient;
import client.nfs.NfsCookie;
import client.nfs.NfsData;
import client.nfs.ReadArgs;
import client.nfs.ReadDirArgs;
import client.nfs.ReadDirRes;
import client.nfs.ReadRes;
import client.nfs.RenameArgs;
import client.nfs.SAttr;
import client.nfs.SAttrArgs;
import client.nfs.Stat;
import client.nfs.TimeVal;
import client.nfs.WriteArgs;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws OncRpcException 
	 */
	public static void main(String[] args) throws OncRpcException, IOException {
//		tryKO();
		try0();
	}
	
	public static void tryKO() throws OncRpcException, IOException {
		InetAddress ia = InetAddress.getByName("192.168.0.12");
		MountClient mnt = new MountClient(ia,OncRpcProtocols.ONCRPC_UDP);
		
		OncRpcClientAuth auth = new OncRpcClientAuthUnix("zhenfeinie",501,20);
		mnt.getClient().setAuth(auth);
		
		String mntPoint = "/Users/cici/nfss/";
		FHStatus fh = null;
		try {
			fh = mnt.MOUNTPROC_MNT_1(new DirPath(mntPoint));
		} catch (OncRpcAuthenticationException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		if ( fh.status != 0 ) {
			System.out.println("ZZZZZZZ " + fh.status);
			System.exit(0);
		} else {
		  byte[] hh = fh.directory.value;
		  client.nfs.FHandle root = new client.nfs.FHandle(hh);
		  NfsClient nfsc = new NfsClient(ia, OncRpcProtocols.ONCRPC_UDP);
		  nfsc.getClient().setAuth(auth);
		  System.out.println("YY " + nfsc.toString());
		  
		  ReadDirArgs rda = new ReadDirArgs();
		  rda.dir = root;
		  byte b[] = new byte[Nfs.COOKIESIZE];
		  rda.cookie = new NfsCookie(b);
		  rda.count = 10000;
		  
		  ReadDirRes rdr = new ReadDirRes();
		  rdr = nfsc.NFSPROC_READDIR_2(rda);
		  
		  if ( rdr.status != Stat.NFS_OK ) {
			  System.out.println("NNN " + rdr.status);
			  System.exit(0);
		  } else {
			  Entry entries = rdr.readdirok.entries;
			  System.out.println("VV " + rdr.readdirok.eof);
			  Entry e = entries;
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
				  System.out.println("EMPTY Dir or the `count` is too small");
			  }
		  }
		}
		
		System.out.println("\n--END--");
	}
	
	public static void try0() throws OncRpcException, IOException {
		InetAddress ia = InetAddress.getByName("192.168.0.12");
		MountClient mnt = new MountClient(ia,OncRpcProtocols.ONCRPC_UDP);
		
		OncRpcClientAuth auth = new OncRpcClientAuthUnix("zhenfeinie",501,20);
		mnt.getClient().setAuth(auth);
		
		String mntPoint = "/Users/cici/nfss/";
		FHStatus fh = null;
		try {
			fh = mnt.MOUNTPROC_MNT_1(new DirPath(mntPoint));
		} catch (OncRpcAuthenticationException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		if ( fh.status != 0 ) {
			System.out.println("ZZZZZZZ " + fh.status);
			System.exit(0);
		} else {
		  byte[] hh = fh.directory.value;
		  client.nfs.FHandle root = new client.nfs.FHandle(hh);
		  NfsClient nfsc = new NfsClient(ia, OncRpcProtocols.ONCRPC_UDP);
		  nfsc.getClient().setAuth(auth);
		  System.out.println("YY " + nfsc.toString());
		  
		  
		  
		  // get attr
//		  attrstat as = nfsc.NFSPROC_GETATTR_2(root);
//		  if ( as.status == Stat.NFS_OK ) {
//			  System.out.printf("%d    %7o\n---\n", as.attributes.mode, as.attributes.mode);
//		  } else {
//			  System.out.println("CCC");
//			  System.exit(0);
//		  }
		  
		  // look up
		  DirOpArgs dp = new DirOpArgs();
		  dp.dir = root;
		  dp.name = new FileName("hhh");
		  
		  DirOpRes dr = nfsc.NFSPROC_LOOKUP_2(dp);
		  if ( dr.status != Stat.NFS_OK ) {
			  System.out.println("Not Fine");
		  } else {
			  System.out.println("Fine");
			  client.nfs.FHandle f = dr.diropok.file;
			  FAttr f_a = dr.diropok.attributes; 
			  System.out.println(f_a.size);
		  }
		  
		  
		  //rename
//		  diropargs from = new diropargs();
//		  from.dir = root;
//		  from.name = new FileName("kkk");
//		  
//		  diropargs to = new diropargs();
//		  to.dir = from.dir;
//		  to.name = new FileName("dirkkk");
//		  
//		  renameargs ra = new renameargs();
//		  ra.from = from;
//		  ra.to = to;
//		  
//		  int status = nfsc.NFSPROC_RENAME_2(ra);
//		  if ( status != Stat.NFS_OK ) {
//			  System.out.println("Not Fine");
//		  } else {
//			  System.out.println("Fine");
//			  
//		  }
		  
		  
//		  diropargs from = new diropargs();
//		  from.dir = root;
//		  from.name = new FileName("emdir");
//		  
//		  int status = nfsc.NFSPROC_RMDIR_2(from);
//		  if ( status != Stat.NFS_OK ) {
//			  System.out.println("3 Not Fine");
//		  } else {
//			  System.out.println("3 Fine");
//			  
//		  }
		  //create new dir
//		  diropargs where = new diropargs();
//		  where.dir = root;
//		  where.name = new FileName("newc");
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
//		  DirOpRes dp = nfsc.NFSPROC_MKDIR_2(ca);
//		  if ( dp.status != Stat.NFS_OK ) {
//			  System.out.println("4 Not Fine");
//		  } else {
//			  System.out.println("4 Fine");
//		  }
		  
		  //create file
//		  diropargs where = new diropargs();
//		  where.dir = root;
//		  where.name = new FileName("newfile.txt");
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
//		  DirOpRes dp = nfsc.NFSPROC_CREATE_2(ca);
//		  if ( dp.status != Stat.NFS_OK ) {
//			  System.out.println("5 Not Fine " + dp.status);
//		  } else {
//			  System.out.println("5 Fine");
//		  }
		  
		  
		  //read file
//		  String fn = "newfile.txt";
//		  diropargs dp = new diropargs();
//		  dp.dir = root;
//		  dp.name = new FileName(fn);
//		  
//		  DirOpRes dr = nfsc.NFSPROC_LOOKUP_2(dp);
//		  if ( dr.status != Stat.NFS_OK ) {
//			  System.out.println("Not Fine");
//		  } else {
//			  System.out.println("Fine");
//			  client.nfs.FHandle f = dr.diropok.file;
//			  FAttr f_a = dr.diropok.attributes; 
//			  System.out.println(f_a.size);
//			  
//			  //
//			  readargs ra = new readargs();
//			  ra.file = f;
//			  ra.offset = 0;
//			  ra.count = f_a.size;
//			  
//			  readres rr = nfsc.NFSPROC_READ_2(ra);
//			  if ( rr.status != Stat.NFS_OK ) {
//				  System.out.println("6 Not Fine " + rr.status);
//			  } else {
//				  System.out.println("6 Fine");
//				  FAttr fa = rr.read.attributes;
//				  nfsdata data = rr.read.data;
//				  FileOutputStream output = new FileOutputStream(new File(fn));
//				  output.write(data.value);
//			  }
//		  }
		  
		  // write file
//		  String fn = "newfile.txt";
//		  diropargs dp = new diropargs();
//		  dp.dir = root;
//		  dp.name = new FileName(fn);
//		  
//		  DirOpRes dr = nfsc.NFSPROC_LOOKUP_2(dp);
//		  if ( dr.status != Stat.NFS_OK ) {
//			  System.out.println("Not Fine");
//		  } else {
//			  System.out.println("Fine");
//			  client.nfs.FHandle f = dr.diropok.file;
//			  FAttr f_a = dr.diropok.attributes; 
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
//			  if ( as.status != Stat.NFS_OK ) {
//				  System.out.println("7 Not Fine " + as.status);
//			  } else {
//				  System.out.println("7 Fine");
//			  }
//		  }
		  
		  
		  
		}
	}
	
}