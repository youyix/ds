/*
 * Automatically generated by jrpcgen 1.0.7 on 1/4/15 12:30 PM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package client.nfs;
import org.acplt.oncrpc.*;
import java.io.IOException;

public class TimeVal implements XdrAble {
    public int seconds;
    public int useconds;

    public TimeVal() {
    }

    public TimeVal(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(seconds);
        xdr.xdrEncodeInt(useconds);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        seconds = xdr.xdrDecodeInt();
        useconds = xdr.xdrDecodeInt();
    }

}
// End of timeval.java
