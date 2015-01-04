/*
 * Automatically generated by jrpcgen 1.0.7 on 1/4/15 12:29 PM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package client.mount;
import org.acplt.oncrpc.*;
import java.io.IOException;

public class fhstatus implements XdrAble {
    public int status;
    public fhandle directory;

    public fhstatus() {
    }

    public fhstatus(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(status);
        switch ( status ) {
        case 0:
            directory.xdrEncode(xdr);
            break;
        default:
            break;
        }
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        status = xdr.xdrDecodeInt();
        switch ( status ) {
        case 0:
            directory = new fhandle(xdr);
            break;
        default:
            break;
        }
    }

}
// End of fhstatus.java
