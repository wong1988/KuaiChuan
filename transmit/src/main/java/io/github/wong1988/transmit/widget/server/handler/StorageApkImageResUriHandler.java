package io.github.wong1988.transmit.widget.server.handler;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
 * 内部存储apk文件的预览图
 */
public class StorageApkImageResUriHandler implements ResUriHandler {

    private static final String STORAGE_APK_IMAGE_PREFIX = "/sapk";

    @Override
    public boolean matches(String uri) {
        return uri.startsWith(STORAGE_APK_IMAGE_PREFIX);
    }

    @Override
    public void handler(HttpRequest request) {
        String uri = request.getUri();
        int indexOf = uri.indexOf(STORAGE_APK_IMAGE_PREFIX);
        String path = "";
        if (indexOf >= 0) {
            indexOf += STORAGE_APK_IMAGE_PREFIX.length();
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
        ByteArrayInputStream fis = null;
        InputStream error = null;
        ByteArrayOutputStream baos = null;
        Bitmap bitmap = null;
        try {
            os = socket.getOutputStream();
            printStream = new PrintStream(os);
            printStream.println("HTTP/1.1 200 OK");

            boolean loadNormalApkImage = true;

            if (file.exists()) {
                PackageManager pm = AndroidKit.getInstance().getAppContext().getPackageManager();
                PackageInfo pkgInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
                if (pkgInfo != null) {
                    ApplicationInfo appInfo = pkgInfo.applicationInfo;
                    /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
                    appInfo.sourceDir = path;
                    appInfo.publicSourceDir = path;
                    /* icon1和icon2其实是一样的 */
                    Drawable icon2 = appInfo.loadIcon(pm);

                    BitmapDrawable bd = (BitmapDrawable) icon2;
                    bitmap = bd.getBitmap();
                    baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    printStream.println("Content-Length:" + baos.size());
                    printStream.println("Content-Type:application/octet-stream");
                    printStream.println();
                    // body
                    fis = new ByteArrayInputStream(baos.toByteArray());
                    int len = 0;
                    byte[] bytes = new byte[2048];
                    while ((len = fis.read(bytes)) != -1) {
                        printStream.write(bytes, 0, len);
                    }
                    loadNormalApkImage = false;
                }
            }

            if (loadNormalApkImage) {
                // 图片预览失败显示默认的apk图
                error = AndroidKit.getInstance().getAppContext().getAssets().open(WebTransferHtmlHandler.APK_ERROR);
                printStream.println("Content-Length:" + error.available());
                printStream.println("Content-Type:application/octet-stream");
                printStream.println();
                // body
                int len = 0;
                byte[] bytes = new byte[2048];
                while ((len = error.read(bytes)) != -1) {
                    printStream.write(bytes, 0, len);
                }
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
            if (error != null) {
                try {
                    error.close();
                    error = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (printStream != null) {
                printStream.close();
                printStream = null;
            }
            if (baos != null) {
                try {
                    baos.close();
                    baos = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // bitmap不能回收，刷新后就没了
        }
    }

    @Override
    public void destroy() {

    }
}
