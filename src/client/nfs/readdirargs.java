/*
 * Automatically generated by jrpcgen 1.0.7 on 1/4/15 12:30 PM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package client.nfs;
import org.acplt.oncrpc.*;
import java.io.IOException;

public class ReadDirArgs implements XdrAble {
    public FHandle dir;
    public NfsCookie cookie;
    public int count;

    public ReadDirArgs() {
    }

    public ReadDirArgs(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        dir.xdrEncode(xdr);
        cookie.xdrEncode(xdr);
        xdr.xdrEncodeInt(count);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        dir = new FHandle(xdr);
        cookie = new NfsCookie(xdr);
        count = xdr.xdrDecodeInt();
    }

}
// End of readdirargs.java
