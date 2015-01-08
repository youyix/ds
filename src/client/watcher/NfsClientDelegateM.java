package client.watcher;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.acplt.oncrpc.OncRpcAuthenticationException;
import org.acplt.oncrpc.OncRpcClientAuth;
import org.acplt.oncrpc.OncRpcClientAuthUnix;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.OncRpcProtocols;

import client.encryption.Encryption;
import client.mount.DirPath;
import client.mount.FHStatus;
import client.mount.MountClient;
import client.nfs.AttrStat;
import client.nfs.CreateArgs;
import client.nfs.DirOpArgs;
import client.nfs.DirOpRes;
import client.nfs.FAttr;
import client.nfs.FileName;
import client.nfs.NfsClient;
import client.nfs.NfsData;
import client.nfs.SAttr;
import client.nfs.Stat;
import client.nfs.TimeVal;
import client.nfs.WriteArgs;
import client.sss.NfsShamir;

public class NfsClientDelegateM {
	private Path localDir;
	private MountClient mnt;
	private client.nfs.FHandle root;
	private NfsClient nfsc;
	private client.nfs.FHandle root2;
	private NfsClient nfsc2;
	private String remoteDir;
	private String host;
	private String remoteDir2;
	private String host2;
	
	public NfsClientDelegateM(String host, String host2, String remoteDir, String remoteDir2, Path localDir) {
		this.localDir = localDir;
		this.host = host;
		this.remoteDir = remoteDir;
		this.host2 = host2;
		this.remoteDir2 = remoteDir2;
		
		try {
			mount();
		} catch (OncRpcException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		String host = "192.168.0.12";
		String host2 = "192.168.0.15";
		String remoteDir = "/Users/cici/nfss";
		String remoteDir2 = "/Users/niezhenfei/nfss";
		String localDir = "/Users/niezhenfei/kkkk";
		
		NfsClientDelegateM m = new NfsClientDelegateM(host, host2, remoteDir, remoteDir2, Paths.get(localDir));
		System.out.println("-end-");
	}
	
	
	public NfsClient mount(int i) throws OncRpcException, IOException {
		String host = "";
		String mntPoint = "";
		NfsClient nfss = null;
		client.nfs.FHandle fr = null;
		
		if ( i == 1 ) {
			host = this.host;
			mntPoint = remoteDir;
			nfss = nfsc;
		} else {
			host = this.host2;
			mntPoint = remoteDir2;
			nfss = nfsc2;
		}
		
		InetAddress ia = InetAddress.getByName(host);
		mnt = new MountClient(ia,OncRpcProtocols.ONCRPC_UDP);
		
		OncRpcClientAuth auth = new OncRpcClientAuthUnix("zhenfeinie",501,20);
		mnt.getClient().setAuth(auth);
		
		FHStatus fh = null;
		try {
			fh = mnt.MOUNTPROC_MNT_1(new DirPath(mntPoint));
		} catch (OncRpcAuthenticationException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		if ( fh.status != 0 ) {
			System.exit(0);
		} else {
		  byte[] hh = fh.directory.value;
		  fr = new client.nfs.FHandle(hh);
		  if (i == 1) {
			  root = fr;
		  } else {
			  root2 = fr;
		  }
		  
		  nfss = new NfsClient(ia, OncRpcProtocols.ONCRPC_UDP);
		  nfss.getClient().setAuth(auth);
		}
		return nfss;
	}
	
	public void mount() throws OncRpcException, IOException {
		nfsc = mount(1);
		nfsc2= mount(2);
	}
	
//	public client.nfs.FHandle doo(Path p, String filename) { 
//		return dooM(i, p, filename);
//	}
	
	public client.nfs.FHandle doo(int i, Path p, String filename) { 
		String host = "";
		String mntPoint = "";
		NfsClient nfss = null;
		client.nfs.FHandle croot = null;
		
		if ( i == 1 ) {
			host = this.host;
			mntPoint = remoteDir;
			croot = root;
			nfss = nfsc;
		} else {
			host = this.host2;
			mntPoint = remoteDir2;
			nfss = nfsc2;
			croot = root2;
		}
		
		client.nfs.FHandle fh = croot;
		for ( int i1=localDir.getNameCount(); i1<p.getNameCount(); i1++ ) {
			Path pp = p.getName(i1);
			DirOpRes res = lookup(i, fh, pp.toString());
			if ( res.status == Stat.NFS_OK ) {
				fh = res.diropok.file;
			} else {
				return fh;
			}
		}
		
		return fh;
	}
	
	public DirOpRes lookup(int i, client.nfs.FHandle where, String filename) {
		String host = "";
		String mntPoint = "";
		NfsClient nfss = null;
		client.nfs.FHandle croot = null;
		
		if ( i == 1 ) {
			host = this.host;
			mntPoint = remoteDir;
			croot = root;
			nfss = nfsc;
		} else {
			host = this.host2;
			mntPoint = remoteDir2;
			nfss = nfsc2;
			croot = root2;
		}
		
		DirOpArgs dp = new DirOpArgs();
		dp.dir = where;
		dp.name = new FileName(filename);

		DirOpRes dr = null;
		try {
			dr = nfss.NFSPROC_LOOKUP_2(dp);
		} catch (OncRpcException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if ( dr.status != Stat.NFS_OK ) {
			System.err.println("fff " + dr.status);
		} else {
			return dr;
		}
		return null;
	}
	
	public boolean createFile(Path p, String filename) {
		boolean f = createFileM(1, p, filename);
		boolean g = createFileM(2, p, filename);
		return f && g;
	}
	
	public boolean createFileM(int i, Path p, String filename) {
		String host = "";
		String mntPoint = "";
		NfsClient nfss = null;
		client.nfs.FHandle root = null;
		
		if ( i == 1 ) {
			host = this.host;
			mntPoint = remoteDir;
			root = this.root;
			nfss = nfsc;
		} else {
			host = this.host2;
			mntPoint = remoteDir2;
			nfss = nfsc2;
			root = root2;
		}
		boolean flag = false;	
		
		client.nfs.FHandle fh =  doo(i, p, filename);
		
		//create new dir
		DirOpArgs where = new DirOpArgs();
		where.dir = fh;
		where.name = new FileName(filename);

		SAttr sa = new SAttr();
		sa.atime = new TimeVal();
		sa.mtime = new TimeVal();
		sa.mode = 33261;
		sa.gid = 20;
		sa.uid = 501;

		CreateArgs ca = new CreateArgs();
		ca.where = where;
		ca.attributes = sa;

		DirOpRes dp = null;
		try {
			dp = nfss.NFSPROC_CREATE_2(ca);
		} catch (OncRpcException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if ( dp.status != Stat.NFS_OK ) {
		} else {
		    flag = true;
		}
		return flag;
	}
	
	public boolean mkDir(Path p, String dirname) {
		boolean f = mkDirM(1, p, dirname);
		boolean g = mkDirM(2, p, dirname);
		return f && g;
	}
	
	public boolean mkDirM(int i, Path p, String dirname) {
		String host = "";
		String mntPoint = "";
		NfsClient nfss = null;
		client.nfs.FHandle root = null;
		
		if ( i == 1 ) {
			host = this.host;
			mntPoint = remoteDir;
			root = this.root;
			nfss = nfsc;
		} else {
			host = this.host2;
			mntPoint = remoteDir2;
			nfss = nfsc2;
			root = root2;
		}
		
		boolean flag = false;
		
		client.nfs.FHandle fh = doo(i, p, dirname);
		//create new dir
		DirOpArgs where = new DirOpArgs();
		where.dir = fh;
		where.name = new FileName(dirname);

		SAttr sa = new SAttr();
		sa.atime = new TimeVal();
		sa.mtime = new TimeVal();
		sa.mode = 16877;
		sa.gid = 20;
		sa.uid = 501;

		CreateArgs ca = new CreateArgs();
		ca.where = where;
		ca.attributes = sa;

		DirOpRes dp = null;
		try {
			dp = nfss.NFSPROC_MKDIR_2(ca);
		} catch (OncRpcException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if ( dp.status != Stat.NFS_OK ) {
		} else {
		    flag = true;
		}
		return flag;
	}
	
	public boolean writeFile(Path p, String filename) {
		Path file = Paths.get(p.toString(), filename);
		byte[] fileArray = null;
		try {
			fileArray = Files.readAllBytes(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ByteBuffer[] bbs = NfsShamir.split(2, fileArray);
//		fileArray = Encryption.getInstance().encrypt(fileArray);
		
		
		boolean f = writeFileM(1, p, filename, bbs[0].array());
		boolean g = writeFileM(2, p, filename, bbs[1].array());
		return f && g;
	}
	
	public boolean writeFileM(int i, Path p, String filename, byte[] fileArray) {
		String host = "";
		String mntPoint = "";
		NfsClient nfss = null;
		client.nfs.FHandle root = null;
		
		if ( i == 1 ) {
			host = this.host;
			mntPoint = remoteDir;
			root = this.root;
			nfss = nfsc;
		} else {
			host = this.host2;
			mntPoint = remoteDir2;
			nfss = nfsc2;
			root = root2;
		}
		
		boolean flag = false;
		client.nfs.FHandle fh = doo(i, p, filename);
		System.out.println(i + " " + fh.hashCode());
		
		DirOpRes res = lookup(i, fh, filename);
		if (res == null) {
			System.out.println("xxxxx" + i);
			return flag;
		}
		if ( res.status != Stat.NFS_OK ) {
			return flag;
		}
		fh = res.diropok.file;
		
		WriteArgs wa = new WriteArgs();
	    wa.file = fh;
	    wa.offset = 0;
	    wa.data = new NfsData(fileArray);

	    AttrStat as = null;
		try {
			as = nfss.NFSPROC_WRITE_2(wa);
		} catch (OncRpcException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    if ( as.status != Stat.NFS_OK ) {
	       System.out.println("7 Not Fine " + as.status);
	    } else {
	       System.out.println("7 Fine");
	    	flag = true;
	    }
		return flag;
	}
}
