package io.github.wong1988.transmit.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.List;

import io.github.wong1988.adapter.divider.LinearLayoutManagerDivider;
import io.github.wong1988.adapter.listener.OnItemClickListener;
import io.github.wong1988.kit.entity.FileInfo;
import io.github.wong1988.kit.task.FileAsyncTask;
import io.github.wong1988.kit.utils.FileUtils;
import io.github.wong1988.media.MediaCenter;
import io.github.wong1988.transmit.R;
import io.github.wong1988.transmit.adapter.OtherFileAdapter;
import io.github.wong1988.transmit.base.BaseListFragment;
import io.github.wong1988.transmit.base.PagerStartType;


public class ApkFragment extends BaseListFragment<FileInfo, OtherFileAdapter> {

    private RecyclerView rv;

    @Override
    protected int layoutResId() {
        return R.layout.wong_fragment_transmit_other;
    }

    @Override
    protected void init(View rootView) {
        rv = rootView.findViewById(R.id.wong_rv);
        // 解决刷新闪烁
        ((SimpleItemAnimator) rv.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    protected PagerStartType startPagerNumber() {
        return PagerStartType.ONE;
    }

    @Override
    protected boolean clearDataNow() {
        return true;
    }

    @Override
    protected RecyclerView initRecyclerView() {
        return rv;
    }

    @Override
    protected RecyclerView.LayoutManager initLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    protected RecyclerView.ItemDecoration initItemDecoration() {
        return LinearLayoutManagerDivider.getVerticalDivider(Color.parseColor("#f1f1f1"), 2);
    }

    @Override
    protected OtherFileAdapter initAdapter() {
        return new OtherFileAdapter(getActivity());
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new OnItemClickListener<FileInfo>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(FileInfo fileInfo, int position, View view) {
                MediaCenter.openFile(getActivity(), fileInfo.getFilePath());
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void requestData(int page) {

        new FileAsyncTask(new FileUtils.FileInfoChanged() {
            @SuppressLint("MissingPermission")
            @Override
            public void change(FileInfo fileInfo) {
                try {
                    mAdapter.notifyItemChanged(fileInfo.getIndex());
                } catch (Exception e) {
                    Log.e("error", "change()", e);
                }
            }
        }, new FileAsyncTask.IFileAsyncTask() {
            @Override
            public void start() {

            }

            @SuppressLint("MissingPermission")
            @Override
            public void complete(List<FileInfo> files) {
                addNoPagingData(files);
            }

            @Override
            public void cancel() {

            }
        }).execute(MediaCenter.INSTALL_EXTENSIONS);
    }
}