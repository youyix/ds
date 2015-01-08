package client.kkk;

import java.nio.file.Path;
import java.nio.file.Paths;

import client.nfs.DirOpRes;
import client.nfs.FAttr;
import client.nfs.Stat;

public class Utils {
	public static String FAttr2String(FAttr attr) {
		String types[] = {"NON", "Reg", "Dir", "BLK", "CHR", "LNK"};
		String str = String.format("[%3s] %8o %d %3d %3d %12d %4d", 
				types[attr.type],
				attr.mode,
				attr.nlink,
				attr.uid,
				attr.gid,
				attr.size,
				attr.blocksize);
		return str;
	}
	
	public static Path convertPath(Path remote, Path local, String filename) {
		Path l = Paths.get(local.toString());
		for ( int i=local.getNameCount(); i<remote.getNameCount(); i++ ) {
			Path pp = remote.getName(i);
			l = l.resolve(pp);
		}
		l = l.resolve(filename);
		
		return l;
	}
}
