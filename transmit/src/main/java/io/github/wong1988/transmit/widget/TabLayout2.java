package io.github.wong1988.transmit.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import io.github.wong1988.kit.utils.UiUtils;
import io.github.wong1988.transmit.R;


public class TabLayout2 extends TabLayout {

    public TabLayout2(@NonNull Context context) {
        this(context, null);
    }

    public TabLayout2(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("WrongConstant")
    public TabLayout2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        // 指示器不充满，仅占文字长度
        setTabIndicatorFullWidth(false);
        // 设置tab模式为滚动
        setTabMode(MODE_SCROLLABLE);

        // 文字颜色变化
        setTabTextColors(Color.parseColor("#666666"), Color.parseColor("#0a58f6"));

        // 指示器动画
        setTabIndicatorAnimationMode(INDICATOR_ANIMATION_MODE_ELASTIC);

        // 当前两项成对使用
        setSelectedTabIndicator(R.drawable.wong_transmit_tab_lline);
        setSelectedTabIndicatorHeight((int) UiUtils.dip2px(2));
        // 还需要在设置一下颜色
        setSelectedTabIndicatorColor(Color.parseColor("#0950de"));

    }
}

