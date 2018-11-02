package FileService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @author qi.liu
 * @create 2018-11-01 11:13
 * @desc 描述:
 **/
public class FileServerHandler extends SimpleChannelInboundHandler<String> {

    private static final String CR  =System.getProperty("line.separator");

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        File file = new File(msg);

        if(file.exists()){
            if(!file.isFile()){
                ctx.writeAndFlush("not a file :     "+file+CR);
                return;
            }
            ctx.write(file.length()+CR);
            RandomAccessFile randomAccessFile = new RandomAccessFile(msg,"r");

            FileRegion region = new DefaultFileRegion(randomAccessFile.getChannel(),0,randomAccessFile.length());
            ctx.write(region);

            ctx.writeAndFlush(CR);
            randomAccessFile.close();


        }else{
            ctx.writeAndFlush("file not fount "+ CR);


        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
