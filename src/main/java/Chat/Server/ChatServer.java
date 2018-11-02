package Chat.Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author qi.liu
 * @create 2018-11-01 15:35
 * @desc 描述:
 **/
public class ChatServer {

    public static void main(String[] args) {
        //接受客户端的链接
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        //处理相关IO 读写操作
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("客户端连接：" + ch.remoteAddress());
                            //用户定义的ChannelInitailizer加入到这个channel的pipeline上面去，这个handler就可以用于处理当前这个channel上面的一些事件
                            ChannelPipeline pipeline = ch.pipeline();
                            //ChannelPipeline类似于一个管道，管道中存放的是一系列对读取数据进行业务操作的ChannelHandler。

                            /**
                             * 发送的数据在管道里是无缝流动的，在数据量很大时，为了分割数据，采用以下几种方法
                             * 定长方法
                             * 固定分隔符
                             * 将消息分成消息体和消息头，在消息头中用一个数组说明消息体的长度
                             */
                            pipeline.addLast("frame",new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                            pipeline.addLast("decode",new StringDecoder());//解码器
                            pipeline.addLast("encode",new StringEncoder());
                            pipeline.addLast("handler",new ChatServerHandler());

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
