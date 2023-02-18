package io.github.wong1988.transmit.widget.server.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;

import io.github.wong1988.transmit.widget.server.HttpRequest;
import io.github.wong1988.transmit.widget.server.ResUriHandler;

public class DownloadResUriHandler implements ResUriHandler {

    public static final String DOWNLOAD_PREFIX = "/sdownload";

    @Override
    public boolean matches(String uri) {
        return uri.startsWith(DOWNLOAD_PREFIX);
    }

    @Override
    public void handler(HttpRequest request) {
        String uri = request.getUri();
        int indexOf = uri.indexOf(DOWNLOAD_PREFIX);
        String path = "";
        if (indexOf >= 0) {
            indexOf += DOWNLOAD_PREFIX.length();
            path = uri.substring(indexOf);
        }

        try {
            // 需要解码才能得到正确地址
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        File file = new File(path);

        Socket socket = request.getSocket();
        // socket的输出流
        OutputStream os = null;
        PrintStream printStream = null;
        FileInputStream fis = null;
        try {
            os = socket.getOutputStream();
            printStream = new PrintStream(os);
            if (file.exists()) {
                printStream.println("HTTP/1.1 200 OK");
                printStream.println("Content-Length:" + file.length());
                printStream.println("Content-Type:application/octet-stream");
                printStream.println();
                // body
                fis = new FileInputStream(file);
                int len = 0;
                byte[] bytes = new byte[2048];
                while ((len = fis.read(bytes)) != -1) {
                    printStream.write(bytes, 0, len);
                }
            } else {
                // 文件未找到
                printStream.println("HTTP/1.1 404 NotFound");
                printStream.println();
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
