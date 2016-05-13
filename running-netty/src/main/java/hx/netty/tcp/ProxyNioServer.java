package hx.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;

public class ProxyNioServer {

	public static void main(String[] args) {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(new NioEventLoopGroup())
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInboundHandlerAdapter() {
					ChannelFuture connectFuture = null;

					@Override
					public void channelActive(ChannelHandlerContext ctx)
							throws Exception {
						Bootstrap bootstrap = new Bootstrap();
						bootstrap.channel(NioSocketChannel.class).handler(
								new SimpleChannelInboundHandler<ByteBuf>() {
									@Override
									protected void channelRead0(
											ChannelHandlerContext ctx,
											ByteBuf in) throws Exception {
										System.out.println("Received thied-party data");
									}
								});
						bootstrap.group(ctx.channel().eventLoop());
						connectFuture = bootstrap
								.connect(new InetSocketAddress(
										"www.manning.com", 80));
					}

					@Override
					public void channelRead(ChannelHandlerContext ctx, Object msg) {
						if (connectFuture.isDone()) {
							System.out.println("Received data after proxy is done");
							ByteBuf in = (ByteBuf) msg;
							try {
						        while (in.isReadable()) { 
						            System.out.print((char) in.readByte());
						            System.out.flush();
						        }
						    } finally {
						        ReferenceCountUtil.release(msg); 
						    }
						}				    
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
