package hx.io;

import java.nio.charset.Charset;

public class ByteBuffer {

	public static void main(String[] args) {
		String s = "HELLO";
		byte[] bytes = s.getBytes(Charset.forName("US-ASCII"));
		System.out.println(bytes.length);
		
		bytes = s.getBytes(Charset.forName("UTF-8"));
		System.out.println(bytes.length);
		
		for (byte b : bytes)
			System.out.println(b + " " + (char) b);
	}
}
