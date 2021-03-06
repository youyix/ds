/*
 * Automatically generated by jrpcgen 1.0.7 on 1/4/15 12:30 PM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package client.nfs;
import org.acplt.oncrpc.*;
import java.io.IOException;

public class SAttr implements XdrAble {
    public int mode;
    public int uid;
    public int gid;
    public int size;
    public TimeVal atime;
    public TimeVal mtime;

    public SAttr() {
    }

    public SAttr(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(mode);
        xdr.xdrEncodeInt(uid);
        xdr.xdrEncodeInt(gid);
        xdr.xdrEncodeInt(size);
        atime.xdrEncode(xdr);
        mtime.xdrEncode(xdr);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        mode = xdr.xdrDecodeInt();
        uid = xdr.xdrDecodeInt();
        gid = xdr.xdrDecodeInt();
        size = xdr.xdrDecodeInt();
        atime = new TimeVal(xdr);
        mtime = new TimeVal(xdr);
    }

}
// End of sattr.java
