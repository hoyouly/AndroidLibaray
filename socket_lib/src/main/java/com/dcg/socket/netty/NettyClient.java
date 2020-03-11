package com.dcg.socket.netty;

import android.util.Log;
import com.dcg.socket.base.SocketClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Joe Li
 */
public class NettyClient implements SocketClient {

    private static final int MAX_RETRY = 5;
    private Channel mChannel;

    private ByteBuf delimiter = Unpooled.copiedBuffer("<<>>".getBytes());
    private Bootstrap bootstrap;
    private NioEventLoopGroup group;
    private static NettyClient INSTANCE;
    private String host;
    private int port;


    public static NettyClient getInstance() {
        if (INSTANCE == null) {
            synchronized (NettyClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NettyClient();
                }
            }
        }
        return INSTANCE;
    }


    private NettyClient() {

    }

    /**
     * 同步连接
     *
     * @return
     * @throws InterruptedException
     */
    public boolean connectSync(String host, int port) {
        this.host = host;
        this.port = port;
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(102400, delimiter));
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new HeartBeatTimerHandler());
                ch.pipeline().addLast("clientHandler", new ResponseMessageHandler());
            }
        });
        Log.e("hoyouly", "NettyClient : " + "connectSync()  host = [" + host + "], port = [" + port + "]");
        boolean success = false;
        try {
            ChannelFuture f = bootstrap.connect(host, port).sync();
            mChannel = f.channel();
            success = f.isSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            mChannel = null;
            Log.e("hoyouly", "Exception " + e.getMessage());
        }
        Log.e("hoyouly", "NettyClient : connectSync  " + success);
        return success;

    }

    /**
     * 异步轮训连接，间隔时间指数递增
     *
     * @param bootstrap
     * @param host
     * @param port
     * @param retry
     */
    private void connectAsync(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                Log.e("hoyouly", new Date() + ": 连接成功，启动控制台线程……");
                mChannel = ((ChannelFuture) future).channel();
            } else if (retry == 0) {
                Log.e("hoyouly", "重试次数已用完，放弃连接！");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                Log.e("hoyouly", ": 连接失败，第" + order + "次重连:" + host + ":" + port);
                bootstrap.config().group().schedule(() -> connectAsync(bootstrap, host, port, retry - 1), delay,
                    TimeUnit.SECONDS);
            }
        });
    }

    /**
     * 发送消息
     *
     * @param action
     * @param message
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public String send(String action, String message) throws InterruptedException, ExecutionException, TimeoutException {
        if (mChannel == null || !mChannel.isActive()) {
            if (!connectSync(host, port)) {
                throw new TimeoutException("响应超时， 请稍后重试");
            }
        }
        Log.e("hoyouly", "发送数据： " + message);
        RequestFuture future = new RequestFuture();
        RequestFutureCenter.add(action, future);
        mChannel.writeAndFlush(message + "<<>>");
        String response = future.get(4, TimeUnit.SECONDS);

        Log.e("hoyouly", "接收数据： " + response);
        return response;
    }

    @Override
    public boolean connect(String host, int port) {
        return connectSync(host, port);
    }

    @Override
    public String sendMessage(String action, String json) throws Exception {
        return send(action, json);
    }
}
