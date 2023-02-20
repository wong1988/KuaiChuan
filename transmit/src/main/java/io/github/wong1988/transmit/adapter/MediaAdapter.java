package io.github.wong1988.transmit.adapter;

import android.content.Context;

import com.bumptech.glide.Glide;

import io.github.wong1988.adapter.SimpleListAdapter;
import io.github.wong1988.adapter.holder.RecyclerViewHolder;
import io.github.wong1988.kit.entity.FileInfo;
import io.github.wong1988.transmit.R;

public class MediaAdapter extends SimpleListAdapter<FileInfo> {

    public MediaAdapter(Context context) {
        super(context, R.layout.wong_basic_selector_media_item, false);
    }

    @Override
    public void onBindViewHolders(RecyclerViewHolder holder, int viewType, FileInfo fileInfo, int position) {
        holder.setChecked(R.id.wong_cb, "true".equals(fileInfo.getExtra()));
        Glide.with(getAttachContext())
                .load(fileInfo.getFilePath())
                .into(holder.getImageView(R.id.wong_pic));
    }
}
