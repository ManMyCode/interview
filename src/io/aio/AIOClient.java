package io.aio;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Random;
import java.util.concurrent.Future;

public class AIOClient {

    public static void main(String[] args){
        new AioClientThread().start();
        new AioClientThread().start();
        new AioClientThread().start();
    }

}

class AioClientThread extends Thread{
    private static final int AIO_PORT=6987;
    private static final String SERVER_IP = "127.0.0.1";
    @Override
    public void run() {
        super.run();
        try {
            //获取AsynchronousSocketChannel的实例,这里可以跟服务器端一样传入一个AsynchronousChannelGroup的对象
            AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
            //连接服务器
            Future<Void> future = socketChannel.connect(new InetSocketAddress(SERVER_IP,AIO_PORT));
            //注意，这个get是一个阻塞的方法，只有当连接上了之后才会往下走
            //如果不执行get方法，等程序没有连接成功时进行发送数据的操作会发生错误
            future.get();
            //拼接发送给服务器的数据
            Random rom = new Random();
            StringBuffer writeBuf = new StringBuffer();
            writeBuf.append("this is client").append(rom.nextInt(1000));
            System.out.println(writeBuf.toString());
            ByteBuffer byteBuffer = ByteBuffer.wrap(writeBuf.toString().getBytes());
            //发送数据
            socketChannel.write(byteBuffer);
            //读取服务器的返回的数据
            read(socketChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read(AsynchronousSocketChannel socketChannel){
        ByteBuffer byteBufer = ByteBuffer.allocate(10);
        try {
            StringBuffer strBuf = new StringBuffer("客户端：");
            boolean hasData = true;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while(hasData){
                socketChannel.read(byteBufer).get();
                if(byteBufer.capacity()>byteBufer.position()){//当容器大小大于容器里面的数量的时候，认为读取完毕
                    hasData = false;
                }
                byteBufer.flip();
                byte[] buf = new byte[byteBufer.remaining()];
                byteBufer.get(buf);
                if(hasData){
                    System.out.println("数据没有读取完毕继续读取");
                }else{
                    System.out.println("数据读取完毕");
                }
                out.write(buf);
                byteBufer.clear();
            }
            strBuf.append(new String(out.toByteArray(),"UTF-8"));
            System.out.println(strBuf);
            read(socketChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
