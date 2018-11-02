package HttpFileServer;

import com.sun.security.ntlm.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author qi.liu
 * @create 2018-10-31 14:44
 * @desc 描述:
 **/
public class HttpFileServer {


    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //它负责把字节解码成Http请求
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            //它负责把多个HttpMessage组装成一个完整的Http请求或者响应。到底是组装成请求还是响应，则取决于它所处理的内容是请求的内容，还是响应的内容。这其实可以通过Inbound和Outbound来判断，对于Server端而言，在Inbound 端接收请求，在Outbound端返回响应。
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            //当Server处理完消息后，需要向Client发送响应。那么需要把响应编码成字节，再发送出去。故添加
                            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            //该通道处理器主要是为了处理大文件传输的情形。大文件传输时，需要复杂的状态管理，而ChunkedWriteHandler实现这个功能。
                            ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            //自定义的通道处理器，其目的是实现文件服务器的业务逻辑。
                            ch.pipeline().addLast("fileServerHandler", new HttpFileServerHandler("/"));


                        }
                    });
            ChannelFuture future = bootstrap.bind("192.168.53.116", 6366).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }

}
