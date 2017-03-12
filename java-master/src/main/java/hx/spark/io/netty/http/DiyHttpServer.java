package hx.spark.io.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public final class DiyHttpServer {

	public static void main(String[] args) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap boot = new ServerBootstrap();
			boot.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				// .handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) {
						ch.pipeline().addLast(new HttpRequestHandler());
					}
				});
			boot.bind(8081).sync().channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}

class HttpRequestHandler extends SimpleChannelInboundHandler<ByteBuf> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg)
			throws Exception {
		
		if (msg.isDirect()) {
			System.out.println("NIO DirectBuffer");
		} 
		
		if (!msg.hasArray()) { // this works the same as the above check
			System.out.println("This buffer is not backed by an array");
			byte[] bytes = new byte[msg.readableBytes()];
			msg.getBytes(msg.readerIndex(), bytes);
			for (byte b : bytes) {
				switch (b) {
				case 10:
					System.out.println("\\n"); // line feed
					break;
				case 13:
					System.out.print("\\r");  // carriage return
					break;
				default:
					System.out.print((char)b);
				}
			}				
		} else {
			for (byte b : msg.array())
				System.out.println(b + " " + (char) b);
		}
		
		String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain; charset=UTF-8\r\n"
				+ "Content-Encoding: UTF-8\r\n\r\nHello World!";
		ctx.write(response).addListener(ChannelFutureListener.CLOSE);;
	}

}