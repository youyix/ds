/*
 * Automatically generated by jrpcgen 1.0.7 on 1/4/15 12:30 PM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package client.nfs;
import org.acplt.oncrpc.*;
import java.io.IOException;

public class WriteArgs implements XdrAble {
    public FHandle file;
    public int beginoffset;
    public int offset;
    public int totalcount;
    public NfsData data;

    public WriteArgs() {
    }

    public WriteArgs(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        file.xdrEncode(xdr);
        xdr.xdrEncodeInt(beginoffset);
        xdr.xdrEncodeInt(offset);
        xdr.xdrEncodeInt(totalcount);
        data.xdrEncode(xdr);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        file = new FHandle(xdr);
        beginoffset = xdr.xdrDecodeInt();
        offset = xdr.xdrDecodeInt();
        totalcount = xdr.xdrDecodeInt();
        data = new NfsData(xdr);
    }

}
// End of writeargs.java
