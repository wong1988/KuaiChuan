package io.github.kuaichuan.example;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import io.github.wong1988.kit.entity.FileInfo;
import io.github.wong1988.kit.receiver.WifiApStateReceiver;
import io.github.wong1988.kit.task.WifiApAddress;
import io.github.wong1988.kit.utils.SettingsUtils;
import io.github.wong1988.transmit.server.HttpServer;
import io.github.wong1988.transmit.widget.BasicFileSelector;

public class MainActivity extends AppCompatActivity {

    // 地址1是否可用
    private boolean address1;
    // 地址2是否可用
    private boolean address2;
    // 热点是否开启
    private boolean wifiApEnable;

    private TextView tv;
    private ImageView iv;
    private WifiApStateReceiver wifiApStateReceiver;

    private HttpServer server;
    private List<FileInfo> infoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        address1 = SettingsUtils.isWifiApActivityExists(WifiApAddress.NORMAL);
        address2 = SettingsUtils.isWifiApActivityExists(WifiApAddress.SPECIAL);

        tv = findViewById(R.id.tv);
        iv = findViewById(R.id.iv);

        initText();

        wifiApStateReceiver = new WifiApStateReceiver(new WifiApStateReceiver.WifiApStateListener() {
            @Override
            public void state(boolean isOpen) {
                if (wifiApEnable != isOpen) {
                    wifiApEnable = isOpen;
                    initText();
                }
            }
        });
        wifiApStateReceiver.registerReceiver();

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setCustomView(R.layout.action_custom);
        supportActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);

        TextView actionTv = supportActionBar.getCustomView().findViewById(R.id.action_tv);
        TextView actionClear = supportActionBar.getCustomView().findViewById(R.id.action_clean);
        actionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tv.getVisibility() == View.VISIBLE)
                    Toast.makeText(MainActivity.this, "请确保个人热点已经开启", Toast.LENGTH_SHORT).show();

                server = new HttpServer(3888);
                server.startServer(infoList);
                startActivityForResult(new Intent(MainActivity.this, WebTransmitActivity.class), 1000);
            }
        });

        BasicFileSelector selector = (BasicFileSelector) findViewById(R.id.fs);
        selector.setListener(new BasicFileSelector.SelectorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void select(List<FileInfo> files) {
                infoList = files;
                if (files.size() > 0) {
                    actionTv.setText("确定（" + files.size() + "）");
                    actionTv.setVisibility(View.VISIBLE);
                    actionClear.setVisibility(View.VISIBLE);
                } else {
                    actionTv.setVisibility(View.GONE);
                    actionClear.setVisibility(View.GONE);
                }
            }
        });
        actionClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionTv.setVisibility(View.GONE);
                actionClear.setVisibility(View.GONE);
                selector.reset();
            }
        });
    }

    public void initText() {

        if (wifiApEnable) {
            iv.setVisibility(View.GONE);
            tv.setVisibility(View.GONE);
        } else {
            // 热点不可用

            iv.setOnClickListener(null);
            tv.setVisibility(View.VISIBLE);

            SpannableStringBuilder spannable = new SpannableStringBuilder();

            if (address1 && address2) {
                spannable.append("创建热点");
                spannable.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        SettingsUtils.startWifiApActivity(MainActivity.this, WifiApAddress.NORMAL, new SettingsUtils.SettingsActivityNotFoundException() {
                            @Override
                            public void notFound() {
                                Toast.makeText(MainActivity.this, "打开设置页面失败，请手动创建热点", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.append("  开始传输");
                iv.setVisibility(View.VISIBLE);
                // 弹窗是 提示使用另一地址
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);//this为上下文，如果在本类里显示，通常使用this
                        b.setTitle("温馨提醒");
                        b.setMessage("如果点击 创建热点 后，页面没有开启热点选项，可尝试使用方式二或手动使用系统设置开启。");
                        b.setNegativeButton("方式二", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SettingsUtils.startWifiApActivity(MainActivity.this, WifiApAddress.SPECIAL, new SettingsUtils.SettingsActivityNotFoundException() {
                                    @Override
                                    public void notFound() {
                                        Toast.makeText(MainActivity.this, "打开设置页面失败，请手动创建热点", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        b.setPositiveButton("我知道了", null);
                        b.show();
                    }
                });
            } else if (!address1 && !address2) {
                // 手动开启
                spannable.append("请手动创建热点");
                spannable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.append("  开始传输");
                iv.setVisibility(View.GONE);
            } else {
                spannable.append("创建热点");
                spannable.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        SettingsUtils.startWifiApActivity(MainActivity.this, address1 ? WifiApAddress.NORMAL : WifiApAddress.SPECIAL, new SettingsUtils.SettingsActivityNotFoundException() {
                            @Override
                            public void notFound() {
                                Toast.makeText(MainActivity.this, "打开设置页面失败，请手动创建热点", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.append("  开始传输");
                iv.setVisibility(View.VISIBLE);
                // 弹窗是 手动创建热点
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);//this为上下文，如果在本类里显示，通常使用this
                        b.setTitle("温馨提醒");
                        b.setMessage("如果点击 创建热点 后，页面没有开启热点选项，请去系统设置去开启。");
                        b.setPositiveButton("我知道了", null);
                        b.show();
                    }
                });
            }

            tv.setText(spannable);
            tv.setMovementMethod(LinkMovementMethod.getInstance());

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK && server != null)
            server.stopServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wifiApStateReceiver != null)
            wifiApStateReceiver.unregisterReceiver();
    }

}