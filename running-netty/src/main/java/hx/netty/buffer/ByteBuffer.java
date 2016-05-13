package hx.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

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
		
		ByteBuf buffer = new PooledByteBufAllocator().buffer(10);
		if (buffer.isDirect()) {
			System.out.println("buffer 1 is NIO DirectBuffer");
		} else {
			buffer.array();
			System.out.println("buffer 1 is heap buffer");
		}
		
		buffer = new PooledByteBufAllocator(true).buffer(10);
		if (buffer.isDirect()) {
			System.out.println("buffer 2 is NIO DirectBuffer");
		} else {
			buffer.array();
			System.out.println("buffer 2 is heap buffer");
		}
		
		buffer = Unpooled.buffer(100);
		buffer = Unpooled.directBuffer(100);
		
		ByteBufUtil.hexDump(buffer);
		ReferenceCountUtil.safeRelease(buffer);
	}
}
