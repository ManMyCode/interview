package io.nio.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class NettyServer {
    public void start(int port) throws Exception {
        // 配置NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            // 服务器辅助启动类配置
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChildChannelHandler());
            // 绑定端口 同步等待绑定成功
            ChannelFuture f = b.bind(port).sync();
            // 等到服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅释放线程资源
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * 网络事件处理器
     */
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            // 添加Jboss的序列化，编解码工具
            ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
            ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
            // 处理网络IO
            ch.pipeline().addLast(new ServerHandler());
        }
    }

    public static void main(String[] args) throws Exception {
        new NettyServer().start(8765);
    }
}
