package io.github.wong1988.transmit.widget.server.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;

import io.github.wong1988.kit.AndroidKit;
import io.github.wong1988.transmit.widget.server.HttpRequest;
import io.github.wong1988.transmit.widget.server.ResUriHandler;

/**
 * assets根目录下的图片文件, 内部使用
 */
public class AssetsImageResUriHandler implements ResUriHandler {

    public static final String ASSETS_PREFIX = "/aimg/";

    @Override
    public boolean matches(String uri) {
        return uri.startsWith(ASSETS_PREFIX);
    }

    @Override
    public void handler(HttpRequest request) {
        String uri = request.getUri();
        int indexOf = uri.indexOf(ASSETS_PREFIX);
        String path = "";
        if (indexOf >= 0) {
            indexOf += ASSETS_PREFIX.length();
            path = uri.substring(indexOf);
        }

        try {
            // 需要解码才能得到正确地址
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Socket socket = request.getSocket();
        // socket的输出流
        OutputStream os = null;
        PrintStream printStream = null;
        InputStream fis = null;
        try {
            os = socket.getOutputStream();
            printStream = new PrintStream(os);
            printStream.println("HTTP/1.1 200 OK");

            try {
                // todo 当前项目内部使用的图片资源，默认图（pdf,word这些没有预览图的）
                fis = AndroidKit.getInstance().getAppContext().getAssets().open(path);
            } catch (Exception e) {
                // 地址不对 加载裂开的图
                fis = AndroidKit.getInstance().getAppContext().getAssets().open(WebTransferHtmlHandler.IMAGE_ERROR);
            }

            printStream.println("Content-Length:" + fis.available());
            printStream.println("Content-Type:application/octet-stream");
            printStream.println();
            // body
            int len = 0;
            byte[] bytes = new byte[2048];
            while ((len = fis.read(bytes)) != -1) {
                printStream.write(bytes, 0, len);
            }

            // 输出流最好都flush
            printStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    fis = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (printStream != null) {
                printStream.close();
                printStream = null;
            }
        }
    }

    @Override
    public void destroy() {

    }
}
