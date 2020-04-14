package com.dcg.network.socket.netty;


import com.dcg.network.utils.GsonManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ResponseMessageHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (msg != null) {
            Message mssage = GsonManager.getInstance().fromJson(msg, Message.class);
            if("app/heartbeat".equals(mssage.getAction())){ // skip HeartBeat
                return;
            }
            RequestFutureCenter.set(mssage.getAction(), msg);
        }
    }

    static class Message{
        private String action;
        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }

}