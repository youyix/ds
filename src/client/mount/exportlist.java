/*
 * Automatically generated by jrpcgen 1.0.7 on 1/4/15 12:29 PM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package client.mount;
import org.acplt.oncrpc.*;
import java.io.IOException;

public class ExportList implements XdrAble {
    public DirPath filesys;
    public Groups groups;
    public ExportList next;

    public ExportList() {
    }

    public ExportList(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        filesys.xdrEncode(xdr);
        groups.xdrEncode(xdr);
        next.xdrEncode(xdr);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        filesys = new DirPath(xdr);
        groups = new Groups(xdr);
        next = new ExportList(xdr);
    }

}
// End of exportlist.java
