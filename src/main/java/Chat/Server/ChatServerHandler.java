package Chat.Server;


import Chat.UserInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Random;
import java.util.RandomAccess;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author qi.liu
 * @create 2018-11-01 15:49
 * @desc 描述:
 **/
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {


    //在线的用户
    private static ConcurrentMap<Channel, UserInfo> onLineUserContext = new ConcurrentHashMap<>();

    //离线的用户
    private static ConcurrentMap<Channel, UserInfo> offLineUserContext = new ConcurrentHashMap<>();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        Channel inComing = ctx.channel();//获得客户端通道
        UserInfo userInfo = null;
        //如果离线包含该人 那么就自动上线
        if (offLineUserContext.containsKey(inComing)) {
            userInfo = offLineUserContext.get(inComing);

            offLineUserContext.remove(inComing);
        } else {
            //系统中没有该人,难么就新建用户
            userInfo = new UserInfo();
            String ipInfo = inComing.remoteAddress().toString();
            userInfo.setIpInfo(ipInfo);
            userInfo.setNickName(ipInfo);
            userInfo.setUserId(ThreadLocalRandom.current().nextLong(100));
        }

        //通知其他客户端有新人进入
        for (Channel channel : onLineUserContext.keySet()) {
            if (channel != inComing) {
                channel.writeAndFlush("[欢迎: " + userInfo.getNickName() + "] 进入聊天室！\n");
            }
        }
        onLineUserContext.put(inComing, userInfo);
        LogPersonNum();
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel outComing = ctx.channel();//获得客户端通道

        UserInfo userInfo = onLineUserContext.get(outComing);


        //通知其他客户端有人离开
        for (Channel channel : onLineUserContext.keySet()) {
            if (channel != outComing) {
                channel.writeAndFlush("[再见: ]" + userInfo.getNickName() + " 离开聊天室！\n");
            }
        }

        onLineUserContext.remove(outComing);
        offLineUserContext.put(outComing, userInfo);
        LogPersonNum();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel inComing = ctx.channel();
        UserInfo userInfo = onLineUserContext.get(inComing);

        if (msg.toString().startsWith("renickname ")) {
            userInfo.setNickName(msg.toString().replace("renickname ", ""));

            onLineUserContext.put(inComing, userInfo);

            inComing.writeAndFlush("昵称修改成功!" + "\n");

            return;
        } else if (msg.toString().startsWith("@")) {

            for (Channel channel : onLineUserContext.keySet()) {
                if (onLineUserContext.get(channel).getNickName().equals(msg.toString().replace("@", ""))) {
                    channel.writeAndFlush("[用户" + userInfo.getNickName() + " 对你说：]" + msg + "\n");
                    return;
                }
            }
        }

        for (Channel channel : onLineUserContext.keySet()) {
            if (channel != inComing) {
                channel.writeAndFlush("[用户" + userInfo.getNickName() + " 说：]" + msg + "\n");
            } else {
                channel.writeAndFlush("[我说：]" + msg + "\n");
            }
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();
        UserInfo userInfo = onLineUserContext.get(inComing);

        System.out.println("[" + userInfo.getNickName() + "]: 在线");

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel inComing = ctx.channel();
        UserInfo userInfo = onLineUserContext.get(inComing);
        onLineUserContext.remove(inComing);
        offLineUserContext.put(inComing, userInfo);
        System.out.println(inComing.remoteAddress() + "通讯异常！");
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("messageReceived" + msg);
    }

    public void LogPersonNum() {
        System.out.println("有" + onLineUserContext.size() + "人在");
    }

}
