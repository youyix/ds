package client.watcher;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.acplt.oncrpc.OncRpcAuthenticationException;
import org.acplt.oncrpc.OncRpcClientAuth;
import org.acplt.oncrpc.OncRpcClientAuthUnix;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.OncRpcProtocols;

import client.mount.DirPath;
import client.mount.FHStatus;
import client.mount.MountClient;
import client.nfs.AttrStat;
import client.nfs.CreateArgs;
import client.nfs.DirOpArgs;
import client.nfs.DirOpRes;
import client.nfs.FAttr;
import client.nfs.FHandle;
import client.nfs.FileName;
import client.nfs.NfsClient;
import client.nfs.NfsData;
import client.nfs.SAttr;
import client.nfs.Stat;
import client.nfs.TimeVal;
import client.nfs.WriteArgs;

public class NfsClientDelegate {
	private Path dir;
	private MountClient mnt;
	private client.nfs.FHandle root;
	private NfsClient nfsc;
	
	public NfsClientDelegate(Path dir) {
		this.dir = dir;
		try {
			mount();
		} catch (OncRpcException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void mount() throws OncRpcException, IOException {
		InetAddress ia = InetAddress.getByName("192.168.0.12");
		mnt = new MountClient(ia,OncRpcProtocols.ONCRPC_UDP);
		
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
		  root = new client.nfs.FHandle(hh);
		  nfsc = new NfsClient(ia, OncRpcProtocols.ONCRPC_UDP);
		  nfsc.getClient().setAuth(auth);
		}
		System.out.println("Mount !!");
	}
	
	public client.nfs.FHandle doo(Path p, String filename) { 
//		int count = p.getNameCount() - dir.getNameCount();
		client.nfs.FHandle fh = root;
		System.out.println("doo");
		for ( int i=dir.getNameCount(); i<p.getNameCount(); i++ ) {
			System.out.println("mmmm");
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
			System.out.println("Not Fine");
		} else {
			System.out.println("Fine");
			client.nfs.FHandle f = dr.diropok.file;
			FAttr f_a = dr.diropok.attributes; 
			System.out.println(f_a.size);
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
		    System.out.println("3 Not Fine " + dp.status);
		} else {
		    System.out.println("3 Fine");
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
		    System.out.println("4 Not Fine " + dp.status);
		} else {
		    System.out.println("4 Fine");
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
		
		
		DirOpRes res =  lookup(fh, filename);
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
	       System.out.println("7 Not Fine " + as.status);
	    } else {
	       System.out.println("7 Fine");
	    }
		return flag;
	}
}
