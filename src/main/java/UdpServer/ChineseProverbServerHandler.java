package UdpServer;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author qi.liu
 * @create 2018-10-31 17:26
 * @desc 描述:
 **/
public class ChineseProverbServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final String[] dict = {"111", "2222", "3333"};


    private String nextQuote() {
        int quoteId = ThreadLocalRandom.current().nextInt(dict.length);
        return dict[quoteId];

    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        String request = packet.content().toString(CharsetUtil.UTF_8);

        System.out.println(request);

        ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(nextQuote(), CharsetUtil.UTF_8), packet.sender()));


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
