package com.dcg.network;

import android.util.Log;
import com.dcg.network.socket.SocketFactory;
import okhttp3.*;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @ Time  :  2020-03-17
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description :
 */
public class SocketInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (request == null) {
            Log.d("SocketInterceptor", "intercept request is null");
            return null;
        }
        RequestBody body = request.body();
        if (body == null) {
            Log.d("SocketInterceptor", "intercept body is null");
            return null;
        }

        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        buffer.close();//数据的真正写入是在close()中，之前的只是在缓存中。
        String utf8 = buffer.readString(Charset.forName("utf-8"));

        Response response = null;
        if (request.url() != null) {
            // 得到 action  和参数 json
            HttpUrl url = request.url();
            String action = url.encodedPath().substring(1);
            try {
                //调用 Socket 请求的参数
                String result = SocketFactory.getSocketClient().sendMessage(action, utf8);
                if (result == null) {
                    Log.d("SocketInterceptor", "intercept result  is null");
                    return null;
                }
                //把得到的结果 封装成一个response 返回即可
                buffer = new Buffer();
                buffer.writeUtf8(result);
                buffer.close();
                response = new Response.Builder()
                    .body(new RealResponseBody(body.contentType().type(), result.length(), buffer))
                    .request(request)
                    .protocol(Protocol.SPDY_3)
                    .code(200)
                    .message(action + "  is success")
                    .build();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }
}
