package client.sss;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class NfsShamir {
	
	public static ByteBuffer[] split(int k, byte[] data) {
		int n = data.length / k;
		int remain = data.length % k;
		ByteBuffer[] bbs = new ByteBuffer[k];
		for ( int i=0; i<k; i++ ) {
			int cap = i==k-1 ? n+remain : n;
			bbs[i] = ByteBuffer.allocate(cap); 
			
		}
		for ( int i=0; i<k; i++ ) {
			int start = i * n;
			int end = i * n + n;
			end = end > data.length ? data.length : end;
			end += i==k-1 ? remain : 0;
			for ( int j=start; j<end; j++ ) {
				bbs[i].put(data[j]);
			}
		}
		return bbs;
	}
	
	public static byte[] join(ByteBuffer[] bbs) {
		int k = bbs.length;
		int size = 0;
		for ( ByteBuffer bb: bbs ) {
			size += bb.capacity();
		}
		byte[] data = new byte[size]; 
		int p=0;
		for ( int i=0; i<k; i++ ) {
			byte[] bb = bbs[i].array();
			for( int j=0; j<bb.length; j++  ) {
				data[p++] = bb[j];
			}
		}
		
		return data;
	}

	public static void main(String[] args) {
		byte[] data = {-1, 1, 0, 2, 100, 90, 89};
		System.out.println(Arrays.toString(data));
		ByteBuffer[] bbs = NfsShamir.split(3, data);
		for ( ByteBuffer bb: bbs ) {
			System.out.println(Arrays.toString(bb.array()));
		}
		
		byte[] d = NfsShamir.join(bbs);
		System.out.println(Arrays.toString(d));
	}

}
