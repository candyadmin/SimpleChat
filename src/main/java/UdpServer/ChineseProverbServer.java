package UdpServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * @author qi.liu
 * @create 2018-10-31 17:22
 * @desc 描述:
 **/
public class ChineseProverbServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChineseProverbServerHandler());
        ChannelFuture channelFuture = bootstrap.bind(6367).sync().channel().closeFuture().await();


        eventLoopGroup.shutdownGracefully();


    }
}
