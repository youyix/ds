package client.kkk;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
import client.nfs.Stat;
import client.nfs.TimeVal;
import client.nfs.WriteArgs;
import client.sss.NfsShamir;

public class NfsDirM {
	private Path downloadDir;
	private MountClient mnt;
	private client.nfs.FHandle root;
	private client.nfs.FHandle root2;
	
	private NfsClient nfsc;
	private NfsClient nfsc2;
	private String host;
	private String host2;
	private String remoteDir;
	private String remoteDir2;
	
	public NfsDirM(String host, String host2, String remoteDir, String remoteDir2, Path downloadDir) {
		this.downloadDir = downloadDir;
		this.host = host;
		this.host2 = host2;
		this.remoteDir = remoteDir;
		this.remoteDir2 = remoteDir2;
		nfsc = mount(nfsc, host, remoteDir, 1);
		nfsc2 = mount(nfsc2, host2, remoteDir2, 2);
	}
	
	public NfsClient mount(NfsClient nfss, String hostt, String mntPoint, int i){
		InetAddress ia = null;
		try {
			ia = InetAddress.getByName(hostt);
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
			System.exit(0);
		}
		try {
			mnt = new MountClient(ia,OncRpcProtocols.ONCRPC_UDP);
		} catch (OncRpcException | IOException e1) {
			e1.printStackTrace();
			System.exit(0);
		}
		
		OncRpcClientAuth auth = new OncRpcClientAuthUnix("zhenfeinie",501,20);
		mnt.getClient().setAuth(auth);
		
		FHStatus fh = null;
		try {
			fh = mnt.MOUNTPROC_MNT_1(new DirPath(mntPoint));
		} catch (OncRpcAuthenticationException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (OncRpcException | IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		if ( fh.status != 0 ) {
			System.err.println("error " + fh.status);
			System.exit(0);
		} else {
		  byte[] hh = fh.directory.value;
		  if (i==1) {
			  root = new client.nfs.FHandle(hh);
		  } else {
			  root2 = new client.nfs.FHandle(hh);
		  }
		  try {
			  nfss = new NfsClient(ia, OncRpcProtocols.ONCRPC_UDP);
		  } catch (OncRpcException | IOException e) {
			  e.printStackTrace();
			  System.exit(0);
		  }
		  nfss.getClient().setAuth(auth);
		}
		return nfss;
	}
	
	public client.nfs.FHandle doo(NfsClient nfss, Path p) {
		client.nfs.FHandle fh = null;
		if ( nfss.hashCode() == nfsc.hashCode() ) {
			fh = root;
		} else {
			fh = root2;
		}
		
		for ( int i=downloadDir.getNameCount(); i<p.getNameCount(); i++ ) {
			Path pp = p.getName(i);
			DirOpRes res = lookup(nfss, fh, pp.toString());
			if ( res.status == Stat.NFS_OK ) {
				fh = res.diropok.file;
			} else {
				return fh;
			}
		}
		
		return fh;
	}
	
	public DirOpRes lookup(NfsClient nfss, client.nfs.FHandle where, String filename) {
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
		} else {
			return dr;
		}
		return null;
	}
	
	
	public boolean createFileM(Path p, String filename) {
		boolean f = createFile(nfsc, p, filename);
		boolean g = createFile(nfsc2, p, filename);
		return f && g;
	}
	
	public boolean createFile(NfsClient nfss, Path p, String filename) {
		boolean flag = false;	
		
		client.nfs.FHandle fh =  doo(nfss, p);
		
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
	
	public boolean mkdirM(Path p, String filename) {
		boolean f = mkDir(nfsc, p, filename);
		boolean g = mkDir(nfsc2, p, filename);
		return f && g;
	}
	
	public boolean mkDir(NfsClient nfss, Path p, String dirname) {
		boolean flag = false;
		
		client.nfs.FHandle fh = doo(nfss, p);
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
	
	public boolean writeFileM(Path p, String filename) {
		Path file = Paths.get(p.toString(), filename);
		byte[] fileArray = null;
		try {
			fileArray = Files.readAllBytes(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ByteBuffer[] bbs = NfsShamir.split(2, fileArray);
//		fileArray = Encryption.getInstance().encrypt(fileArray);
		
		boolean f = writeFile(nfsc, p, filename, bbs[0].array());
		boolean g = writeFile(nfsc2, p, filename, bbs[1].array());
		return f && g;
	}
	
	public boolean writeFile(NfsClient nfss, Path p, String filename, byte[] fileArray) {
		boolean flag = false;
		client.nfs.FHandle fh = doo(nfss, p);
		
		
		DirOpRes res =  lookup(nfss, fh, filename);
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
	    } else {
	    	flag = true;
	    }
		return flag;
	}

	public FAttr getAttrM(Path p, String filename) {
		return getAttr(nfsc, p, filename);
	}
	
	public FAttr getAttr(NfsClient nfss, Path p, String filename) {
		client.nfs.FHandle fh = doo(nfss, Paths.get(p.toString(), filename));
		AttrStat as = null;
		try {
			as = nfss.NFSPROC_GETATTR_2(fh);
		} catch (OncRpcException | IOException e) {
			e.printStackTrace();
		}
		if ( as.status == Stat.NFS_OK ) {
		    return as.attributes;
		}
		return null;
	}
	
	public List<EntryWrapper> readDirM(Path p, String dirname) {
		return readDir(nfsc, p, dirname);
	}
	
	public List<EntryWrapper> readDir(NfsClient nfss, Path p, String dirname) {
		
		client.nfs.FHandle fh = doo(nfss, Paths.get(p.toString(), dirname));
		System.out.println(Paths.get(p.toString(), dirname));
		ReadDirArgs rda = new ReadDirArgs();
		rda.dir = fh;
		byte b[] = new byte[Nfs.COOKIESIZE];
		rda.cookie = new NfsCookie(b);
		rda.count = 10000;

		ReadDirRes rdr = new ReadDirRes();
		try {
			rdr = nfss.NFSPROC_READDIR_2(rda);
		} catch (OncRpcException | IOException e1) {
			e1.printStackTrace();
		}

		if ( rdr.status != Stat.NFS_OK ) {
			System.out.println("XX " + rdr.status);
		    System.exit(0);
		} else {
			List<EntryWrapper> ews = new ArrayList<EntryWrapper>();
			
		    Entry entries = rdr.readdirok.entries;
		    Entry e = entries;
		    if ( rdr.readdirok.entries != null ) {
		        do { 
		        	FAttr attr = getAttr(nfss, p, e.name.value);
		        	
		        	if ( (attr != null && ! e.name.value.startsWith(".") ) || e.name.value.equals("..") ) {
		        		ews.add(new EntryWrapper(e.name, attr));
		        	}
		        	
		            if ( e.nextentry != null ) {
		                e = e.nextentry;
		            } else {
		                e = null;
		            }
		        } while (e != null);
		        return ews;
		    } else {
		    	return ews;
//		        System.out.println("EMPTY Dir or the `count` is too small");
		    }
		}
		return null;

	}
	
	public boolean readFileM(Path p, String filename, EntryWrapper entry) {
		ByteBuffer[] bbs = new ByteBuffer[2];
		byte[] b1 = readFile(nfsc, p, filename, entry);
		byte[] b2 = readFile(nfsc2, p, filename, entry);
		
		bbs[0] = ByteBuffer.wrap(b1);
		bbs[1] = ByteBuffer.wrap(b2);
		
		byte[] filedata = NfsShamir.join(bbs);
		try {
			FileOutputStream output = new FileOutputStream(Utils.convertPath(p, downloadDir, filename).toString());
			output.write(filedata);
			output.close();
		} catch ( IOException e) {
			e.printStackTrace();
		}
		return true;
		
//		byte[] fileData = Encryption.getInstance().decrypt(data.value);
	}

	
	public byte[] readFile(NfsClient nfss, Path p, String filename, EntryWrapper entry) {
		client.nfs.FHandle fh = doo(nfss, Paths.get(p.toString(), filename));
		ReadArgs ra = new ReadArgs();
		ra.file = fh;
		ra.offset = 0;
		ra.count = entry.attribute.size;

		ReadRes rr = null;
		try {
			rr = nfss.NFSPROC_READ_2(ra);
		} catch (OncRpcException | IOException e) {
			e.printStackTrace();
		}
		if ( rr.status != Stat.NFS_OK ) {
		} else {
			NfsData data = rr.read.data;
//			
			return data.value;
			
		}
		return null;
	}
	
	public boolean renameM(Path p, String fromFilename, String toFilename) {
		boolean f = rename(nfsc, p, fromFilename, toFilename);
		boolean g = rename(nfsc2, p, fromFilename, toFilename);
		return f && g;
	}

	public boolean rename(NfsClient nfss, Path p, String fromFilename, String toFilename) {
		client.nfs.FHandle fh = doo(nfss, Paths.get(p.toString()));
		DirOpArgs from = new DirOpArgs();
		from.dir = fh;
		from.name = new FileName(fromFilename);

		DirOpArgs to = new DirOpArgs();
		to.dir = from.dir;
		to.name = new FileName(toFilename);

		RenameArgs ra = new RenameArgs();
		ra.from = from;
		ra.to = to;

		int status = -1;
		try {
			status = nfss.NFSPROC_RENAME_2(ra);
		} catch (OncRpcException | IOException e) {
			e.printStackTrace();
		}
		if ( status != Stat.NFS_OK ) {
		    System.out.println("Not Fine " + status);
		} else {
		    System.out.println("Fine");
			return true;
		  
		}
		return false;
	}

	public boolean deleteFileM(Path p, String filename) {
		boolean f = deleteFile(nfsc, p, filename);
		boolean g = deleteFile(nfsc2, p, filename);
		return f && g;
	}
	
	public boolean deleteFile(NfsClient nfss, Path p, String filename) {
		client.nfs.FHandle fh = doo(nfss, Paths.get(p.toString(), filename));
		DirOpArgs doa = new DirOpArgs();
		doa.dir = root;
		doa.name = new FileName(filename);
		  
		int status = -1;
		try {
			status = nfss.NFSPROC_REMOVE_2(doa);
		} catch (OncRpcException | IOException e) {
			e.printStackTrace();
			return false;
		}
		if ( status != Stat.NFS_OK ) {
		} else {
		 	return true;
		  
		}
		return false;
	}
}


