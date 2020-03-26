package io.nio.netty;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends ChannelHandlerAdapter {

    // 用于获取客户端发送的信息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 用于获取客户端发来的数据信息
        Message body = (Message) msg;
        System.out.println("Server接受的客户端的信息 :" + body.toString());

        // 写数据给客户端
        Message response = new Message("欢迎您，与服务端连接成功");
        // 当服务端完成写操作后，关闭与客户端的连接
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}