package hx.io.nio;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.Pipe;
import java.nio.channels.Channels;
import java.util.Random;

/**
 * A pipe, in the general sense is a conduit through which data can be passed in a single direction between two entities.
 * 
 * </p>The notion of a pipe has long been familiar to users of Unix operating systems. 
 * Pipes are used on Unix systems to connect the output of one process to the input of another.
 * 
 * </p>While the Pipe class implements a pipe paradigm, but the pipes it creates are intraprocess 
 * (within the JVM process) rather than interprocess (between processes).
 * 
 * @author BenSNW
 */
public class ProcessPipe {
	
	public static void main(String[] argv) throws Exception {
		// wrap a channel around stdout
		WritableByteChannel out = Channels.newChannel(System.out);
		// start worker and get read dateTime of channel
		ReadableByteChannel workerChannel = startWorker(10);
		ByteBuffer buffer = ByteBuffer.allocate(100);

		while (workerChannel.read(buffer) >= 0) {
			buffer.flip();
			out.write(buffer);
			buffer.clear();
		}
	}

	// This method could as easily return a SocketChannel or
	// FileChannel instance.
	private static ReadableByteChannel startWorker(int reps) throws Exception {
		Pipe pipe = Pipe.open();
		Worker worker = new Worker(pipe.sink(), reps);

		worker.start();

		return (pipe.source());
	}

	// -----------------------------------------------------------------

	/**
	 * A worker thread object which writes data down a channel. Note: this
	 * object knows nothing about Pipe, uses only a generic WritableByteChannel.
	 */
	private static class Worker extends Thread {
		WritableByteChannel channel;
		private int reps;

		Worker(WritableByteChannel channel, int reps) {
			this.channel = channel;
			this.reps = reps;
		}

		// thread execution begins here
		public void run() {
			ByteBuffer buffer = ByteBuffer.allocate(100);

			try {
				for (int i = 0; i < this.reps; i++) {
					doSomeWork(buffer);

					// channel may not take it all at once
					while (channel.write(buffer) > 0) {
						// empty
					}
				}

				this.channel.close();
			} catch (Exception e) {
				// easy way out, this is demo code
				e.printStackTrace();
			}
		}

		private String[] products = { "No good deed goes unpunished",
				"To be, or what?", "No matter where you go, there you are",
				"Just say \"Yo\"", "My karma ran over my dogma" };

		private Random rand = new Random();

		private void doSomeWork(ByteBuffer buffer) throws Exception {
			int product = rand.nextInt(products.length);

			buffer.clear();
			buffer.put(products[product].getBytes("US-ASCII"));
			buffer.put("\r\n".getBytes("US-ASCII"));
			buffer.flip();
		}
	}
}
