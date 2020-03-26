package io.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AIOServer {
    private static final int AIO_PORT=6987;
    private static final String SERVER_IP = "127.0.0.1";
    public static AsynchronousServerSocketChannel serverSocketChannel;//第二次接收的时候会使用到，因此作为一个属性

    public static void main(String[] args) {
        //新建一个线程池，使得aio中的操作都使用这个线程池中的线程，而且，还传入一个ThreadFactory对象，自己去定义线程
        ExecutorService executorService = new ThreadPoolExecutor(10, 10, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        try {
            //创建使用的公共线程池资源
            AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withThreadPool(executorService);
            //创建AsynchronousServerSocketChannel对象
            if(serverSocketChannel==null){
                serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
            }
            //设定一些参数
            //接收缓冲区的大小
            serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 64 * 1024);
            //是否重用本地地址
            serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            //绑定监听的端口号
            serverSocketChannel.bind(new InetSocketAddress(AIO_PORT));
            //开始接收请求，第一个参数是根据自己的需求出入对应的对象
            //第二个参数CompletionHandler的子对象
            serverSocketChannel.accept(new AIOServer(), new MyCompletion());
            System.out.println("服务启动...");
            //保持程序不停止
            waitRevicer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void waitRevicer(){
        new Thread(new Runnable() {

            @Override
            public void run() {
                while(true){
                    System.out.println("保持程序运行不停止...");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}

class MyCompletion implements CompletionHandler<AsynchronousSocketChannel,AIOServer> {

    @Override
    public void completed(AsynchronousSocketChannel result, AIOServer attachment) {
        System.out.println("completed");
        //用来缓存数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            //读取客户端数据，存放到ByteBuffer对象中
            result.read(byteBuffer).get();
            //重新设置position和limit的值，为读取值做准备
            byteBuffer.flip();
            //根据值的长度创建对象，并将ByteBuffer中的数据存入，打印输出
            byte[] input = new byte[byteBuffer.remaining()];
            byteBuffer.get(input);
            System.out.println(new String(input));
            //重新设置position的值，并将客户端发送的值返回给客户端
            byteBuffer.position(0);
            result.write(byteBuffer);
//          String sayHello = new String("你好啊，客户端"+result.getRemoteAddress().toString());
//          System.out.println("sayHello:"+sayHello);
//          result.write(ByteBuffer.wrap(sayHello.getBytes("UTF-8")));
            attachment.serverSocketChannel.accept(attachment, this);//不重新设置接收的话就只能接收一次了
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void failed(Throwable exc, AIOServer attachment) {
        System.out.println("failed");
        System.out.println(attachment);
        System.out.println(exc);

    }

}
