package client.watcher;

import java.io.IOException;
import java.net.InetAddress;
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

public class NfsClientDelegate {
	private Path localDir;
	private MountClient mnt;
	private client.nfs.FHandle root;
	private NfsClient nfsc;
	private String remoteDir;
	private String host;
	
	public NfsClientDelegate(String host, String remoteDir, Path localDir) {
		this.localDir = localDir;
		this.host = host;
		this.remoteDir = remoteDir;
		try {
			mount();
		} catch (OncRpcException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void mount() throws OncRpcException, IOException {
		InetAddress ia = InetAddress.getByName(host);
		mnt = new MountClient(ia,OncRpcProtocols.ONCRPC_UDP);
		
		OncRpcClientAuth auth = new OncRpcClientAuthUnix("zhenfeinie",501,20);
		mnt.getClient().setAuth(auth);
		
		String mntPoint = remoteDir;
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
		  root = new client.nfs.FHandle(hh);
		  nfsc = new NfsClient(ia, OncRpcProtocols.ONCRPC_UDP);
		  nfsc.getClient().setAuth(auth);
		}
	}
	
	public client.nfs.FHandle doo(Path p, String filename) { 
		client.nfs.FHandle fh = root;
		for ( int i=localDir.getNameCount(); i<p.getNameCount(); i++ ) {
			Path pp = p.getName(i);
			DirOpRes res = lookup(fh, pp.toString());
			if ( res.status == Stat.NFS_OK ) {
				fh = res.diropok.file;
			} else {
				return fh;
			}
		}
		
		return fh;
	}
	
	public DirOpRes lookup(client.nfs.FHandle where, String filename) {
		DirOpArgs dp = new DirOpArgs();
		dp.dir = where;
		dp.name = new FileName(filename);

		DirOpRes dr = null;
		try {
			dr = nfsc.NFSPROC_LOOKUP_2(dp);
		} catch (OncRpcException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if ( dr.status != Stat.NFS_OK ) {
		} else {
			return dr;
		}
		return null;
	}
	
	public boolean createFile(Path p, String filename) {
		boolean flag = false;	
		
		client.nfs.FHandle fh =  doo(p, filename);
		
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
			dp = nfsc.NFSPROC_CREATE_2(ca);
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
		boolean flag = false;
		
		client.nfs.FHandle fh = doo(p, dirname);
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
			dp = nfsc.NFSPROC_MKDIR_2(ca);
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
		boolean flag = false;
		client.nfs.FHandle fh = doo(p, filename);
		Path file = Paths.get(p.toString(), filename);
		System.out.println(file.toString());
		byte[] fileArray = null;
		try {
			fileArray = Files.readAllBytes(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileArray = Encryption.getInstance().encrypt(fileArray);
		
		DirOpRes res = lookup(fh, filename);
		if (res == null) {
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
			as = nfsc.NFSPROC_WRITE_2(wa);
		} catch (OncRpcException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    if ( as.status != Stat.NFS_OK ) {
//	       System.out.println("7 Not Fine " + as.status);
	    } else {
//	       System.out.println("7 Fine");
	    	flag = true;
	    }
		return flag;
	}
}
