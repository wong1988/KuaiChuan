package io.github.wong1988.transmit.fragment;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.List;

import io.github.wong1988.adapter.divider.GridLayoutManagerDivider;
import io.github.wong1988.adapter.listener.OnItemClickListener;
import io.github.wong1988.kit.entity.FileInfo;
import io.github.wong1988.kit.task.QueryMediaStoreAsyncTask;
import io.github.wong1988.kit.utils.FileUtils;
import io.github.wong1988.kit.utils.UiUtils;
import io.github.wong1988.media.MediaCenter;
import io.github.wong1988.transmit.R;
import io.github.wong1988.transmit.adapter.MediaAdapter;
import io.github.wong1988.transmit.base.BaseListFragment;
import io.github.wong1988.transmit.base.PagerStartType;
import io.github.wong1988.transmit.widget.BasicFileSelector;


public class VideoFragment extends BaseListFragment<FileInfo, MediaAdapter> {

    private RecyclerView rv;

    private final BasicFileSelector.SelectorListener2 listener;

    public VideoFragment(BasicFileSelector.SelectorListener2 listener) {
        this.listener = listener;
    }

    @Override
    protected int layoutResId() {
        return R.layout.wong_fragment_transmit_media;
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
        return new GridLayoutManager(getActivity(), 4);
    }

    @Override
    protected RecyclerView.ItemDecoration initItemDecoration() {
        return GridLayoutManagerDivider.getVerticalDivider(getResources().getColor(android.R.color.transparent), (int) UiUtils.dip2px(2));
    }

    @Override
    protected MediaAdapter initAdapter() {
        return new MediaAdapter(getActivity());
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new OnItemClickListener<FileInfo>() {
            @Override
            public void onClick(FileInfo fileInfo, int position, View view) {
                String extra = "true".equals(fileInfo.getExtra()) ? "" : "true";
                fileInfo.setExtra(extra);
                mAdapter.notifyItemChanged(mAdapter.getRealPosition(position));
                if (listener != null) {
                    if ("true".equals(extra))
                        listener.add(fileInfo);
                    else
                        listener.remove(fileInfo);
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void requestData(int page) {

        new QueryMediaStoreAsyncTask(new FileUtils.FileInfoChanged() {
            @SuppressLint("MissingPermission")
            @Override
            public void change(FileInfo fileInfo) {

            }
        }, new QueryMediaStoreAsyncTask.IFileAsyncTask() {
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
        }).execute(MediaCenter.VIDEO_EXTENSIONS);
    }
}