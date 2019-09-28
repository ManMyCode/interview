# TCP包报文格式
![TCP报文格式](images/TCP报文格式.png)

1. 序号Seq。占32位，用来标识从TCP源端向目的端发送的字节流，发起方发送数据时对此进行标记。 
2. 确认序号Ack。占32位，对已收到的包进行确认。Ack=Seq+1，只有ACK标志位为1时，确认序号字段才有效。 
3. 标志位：共6个，即URG、ACK、PSH、RST、SYN、FIN： 
（A）URG：紧急指针（urgent pointer）有效。 
（B）ACK：确认序号有效。 
（C）PSH：接收方应该尽快将这个报文交给应用层。 
（D）RST：重置连接。 
（E）SYN：发起一个新连接。 
（F）FIN：释放一个连接。

# 三次握手
![三次握手](images/三次握手.png)

第一步：client发出连接请求，client就进入了SYN_SENT状态，将连接报文的SYN标志位置1，同时假设序号seq=J； 
第二步：server收到连接请求后，进入SYN_RCVD状态，发出响应报文，响应报文的SYN标志位置1，ACK标志位置1，ack序列号等于收到的报文seq+1，也就是J+1.假设此报文的序列号seq=K； 
第三步：client收到来自server的响应报文后即进入ESTABLISHED状态，并发送确认报文，表示我收到了你的回应，我已经进入了连接状态。此确认报文的ACK状态位置1，ack为K+1。server收到后进入ESTABLISHED状态，连接已经建立。

# 四次挥手
![四次挥手](images/四次挥手.png)

TCP连接完成后，client和server就可以互相发送数据了，TCP连接是全双工的。在断开连接时，两端也要分别断开。 
1. client想要断开连接时，发送报文FIN状态为为1，假设序号seq=M。client就进入了FIN_WAIT_1状态； 
2. server收到后发送确认报文后进入了CLOSE_WAIT状态，确认报文的ack状态位置1，序号seq=M+1； 
3. client收到确认报文后就进入了FIN_WAIT_2状态。此时就处于了半连接状态。client主动发送断开连接的请求后肯定就不会也不能再向server发送数据了。但是此时server还可以向client发送数据； 
4. server发送完所有的数据后就要发送断开连接的报文了，FIN状态位置1，假设序号seq=K。server就进入了LAST_ACK状态等待来自client的确认报文； 
5. client回复确认报文，ACK状态位置1，ack=K+1.然后进入TIME_WAIT状态，等待一定的时间后就完全断开了。server收到确认报文后也进入了CLOSED状态，断开了与client的连接。