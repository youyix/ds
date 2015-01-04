/*
 * Automatically generated by jrpcgen 1.0.7 on 1/4/15 12:30 PM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package client.nfs;
import org.acplt.oncrpc.*;
import java.io.IOException;

public class entry implements XdrAble {
    public int fileid;
    public filename name;
    public nfscookie cookie;
    public entry nextentry;

    public entry() {
    }

    public entry(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        entry $this = this;
        do {
            xdr.xdrEncodeInt($this.fileid);
            $this.name.xdrEncode(xdr);
            $this.cookie.xdrEncode(xdr);
            $this = $this.nextentry;
            xdr.xdrEncodeBoolean($this != null);
        } while ( $this != null );
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        entry $this = this;
        entry $next;
        do {
            $this.fileid = xdr.xdrDecodeInt();
            $this.name = new filename(xdr);
            $this.cookie = new nfscookie(xdr);
            $next = xdr.xdrDecodeBoolean() ? new entry() : null;
            $this.nextentry = $next;
            $this = $next;
        } while ( $this != null );
    }

}
// End of entry.java
