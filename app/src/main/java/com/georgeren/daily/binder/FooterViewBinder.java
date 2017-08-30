package com.georgeren.daily.binder;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.georgeren.daily.R;
import com.georgeren.daily.bean.FooterBean;
import com.georgeren.daily.utils.SettingUtil;

import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by georgeRen on 2017/8/30.
 */

public class FooterViewBinder extends ItemViewBinder<FooterBean, FooterViewBinder.ViewHolder> {

    @NonNull
    @Override
    protected FooterViewBinder.ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_loading, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull FooterBean item) {
        int color = SettingUtil.getInstance().getColor();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable wrapDrawable = DrawableCompat.wrap(holder.mProgressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, color);
            holder.mProgressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            holder.mProgressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar mProgressBar;

        ViewHolder(View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.loading);
        }
    }
}
