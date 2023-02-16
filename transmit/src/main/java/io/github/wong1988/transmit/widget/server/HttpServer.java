package io.github.wong1988.transmit.widget.server;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class HttpServer {

    /**
     * 端口号
     */
    private int mPort;

    /**
     * socket服务
     */
    private ServerSocket mServerSocket;

    /**
     * 线程池
     */
    private final ExecutorService mThreadPool = Executors.newCachedThreadPool();

    /**
     * uri
     */
    private final List<ResUriHandler> mResUriHandlerList = new ArrayList<>();

    /**
     * 是否启用
     */
    private boolean mIsEnable;

    public HttpServer(int port) {
        this.mPort = port;
    }

    /**
     * 启动服务器
     */
    public void startServer() {
        mIsEnable = true;
        mThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mServerSocket = new ServerSocket(mPort);

                    while (mIsEnable) {
                        Socket socket = mServerSocket.accept();
                        handleSocketAsync(socket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 停用服务
     */
    public void stopServer() {
        mIsEnable = false;
        for (ResUriHandler resUriHandler : mResUriHandlerList) {
            resUriHandler.destroy();
        }

        if (mServerSocket != null) {
            try {
                // 停用服务
                mServerSocket.close();
                mServerSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理socket
     */
    private void handleSocketAsync(final Socket socket) {
        mThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                //1. auto create request object by the parameter socket
                HttpRequest request = createRequest(socket);

                //2. loop the mResUriHandlerList, and assign the task to the specify ResUriHandler
                for (ResUriHandler resUriHandler : mResUriHandlerList) {
                    if (!resUriHandler.matches(request.getUri())) {
                        continue;
                    }
                    // 匹配指定的资源后进行处理
                    resUriHandler.handler(request);
                }
            }
        });
    }


    private HttpRequest createRequest(Socket socket) {
        HttpRequest request = new HttpRequest(socket);
        try {
            // 获取客户端请求
            // 客户端请求地址 socket.getRemoteSocketAddress();
            InputStream is = socket.getInputStream();
            // 请求行(请求方式 + uri + http版本) 如：GET /assets/ic_logo.png HTTP/1.1
            String requestLine = readLine(is);
            Log.e("请求行", requestLine + "");

            if (requestLine != null) {
                String[] split = requestLine.split(" ");
                if (split.length > 0)
                    request.setType(split[0]);
                if (split.length > 1)
                    request.setUri(split[1]);
            }

            String header = "";
            // 请求头，如
            // Host: 192.168.43.1:3999
            // Connection: keep-alive
            // User-Agent: Mozilla/5.0 (Linux; Android 12; HarmonyOS; OCE-AN50; HMSCore 6.9.6.302) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.88 HuaweiBrowser/13.0.3.320 Mobile Safari/537.36
            // Accept: image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8
            // Referer: http://192.168.43.1:3999/
            // Accept: gzip, deflate
            // Accept-Language: zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7
            while ((header = readLine(is)) != null) {
                String[] split = header.split(":");
                if (split.length > 1) {
                    String headerKey = split[0];
                    String headerVal = split[1];
                    if (headerVal != null && headerVal.startsWith(" "))
                        headerVal = headerVal.substring(1);
                    request.addHeader(headerKey, headerVal);
                }

                // 空行后就不读了 往下是请求体 一般GET无请求体 TODO 目前GET请求就能处理当前的需求
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }

    public HttpServer addResUriHandler(ResUriHandler resUriHandler) {
        if (resUriHandler != null)
            this.mResUriHandlerList.add(resUriHandler);
        return this;
    }


    /**
     * 可客户端请求的流进行读写
     */
    private String readLine(InputStream is) throws IOException {

        StringBuilder sb = new StringBuilder();
        int a = 0, b = 0;
        while ((b != -1) && !(a == '\r' && b == '\n')) {
            a = b;
            b = is.read();
            sb.append((char) (b));
        }

        String line = sb.toString();
        return line.equals("\r\n") ? null : line;
    }
}
