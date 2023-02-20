package io.github.wong1988.transmit.server;

import java.net.Socket;
import java.util.HashMap;

public class HttpRequest {

    // 请求地址
    private String mUri;
    // 请求方式
    private String mType;
    private final HashMap<String, String> mHeaderMap = new HashMap<String, String>();
    private final Socket mSocket;

    public HttpRequest(Socket socket) {
        this.mSocket = socket;
    }

    public Socket getSocket() {
        return mSocket;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        this.mUri = uri;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public void addHeader(String key, String value) {
        this.mHeaderMap.put(key, value);
    }
}
