package hx.spark.io.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

public class NioDatagramServer {

	public static void main(String[] args) {
		// Bootstrap is used in clients or in applications that use a connectionless protocol
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(new NioEventLoopGroup())
				.channel(NioDatagramChannel.class)
				.handler(new SimpleChannelInboundHandler<DatagramPacket>() {
					@Override
					protected void channelRead0(ChannelHandlerContext ctx,
							DatagramPacket msg) throws Exception {
						System.out.println("Received data: " + msg);
					}
				});
		ChannelFuture future = bootstrap.bind(new InetSocketAddress(0));
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture channelFuture)
					throws Exception {
				if (channelFuture.isSuccess()) {
					System.out.println("Channel bound");
				} else {
					System.err.println("Bind attempt failed");
				}
			}
		});
	}
}
