package com.netty.server;

import com.netty.handler.EchoServerHander;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @Description:
 * @Auther: HXM
 * @Date: 2018/8/17 20:21
 */
public class EchoServer {

    private static int port = 8080;

    public EchoServer(int port){
        this.port = port;
    }

    public static void main(String[] args) throws Exception{

        new EchoServer(port).start();   //呼叫服务器的 start() 方法
    }

    /*创建 ServerBootstrap 实例来引导服务器并随后绑定
    创建并分配一个 NioEventLoopGroup 实例来处理事件的处理，如接受新的连接和读/写数据。
    写一个 echo 服务器指定本地 InetSocketAddress 给服务器绑定
    通过 EchoServerHandler 实例给每一个新的 Channel 初始化
    最后调用 ServerBootstrap.bind() 绑定服务器*/
    public void start() throws  Exception{
        NioEventLoopGroup group = new NioEventLoopGroup();  //创建 EventLoopGroup, NioEventLoopGroup可以理解为一个线程池，
        // 内部维护了一组线程，每个线程负责处理多个Channel上的事件，而一个Channel只对应于一个线程，这样可以回避多线程下的数据同步问题。
        // Netty 通过触发事件从应用程序中抽象出 Selector，从而避免手写调度代码。EventLoop 分配
        //给每个 Channel 来处理所有的事件，包括注册感兴趣的事件,调度事件到 ChannelHandler,安排进一步行动
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)  //创建 ServerBootstrap
                    .channel(NioServerSocketChannel.class)  //指定使用 NIO 的传输 Channel
                    .localAddress(new InetSocketAddress(port))  //设置 socket 地址使用所选的端口
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel){    //添加 EchoServerHandler 到 Channel 的 ChannelPipeline
                            socketChannel.pipeline().addLast(new EchoServerHander());
                        }
                    });
            ChannelFuture f = b.bind().sync();  //绑定的服务器;sync 等待服务器关闭
            System.out.println(EchoServer.class.getName()+"started and listen on" + f.channel().localAddress());
            f.channel().closeFuture().sync();   //关闭 channel 和 块，直到它被关闭

        }  finally {
            group.shutdownGracefully().sync();  //关闭 EventLoopGroup，释放所有资源
        }
    }
}
