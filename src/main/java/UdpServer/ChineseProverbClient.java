package UdpServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * @author qi.liu
 * @create 2018-10-31 17:32
 * @desc 描述:
 **/
public class ChineseProverbClient {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST,true)
                .handler(new ChineseProverbClientHandler());

        Channel channel = bootstrap.bind(0).sync().channel();

        channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("asd",CharsetUtil.UTF_8),new InetSocketAddress("255.255.255.255",6367))).sync();


        if(!channel.closeFuture().await(15000)){
            System.out.println("超时");
        }



        eventLoopGroup.shutdownGracefully();


    }



}
