package hx.spark.io.nio;

import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.Channels;
import java.io.FileInputStream;

/**
 * Channel-to-channel transfers, eliminating the need to pass data through
 * an intermediate buffer, can potentiallybe extremely fast especially
 * when the underlying operating system provides native support.
 * Some operating systems can perform direct transfer without ever
 * passing the data through user space.
 *
 * @author BenSNW
 */
public class ChannelTransfer {
	
	public static void main(String[] argv) throws Exception {
		if (argv.length == 0) {
			System.err.println("Usage: filename ...");
			return;
		}

		catFiles(Channels.newChannel(System.out), argv);
	}

	// Concatenate the content of each of the named files to
	// the given channel. A very dumb version of 'cat'.
	private static void catFiles(WritableByteChannel target, String[] files)
			throws Exception {
		for (int i = 0; i < files.length; i++) {
			FileInputStream fis = new FileInputStream(files[i]);
			FileChannel channel = fis.getChannel();

			channel.transferTo(0, channel.size(), target);

			channel.close();
			fis.close();
		}
	}
}
