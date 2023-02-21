package io.github.wong1988.transmit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.wong1988.adapter.ViewPagerLazyAdapter;
import io.github.wong1988.kit.entity.FileInfo;
import io.github.wong1988.transmit.R;
import io.github.wong1988.transmit.fragment.ApkFragment;
import io.github.wong1988.transmit.fragment.DocumentFragment;
import io.github.wong1988.transmit.fragment.ImageFragment;
import io.github.wong1988.transmit.fragment.MusicFragment;
import io.github.wong1988.transmit.fragment.VideoFragment;

public class BasicFileSelector extends FrameLayout {

    private SelectorListener listener;

    private final List<FileInfo> infoList = new ArrayList<>();

    public BasicFileSelector(@NonNull Context context) {
        this(context, null);
    }

    public BasicFileSelector(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasicFileSelector(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.wong_basic_file_selector, this, true);

        TabLayout2 tab2 = findViewById(R.id.wong_tb2);
        ViewPager vp = findViewById(R.id.wong_vp);


        SelectorListener2 innerListener = new SelectorListener2() {

            @Override
            public void add(FileInfo file) {
                infoList.add(file);
                if (listener != null)
                    listener.select(infoList);
            }

            @Override
            public void remove(FileInfo file) {
                infoList.remove(file);
                if (listener != null)
                    listener.select(infoList);
            }
        };

        // 绑定viewpager 和 tabLayout
        List<Fragment> fragments = Arrays.asList(new ImageFragment(innerListener), new VideoFragment(innerListener), new MusicFragment(innerListener), new DocumentFragment(innerListener), new ApkFragment(innerListener));
        vp.setAdapter(new ViewPagerLazyAdapter(((FragmentActivity) context).getSupportFragmentManager(), fragments, Arrays.asList("图片", "视频", "音频", "文档", "应用")));
        vp.setOffscreenPageLimit(fragments.size());
        tab2.setupWithViewPager(vp);

    }

    public void reset() {
        for (FileInfo fileInfo : infoList) {
            fileInfo.setExtra("");
        }
        infoList.clear();
        List<Fragment> fragments = ((FragmentActivity) getContext()).getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof ImageFragment)
                ((ImageFragment) fragment).notifyDataSetChanged();
            else if (fragment instanceof VideoFragment)
                ((VideoFragment) fragment).notifyDataSetChanged();
            else if (fragment instanceof MusicFragment)
                ((MusicFragment) fragment).notifyDataSetChanged();
            else if (fragment instanceof DocumentFragment)
                ((DocumentFragment) fragment).notifyDataSetChanged();
            else if (fragment instanceof ApkFragment)
                ((ApkFragment) fragment).notifyDataSetChanged();
        }
    }

    public void setListener(SelectorListener listener) {
        this.listener = listener;
    }

    public interface SelectorListener {
        void select(List<FileInfo> files);
    }


    public interface SelectorListener2 {
        void add(FileInfo file);

        void remove(FileInfo file);
    }
}
