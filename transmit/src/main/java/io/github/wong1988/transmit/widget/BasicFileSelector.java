package io.github.wong1988.transmit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.wong1988.transmit.R;

public class BasicFileSelector extends FrameLayout {

    public BasicFileSelector(@NonNull Context context) {
        this(context, null);
    }

    public BasicFileSelector(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasicFileSelector(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.wong_basic_file_selector, this, true);

    }
}
