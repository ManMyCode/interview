package io.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 基于BIO的socket通信,服务端
 */
public class BIOServer {
    private final static int BIO_PORT = 8888;
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try{
            //线程池
            ExecutorService executor = Executors.newFixedThreadPool(10);
            serverSocket = new ServerSocket(BIO_PORT);
            System.out.println("启动服务器，监听端口"+BIO_PORT);
            while(!Thread.currentThread().isInterrupted()){
                Socket socket = serverSocket.accept();
                System.out.println("客户端[" + socket.getPort() + "]已连接");
                executor.submit(new BIOHandler(socket));
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (serverSocket != null){
                try {
                    serverSocket.close();
                    System.out.println("关闭ServerSocket");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
class BIOHandler implements Runnable{
    private Socket socket;
    private static final  String QUIT = "quit";
    public BIOHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
//            while (Thread.currentThread().isInterrupted()&&!socket.isClosed()){
            while (!Thread.currentThread().isInterrupted()&&!socket.isClosed()){
                //创建IO流
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                String msg = null;
                while ((msg = bufferedReader.readLine()) != null) {
                    // 读取客户端发送的消息,当对方关闭时返回null
                    System.out.println("客户端["+socket.getPort()+"]："+ msg);
                    //回复客户发送的消息
                    bufferedWriter.write("服务器：" + msg + "\n");
                    bufferedWriter.flush(); //保证缓冲区的数据发送出去
                    //查看客户端是否退出
                    if(QUIT.equals(msg)){
                        System.out.println("客户端["+socket.getPort()+"]已退出");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
