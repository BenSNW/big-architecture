package hx.spark.io.nio;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * Create a temp file with holes (0x00) in it.
 * 
 * @author BenSNW
 */
public class RandomFileWrite {
	
	public static void main(String[] argv) throws IOException {
		
		// create a temp file, open for writing and get a FileChannel
		File temp = File.createTempFile("holy", null);
		RandomAccessFile file = new RandomAccessFile(temp, "rw");
		FileChannel channel = file.getChannel();
		// create a working buffer
		ByteBuffer byteBuffer = ByteBuffer.allocate(64);

		putData(0, byteBuffer, channel);
		putData(1024, byteBuffer, channel);
		putData(512, byteBuffer, channel);

		// Size will report the largest position written, but
		// there are two holes in this file. This file will
		// not consume 5MB on disk (unless the filesystem is
		// extremely brain-damaged).
		System.out.println("Wrote temp file '" + temp.getPath() +
				"', size=" + channel.size());

		channel.close();
		file.close();
	}

	private static void putData(int position, ByteBuffer buffer,
			FileChannel channel) throws IOException {
		String string = "location " + position;

		buffer.clear();
		buffer.put(string.getBytes("US-ASCII"));
		buffer.flip();

		channel.position(position);
		channel.write(buffer);
	}
}
