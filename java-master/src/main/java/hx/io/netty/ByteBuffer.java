package hx.io.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;
import java.util.stream.IntStream;

public class ByteBuffer {

	public static void main(String[] args) {
		
		String s = "HELLO";
		byte[] bytes = s.getBytes(Charset.forName("US-ASCII"));
		System.out.println(bytes.length);
		
		bytes = s.getBytes(Charset.forName("UTF-8"));
		System.out.println(bytes.length);
		
		for (byte b : bytes)
			System.out.println(b + " " + (char) b);
		
		java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(1024);
		buffer.put(bytes).putDouble(1.5).put(10, (byte)5);
		if (buffer.hasArray()) {
			System.out.println(buffer.arrayOffset() + new String(buffer.array()));
			final byte[] bufBytes = buffer.array();
			IntStream.range(0, buffer.position()).map(index->bufBytes[index])
				.forEach(value -> System.out.print(
						String.format("0x%02x", (byte)value) + " "));
			System.out.println();
			IntStream.range(0, buffer.position()).map(index->bufBytes[index])
				.forEach(value -> System.out.print(
						String.format("0x%02x", value) + " "));
			System.out.println();
		}
		
		buffer.compact().clear();
		buffer = java.nio.ByteBuffer.allocateDirect(1024);
		if (buffer.hasArray())
			System.out.println(buffer.arrayOffset() + new String(buffer.array()));
		if (buffer.isDirect()) {
			System.out.println(buffer.isReadOnly());
//			buffer.array(); // UnsupportedOperationException
		}

		// UnpooledHeapByteBuf, public access
		System.out.println(Unpooled.wrappedBuffer(s.getBytes()));
				
		// UnpooledUnsafeHeapByteBuf, package access
		ByteBuf buf = Unpooled.copyFloat(1.5f, 3.8f);
		System.out.println(buf.toString());
		
		// SimpleLeakAwareByteBuf(UnpooledUnsafeDirectByteBuf)
		System.out.println(Unpooled.directBuffer().toString());

		// ReadOnlyByteBuf, public access
		System.out.println(Unpooled.unmodifiableBuffer(buf));
		
		// CompositeByteBuf public access
		System.out.println(Unpooled.compositeBuffer());
		
	}
}
