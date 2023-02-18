package io.github.wong1988.transmit.widget.server.handler;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.github.wong1988.kit.AndroidKit;
import io.github.wong1988.kit.entity.FileInfo;
import io.github.wong1988.kit.utils.AppUtils;
import io.github.wong1988.media.MediaCenter;
import io.github.wong1988.transmit.widget.server.HttpRequest;
import io.github.wong1988.transmit.widget.server.ResUriHandler;

/**
 * 网传页处理器
 */
public class WebTransferHtmlHandler implements ResUriHandler {

    // 默认
    public static String IMAGE_ERROR;
    public static String TRANSFER_LOGO;
    public static String APK_LOGO;
    public static String AUDIO_LOGO;
    public static String PDF_LOGO;
    public static String WORD_LOGO;
    public static String EXCEL_LOGO;
    public static String POWER_POINT_LOGO;
    public static String TXT_LOGO;

    // 预览图的前缀类型 + aimg（资源图片）
    public static String STORAGE_APK_PREFIX;
    public static String STORAGE_IMAGE_PREFIX;
    public static String STORAGE_VIDEO_PREFIX;
    public static String STORAGE_AUDIO_PREFIX;
    // 下载地址的前缀就此一种
    public static String STORAGE_DOWNLOAD_PREFIX;

    private final List<FileInfo> mFiles;

    public WebTransferHtmlHandler(int port, List<FileInfo> files) {
        this(port, "transfer_logo.png", files);
    }

    /**
     * @param port 端口号
     * @param logo logo需要放到asserts根目录下
     */
    public WebTransferHtmlHandler(int port, String logo, List<FileInfo> files) {
        if (files == null)
            files = new ArrayList<>();
        this.mFiles = files;

        IMAGE_ERROR = "http://192.168.43.1:" + port + "/aimg/thumbnail_error.png";
        TRANSFER_LOGO = "http://192.168.43.1:" + port + "/aimg/" + logo;
        APK_LOGO = "http://192.168.43.1:" + port + "/aimg/apk_logo";
        AUDIO_LOGO = "http://192.168.43.1:" + port + "/aimg/audio_logo";
        PDF_LOGO = "http://192.168.43.1:" + port + "/aimg/pdf_logo";
        WORD_LOGO = "http://192.168.43.1:" + port + "/aimg/word_logo";
        EXCEL_LOGO = "http://192.168.43.1:" + port + "/aimg/excel_logo";
        POWER_POINT_LOGO = "http://192.168.43.1:" + port + "/aimg/ppt_logo";
        TXT_LOGO = "http://192.168.43.1:" + port + "/aimg/txt_logo";


        STORAGE_APK_PREFIX = "http://192.168.43.1:" + port + "/sapk";
        STORAGE_IMAGE_PREFIX = "http://192.168.43.1:" + port + "/simg";
        STORAGE_VIDEO_PREFIX = "http://192.168.43.1:" + port + "/svideo";
        STORAGE_AUDIO_PREFIX = "http://192.168.43.1:" + port + "/saudio";
        STORAGE_DOWNLOAD_PREFIX = "http://192.168.43.1:" + port + "/sdownload";
    }

    @Override
    public boolean matches(String uri) {
        // 直接地址输入服务器host:port
        // 浏览器接收是：GET / HTTP/1.1
        // 即直接打开主页（网传页）
        return uri == null || uri.equals("") || uri.equals("/");
    }

    @Override
    public void handler(HttpRequest request) {

        String indexHtml = "";
        try {
            InputStream is = AndroidKit.getInstance().getAppContext().getAssets().open("transfer.template");
            // 读取 本地 transfer.html文件 并转换成string
            indexHtml = htmlFile2String(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (request.getSocket() != null) {
            OutputStream outputStream = null;
            PrintStream printStream = null;

            try {
                outputStream = request.getSocket().getOutputStream();
                printStream = new PrintStream(outputStream);
                printStream.println("HTTP/1.1 200 OK");
                printStream.println("Content-Type:text/html");
                printStream.println("Cache-Control:no-cache");
                printStream.println("Pragma:no-cache");
                printStream.println("Expires:0");
                printStream.println();

                indexHtml = convert(indexHtml);

                // 中文会进行转码
                byte[] bytes = indexHtml.getBytes(StandardCharsets.UTF_8);
                printStream.write(bytes);

                printStream.flush();
                printStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                        outputStream = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (printStream != null) {
                    try {
                        printStream.close();
                        printStream = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void destroy() {

    }

    private String convert(String indexHtml) {

        if (indexHtml == null)
            return "";

        StringBuilder allFileListInfoHtmlBuilder = new StringBuilder();
        // 获取分享文件的总数
        int count = mFiles.size();
        // 对logo进行赋值
        indexHtml = indexHtml.replaceAll("\\{app_avatar\\}", TRANSFER_LOGO);
        // 文件个数
        indexHtml = indexHtml.replaceAll("\\{file_count\\}", String.valueOf(count));
        // app名称
        indexHtml = indexHtml.replaceAll("\\{app_name\\}", AppUtils.getAppName());

        // ↑ 转换完成 logo 标题 分享的总个数

        List<FileInfo> apkFiles = new ArrayList<>();
        List<FileInfo> imgFiles = new ArrayList<>();
        List<FileInfo> videoFiles = new ArrayList<>();
        List<FileInfo> audioFiles = new ArrayList<>();
        List<FileInfo> documentFiles = new ArrayList<>();

        for (int i = 0; i < mFiles.size(); i++) {
            FileInfo fileInfo = mFiles.get(i);
            // 忽略 这里无需解包
            @SuppressLint("MissingPermission") MediaCenter.FileClassify fileType = fileInfo.getFileType();
            if (MediaCenter.isApkFileType(fileType)) {
                apkFiles.add(fileInfo);
            } else if (MediaCenter.isImageFileType(fileType)) {
                imgFiles.add(fileInfo);
            } else if (MediaCenter.isVideoFileType(fileType)) {
                videoFiles.add(fileInfo);
            } else if (MediaCenter.isAudioFileType(fileType)) {
                audioFiles.add(fileInfo);
            } else if (MediaCenter.isDocumentFileType(fileType)) {
                documentFiles.add(fileInfo);
            }
            // 其他类型忽略
        }

        String apkHtml = convertClassifyTemplateHtml(apkFiles, "应用");
        String imageHtml = convertClassifyTemplateHtml(imgFiles, "图片");
        String videoHtml = convertClassifyTemplateHtml(videoFiles, "视频");
        String audioHtml = convertClassifyTemplateHtml(audioFiles, "音乐");
        String documentHtml = convertClassifyTemplateHtml(documentFiles, "文档");

        allFileListInfoHtmlBuilder.append(apkHtml);
        allFileListInfoHtmlBuilder.append(imageHtml);
        allFileListInfoHtmlBuilder.append(videoHtml);
        allFileListInfoHtmlBuilder.append(audioHtml);
        allFileListInfoHtmlBuilder.append(documentHtml);
        indexHtml = indexHtml.replaceAll("\\{file_list_template\\}", allFileListInfoHtmlBuilder.toString());

        return indexHtml;

    }


    /**
     * 转换分类以及当前分类的文件列表
     */
    private String convertClassifyTemplateHtml(List<FileInfo> files, String classifyName) {

        if (files == null)
            files = new ArrayList<>();

        if (classifyName == null)
            classifyName = "undefined";

        String classifyHtml = "";
        try {
            String temp = htmlFile2String(AndroidKit.getInstance().getAppContext().getAssets().open("classify.template"));
            if (!TextUtils.isEmpty(temp))
                classifyHtml = "\n" + temp;
        } catch (IOException e) {
            e.printStackTrace();
        }

        classifyHtml = classifyHtml.replaceAll("\\{class_name\\}", classifyName);
        classifyHtml = classifyHtml.replaceAll("\\{class_count\\}", String.valueOf(files.size()));

        // ↑ 转换完成 分类名 以及 当前分类的文件个数

        classifyHtml = classifyHtml.replaceAll("\\{file_list\\}", convertFileTemplateHtml(files));

        // ↑ 转换当前分类的文件列表

        return classifyHtml;
    }

    @SuppressLint("MissingPermission")
    private String convertFileTemplateHtml(List<FileInfo> files) {

        if (files == null || files.size() == 0)
            return "";


        String fileInfoHtml = "";

        try {
            // 分类下文件列表每个item信息
            fileInfoHtml = htmlFile2String(AndroidKit.getInstance().getAppContext().getAssets().open("file.template"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();

        for (FileInfo fileInfo : files) {
            sb.append("\n");
            String temp = fileInfoHtml;

            temp = temp.replaceAll("\\{file_name\\}", fileInfo.getFileName());
            temp = temp.replaceAll("\\{file_describe\\}", fileInfo.getDescribe());
            // download + /storage/emulated/0/Music/Plastic_n_Ivory.mp3(文件地址)
            temp = temp.replaceAll("\\{file_path\\}", STORAGE_DOWNLOAD_PREFIX + fileInfo.getFilePath());

            MediaCenter.FileClassify fileType = fileInfo.getFileType();
            if (MediaCenter.isApkFileType(fileType)) {
                temp = temp.replaceAll("\\{file_avatar\\}", STORAGE_APK_PREFIX + fileInfo.getFilePath());
                temp = temp.replaceAll("\\{file_width\\}", "100%");
                temp = temp.replaceAll("\\{file_height\\}", "100%");
            } else if (MediaCenter.isImageFileType(fileType)) {
                temp = temp.replaceAll("\\{file_avatar\\}", STORAGE_IMAGE_PREFIX + fileInfo.getFilePath());
                temp = temp.replaceAll("\\{file_width\\}", fileInfo.getHeight() > fileInfo.getWidth() ? "100%" : "auto");
                temp = temp.replaceAll("\\{file_height\\}", fileInfo.getHeight() > fileInfo.getWidth() ? "auto" : "100%");
            } else if (MediaCenter.isVideoFileType(fileType)) {
                temp = temp.replaceAll("\\{file_avatar\\}", STORAGE_VIDEO_PREFIX + fileInfo.getFilePath());
                temp = temp.replaceAll("\\{file_width\\}", fileInfo.getHeight() > fileInfo.getWidth() ? "100%" : "auto");
                temp = temp.replaceAll("\\{file_height\\}", fileInfo.getHeight() > fileInfo.getWidth() ? "auto" : "100%");
            } else if (MediaCenter.isAudioFileType(fileType)) {
                temp = temp.replaceAll("\\{file_avatar\\}", STORAGE_AUDIO_PREFIX + fileInfo.getFilePath());
                temp = temp.replaceAll("\\{file_width\\}", "100%");
                temp = temp.replaceAll("\\{file_height\\}", "100%");
            } else if (MediaCenter.isPdfFileType(fileType)) {
                temp = temp.replaceAll("\\{file_avatar\\}", PDF_LOGO);
                temp = temp.replaceAll("\\{file_width\\}", "100%");
                temp = temp.replaceAll("\\{file_height\\}", "100%");
            } else if (MediaCenter.isWordFileType(fileType)) {
                temp = temp.replaceAll("\\{file_avatar\\}", WORD_LOGO);
                temp = temp.replaceAll("\\{file_width\\}", "100%");
                temp = temp.replaceAll("\\{file_height\\}", "100%");
            } else if (MediaCenter.isExcelFileType(fileType)) {
                temp = temp.replaceAll("\\{file_avatar\\}", EXCEL_LOGO);
                temp = temp.replaceAll("\\{file_width\\}", "100%");
                temp = temp.replaceAll("\\{file_height\\}", "100%");
            } else if (MediaCenter.isPowerPointFileType(fileType)) {
                temp = temp.replaceAll("\\{file_avatar\\}", POWER_POINT_LOGO);
                temp = temp.replaceAll("\\{file_width\\}", "100%");
                temp = temp.replaceAll("\\{file_height\\}", "100%");
            } else if (MediaCenter.isTxtFileType(fileType)) {
                temp = temp.replaceAll("\\{file_avatar\\}", TXT_LOGO);
                temp = temp.replaceAll("\\{file_width\\}", "100%");
                temp = temp.replaceAll("\\{file_height\\}", "100%");
            }
            sb.append(temp);
        }

        return sb.toString();
    }

    /**
     * 本地html文件读取成string
     */
    private String htmlFile2String(InputStream is) {

        if (is == null)
            return "";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len;
        byte[] bytes = new byte[2048];
        try {
            while ((len = is.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            return baos.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
