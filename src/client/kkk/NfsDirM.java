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
		nfsc = mount(1);
		nfsc2 = mount(2);
	}
	
	public NfsClient mount(int i){
		String host = "";
		String mntPoint = "";
		NfsClient nfss = null;
		client.nfs.FHandle root = null;
		
		if ( i == 1 ) {
			host = this.host;
			mntPoint = remoteDir;
			nfss = nfsc;
		} else {
			host = this.host2;
			mntPoint = remoteDir2;
			nfss = nfsc2;
		}

		InetAddress ia = null;
		try {
			ia = InetAddress.getByName(host);
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
			  this.root = new client.nfs.FHandle(hh);
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
	
	public client.nfs.FHandle doo(int i, Path p) {
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

		client.nfs.FHandle fh = null;
		if ( nfss.hashCode() == nfsc.hashCode() ) {
			fh = root;
		} else {
			fh = root2;
		}
		
		for ( int i1=downloadDir.getNameCount(); i1<p.getNameCount(); i1++ ) {
			Path pp = p.getName(i1);
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
		
		client.nfs.FHandle fh =  doo(i, p);
		
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
	
	public boolean mkdir(Path p, String filename) {
		boolean f = mkDirM(1, p, filename);
		boolean g = mkDirM(2, p, filename);
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
		
		client.nfs.FHandle fh = doo(i, p);
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
		client.nfs.FHandle fh = doo(i, p);
		
		
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

	public FAttr getAttr(Path p, String filename) {
		return getAttrM(1, p, filename);
	}
	
	public FAttr getAttrM(int i, Path p, String filename) {
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

		client.nfs.FHandle fh = doo(i, Paths.get(p.toString(), filename));
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
	
	public List<EntryWrapper> readDir(Path p, String dirname) {
		return readDirM(1, p, dirname);
	}
	
	public List<EntryWrapper> readDirM(int i, Path p, String dirname) {
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

		client.nfs.FHandle fh = doo(i, Paths.get(p.toString(), dirname));
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
		        	FAttr attr = getAttrM(i, p, e.name.value);
		        	
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
		    }
		}
		return null;

	}
	
	public boolean readFile(Path p, String filename, EntryWrapper entry) {
		ByteBuffer[] bbs = new ByteBuffer[2];
		byte[] b1 = readFileM(1, p, filename, entry);
		byte[] b2 = readFileM(2, p, filename, entry);
		
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

	
	public byte[] readFileM(int i, Path p, String filename, EntryWrapper entry) {
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

		client.nfs.FHandle fh = doo(i, Paths.get(p.toString(), filename));
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
	
	public boolean rename(Path p, String fromFilename, String toFilename) {
		boolean f = renameM(1, p, fromFilename, toFilename);
		boolean g = renameM(2, p, fromFilename, toFilename);
		return f && g;
	}

	public boolean renameM(int i, Path p, String fromFilename, String toFilename) {
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

		client.nfs.FHandle fh = doo(i, Paths.get(p.toString()));
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

	public boolean deleteFile(Path p, String filename) {
		boolean f = deleteFileM(1, p, filename);
		boolean g = deleteFileM(2, p, filename);
		return f && g;
	}
	
	public boolean deleteFileM(int i, Path p, String filename) {
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

		client.nfs.FHandle fh = doo(i, Paths.get(p.toString(), filename));
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


