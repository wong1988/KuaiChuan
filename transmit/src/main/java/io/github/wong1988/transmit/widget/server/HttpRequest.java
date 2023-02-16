package io.github.wong1988.transmit.widget.server;

import java.net.Socket;
import java.util.HashMap;

public class HttpRequest {

    private String mUri;
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

    public void addHeader(String key, String value) {
        this.mHeaderMap.put(key, value);
    }
}
