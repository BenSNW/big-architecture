package hx.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class NioSocketClient {

	public static void main(String[] args) {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		// Sets the EventLoopGroup that provides EventLoops for
		// processing Channel events
		bootstrap.group(group)
			.channel(NioSocketChannel.class)
			.handler(new SimpleChannelInboundHandler<ByteBuf>() {
				@Override
				protected void channelRead0(ChannelHandlerContext ctx,
						ByteBuf msg) throws Exception {
					System.out.println("Received data: " + msg);
				}
			});
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(
				"localhost", 8080));
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture channelFuture)
					throws Exception {
				if (channelFuture.isSuccess()) {
					System.out.println("Connection established");
				} else {
					System.err.println("Connection attempt failed");
					channelFuture.cause().printStackTrace();
				}
			}
		});
		
	}
}
