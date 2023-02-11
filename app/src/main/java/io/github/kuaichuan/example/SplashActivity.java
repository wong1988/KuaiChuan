package io.github.kuaichuan.example;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.fonuhuolian.xappwindows.XPermissionsNoticeWindow;
import org.fonuhuolian.xappwindows.bean.XPermissionNoticeBean;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private XPermissionsNoticeWindow xPermissionsNoticeWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // ① 权限申请 防止onAgreed的时候xPermissionsNoticeWindow还没初始化完毕
        final List<XPermissionNoticeBean> l = new ArrayList<>();
        l.add(new XPermissionNoticeBean(R.drawable.eg_storage_permission, "存储权限", "启权限后，可以选择分享的文件", Manifest.permission.WRITE_EXTERNAL_STORAGE));

        xPermissionsNoticeWindow = new XPermissionsNoticeWindow(SplashActivity.this, l, new XPermissionsNoticeWindow.Listener() {
            @Override
            public void onGranted() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });

        xPermissionsNoticeWindow.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        xPermissionsNoticeWindow.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        xPermissionsNoticeWindow.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        xPermissionsNoticeWindow.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!xPermissionsNoticeWindow.isShowing())
            finish();
    }
}