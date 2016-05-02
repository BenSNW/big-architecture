package hx.io.netty.http;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;

public final class HttpServer {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(
    		System.getProperty("port", SSL? "8443" : "8081"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
//           .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer<SocketChannel>() {
            	 @Override
            	    public void initChannel(SocketChannel ch) {
            	        CorsConfig corsConfig = CorsConfig.withAnyOrigin().shortCurcuit().build();
            	        ChannelPipeline pipeline = ch.pipeline();
            	        if (sslCtx != null) {
            	            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
            	        }
            	        
            	        pipeline.addLast(new HttpResponseEncoder());
            	        pipeline.addLast(new HttpRequestDecoder());
            	        pipeline.addLast(new HttpObjectAggregator(65536));
            	        pipeline.addLast(new ChunkedWriteHandler());
            	        pipeline.addLast(new CorsHandler(corsConfig));
            	        pipeline.addLast(new ChannelInboundHandlerAdapter() {
            	            
            	        	private final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd' };

            	            @Override
            	            public void channelRead(ChannelHandlerContext ctx, Object msg) {
            	                if (msg instanceof HttpRequest) {
            	                    HttpRequest req = (HttpRequest) msg;

            	                    if (HttpHeaders.is100ContinueExpected(req)) {
            	                        ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            	                    }
            	                    boolean keepAlive = HttpHeaders.isKeepAlive(req);
            	                    FullHttpResponse response = new DefaultFullHttpResponse(
            	                    		HTTP_1_1, OK, Unpooled.wrappedBuffer(CONTENT));
            	                    response.headers().set(CONTENT_TYPE, "text/plain");
            	                    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

            	                    if (!keepAlive) {
            	                        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            	                    } else {
            	                        response.headers().set(CONNECTION, Values.KEEP_ALIVE);
            	                        ctx.write(response);
            	                    }
            	                }
            	            }
            	            
            	            @Override
            	            public void channelReadComplete(ChannelHandlerContext ctx) {
            	                ctx.flush();
            	            }

            	            @Override
            	            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            	                cause.printStackTrace();
            	                ctx.close();
            	            }
            	        });
            	    }
             });

            b.bind(PORT).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}