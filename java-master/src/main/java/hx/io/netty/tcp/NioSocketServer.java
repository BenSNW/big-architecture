package hx.io.netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;

public class NioSocketServer {

	public static void main(String[] args){
	    EventLoopGroup bossGroup = new NioEventLoopGroup(2);
	    EventLoopGroup workerGroup = new NioEventLoopGroup(3);
	    try {
	        ServerBootstrap serverBootstrap = new ServerBootstrap();
	        serverBootstrap.group(bossGroup,workerGroup)
	            .channel(NioServerSocketChannel.class)
	            .childHandler(new ChannelInitializer<SocketChannel>() {
	                @Override
	                protected void initChannel(SocketChannel ch) throws Exception {
	                	ChannelPipeline pipeline = ch.pipeline();
	                	pipeline.addLast("default handler", new ServerChannelHandler());
	                	pipeline.addAfter("default handler", "time echo handler",
                			new ChannelInboundHandlerAdapter() {
		                		@Override
		                        public void channelActive(final ChannelHandlerContext ctx) { // (1)
		                            final ByteBuf time = ctx.alloc().buffer(4); // (2)
		                            time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
	
		                            final ChannelFuture f = ctx.writeAndFlush(time); // (3)
		                            f.addListener(new ChannelFutureListener() {
		                                @Override
		                                public void operationComplete(ChannelFuture future) {
		                                    assert f == future;
		                                    ctx.close();
		                                }
		                            }); 
		                        }                    	                	
	                	});
	                }
	            });
	        ChannelFuture f = serverBootstrap.bind(8080).sync();
	        f.channel().closeFuture().sync();
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }finally {  
	        workerGroup.shutdownGracefully();  
	        bossGroup.shutdownGracefully();  
	    }  
	}
	
	static class ServerChannelHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
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
		
		@Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
	        // Close the connection when an exception is raised.
	        cause.printStackTrace();
	        ctx.close();
	    }
		
	}

}
