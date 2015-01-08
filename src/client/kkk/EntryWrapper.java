package client.kkk;

import client.nfs.FAttr;
import client.nfs.FileName;

public class EntryWrapper {
	public FileName filename;
	public FAttr attribute;
	
	public EntryWrapper(FileName fn, FAttr fa) {
		this.filename = fn;
		this.attribute = fa;
	}
	
	public boolean isDir() {
		return false;
	}
	
	public boolean isRegFile() {
		return true;
	}
	
	public String getType() {
		
		String type = "..";
		if ( attribute == null ) {
			return type;
		}
		switch ( attribute.type  ) {
		case 1: type = "Reg"; break;
		case 2: type = "Dir"; break;
		default: type = "Others";
		}
		return type;
	}
}
