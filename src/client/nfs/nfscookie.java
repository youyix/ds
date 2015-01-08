/*
 * Automatically generated by jrpcgen 1.0.7 on 1/4/15 12:30 PM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package client.nfs;
import org.acplt.oncrpc.*;
import java.io.IOException;

public class NfsCookie implements XdrAble {

    public byte [] value;

    public NfsCookie() {
    }

    public NfsCookie(byte [] value) {
        this.value = value;
    }

    public NfsCookie(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeOpaque(value, Nfs.COOKIESIZE);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        value = xdr.xdrDecodeOpaque(Nfs.COOKIESIZE);
    }

}
// End of nfscookie.java
