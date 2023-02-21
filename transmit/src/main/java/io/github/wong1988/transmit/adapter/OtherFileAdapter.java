package io.github.wong1988.transmit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import io.github.wong1988.adapter.SimpleListAdapter;
import io.github.wong1988.adapter.holder.RecyclerViewHolder;
import io.github.wong1988.kit.entity.FileInfo;
import io.github.wong1988.kit.utils.UiUtils;
import io.github.wong1988.media.MediaCenter;
import io.github.wong1988.transmit.R;

public class OtherFileAdapter extends SimpleListAdapter<FileInfo> {

    public OtherFileAdapter(Context context) {
        super(context, R.layout.wong_basic_selector_other_file_item, false);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "MissingPermission"})
    @Override
    public void onBindViewHolders(RecyclerViewHolder holder, int viewType, FileInfo fileInfo, int position) {

        @SuppressLint("MissingPermission") MediaCenter.FileClassify fileType = fileInfo.getFileType();
        holder.setText(R.id.wong_tv1, fileInfo.getFileName());
        holder.setText(R.id.wong_tv2, fileInfo.getDescribe());

        holder.getCheckBox(R.id.wong_cb).setOnCheckedChangeListener(null);//先设置一次CheckBox的选中监听器，传入参数null
        holder.getCheckBox(R.id.wong_cb).setChecked("true".equals(fileInfo.getExtra()));//用数组中的值设置CheckBox的选中状态
        //再设置一次CheckBox的选中监听器，当CheckBox的选中状态发生改变时，把改变后的状态储存在数组中
        holder.getCheckBox(R.id.wong_cb).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                fileInfo.setExtra(Boolean.toString(b));
            }
        });

        RequestOptions requestOptions = new RequestOptions().transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) UiUtils.dip2px(6))));

        if (MediaCenter.isApkFileType(fileType)) {
            if (fileInfo.getApkThumbnail() == null) {
                // 默认
                Glide.with(getAttachContext())
                        .load(getAttachContext().getResources().getDrawable(R.drawable.wong_transmit_apk_file))
                        .apply(requestOptions)
                        .into(holder.getImageView(R.id.wong_pic));
            } else {
                Glide.with(getAttachContext())
                        .load(fileInfo.getApkThumbnail())
                        .apply(requestOptions)
                        .into(holder.getImageView(R.id.wong_pic));
            }
        } else if (MediaCenter.isTxtFileType(fileType)) {
            Glide.with(getAttachContext())
                    .load(getAttachContext().getResources().getDrawable(R.drawable.wong_transmit_txt_file))
                    .apply(requestOptions)
                    .into(holder.getImageView(R.id.wong_pic));
        } else if (MediaCenter.isPdfFileType(fileType)) {
            Glide.with(getAttachContext())
                    .load(getAttachContext().getResources().getDrawable(R.drawable.wong_transmit_pdf_file))
                    .apply(requestOptions)
                    .into(holder.getImageView(R.id.wong_pic));
        } else if (MediaCenter.isWordFileType(fileType)) {
            Glide.with(getAttachContext())
                    .load(getAttachContext().getResources().getDrawable(R.drawable.wong_transmit_word_file))
                    .apply(requestOptions)
                    .into(holder.getImageView(R.id.wong_pic));
        } else if (MediaCenter.isExcelFileType(fileType)) {
            Glide.with(getAttachContext())
                    .load(getAttachContext().getResources().getDrawable(R.drawable.wong_transmit_excel_file))
                    .apply(requestOptions)
                    .into(holder.getImageView(R.id.wong_pic));
        } else if (MediaCenter.isPowerPointFileType(fileType)) {
            Glide.with(getAttachContext())
                    .load(getAttachContext().getResources().getDrawable(R.drawable.wong_transmit_powerpoint_file))
                    .apply(requestOptions)
                    .into(holder.getImageView(R.id.wong_pic));
        } else if (MediaCenter.isAudioFileType(fileType)) {
            if (fileInfo.getMusicThumbnail() == null) {
                // 默认
                Glide.with(getAttachContext())
                        .load(getAttachContext().getResources().getDrawable(R.drawable.wong_transmit_music_file))
                        .apply(requestOptions)
                        .into(holder.getImageView(R.id.wong_pic));
            } else {
                Glide.with(getAttachContext())
                        .load(fileInfo.getMusicThumbnail())
                        .apply(requestOptions)
                        .into(holder.getImageView(R.id.wong_pic));
            }
        }

    }
}
