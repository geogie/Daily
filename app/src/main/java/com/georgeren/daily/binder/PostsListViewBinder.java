package com.georgeren.daily.binder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.georgeren.daily.R;
import com.georgeren.daily.bean.PostsListBean;
import com.georgeren.daily.mvp.postscontent.PostsContentView;

import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by georgeRen on 2017/8/30.
 */

public class PostsListViewBinder extends ItemViewBinder<PostsListBean, PostsListViewBinder.ViewHolder> {
    @NonNull
    @Override
    protected PostsListViewBinder.ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_postlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull PostsListViewBinder.ViewHolder holder, @NonNull final PostsListBean item) {
        String publishedTime = item.getPublishedTime().substring(0, 10);
        String likesCount = item.getLikesCount() + "赞";
        String commentsCount = item.getCommentsCount() + "条评论";
        String titleImage = item.getTitleImage();
        String title = item.getTitle();

        if (!TextUtils.isEmpty(titleImage)) {
            titleImage = item.getTitleImage().replace("r.jpg", "b.jpg");
            Glide.with(holder.iv_titleImage.getContext()).load(titleImage).asBitmap().centerCrop().into(holder.iv_titleImage);
        } else {
            holder.iv_titleImage.setImageResource(R.drawable.error_image);
            holder.iv_titleImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        holder.tv_publishedTime.setText(publishedTime);
        holder.tv_likesCount.setText(likesCount);
        holder.tv_commentsCount.setText(commentsCount);
        holder.tv_title.setText(title);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostsContentView.launch(item.getTitleImage(), item.getTitle(), item.getSlug());
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView root;
        private TextView tv_publishedTime;
        private TextView tv_likesCount;
        private TextView tv_commentsCount;
        private ImageView iv_titleImage;
        private TextView tv_title;

        ViewHolder(View itemView) {
            super(itemView);
            this.root = (CardView) itemView.findViewById(R.id.root);
            this.tv_publishedTime = (TextView) itemView.findViewById(R.id.tv_publishedTime);
            this.tv_likesCount = (TextView) itemView.findViewById(R.id.tv_likesCount);
            this.tv_commentsCount = (TextView) itemView.findViewById(R.id.tv_commentsCount);
            this.iv_titleImage = (ImageView) itemView.findViewById(R.id.iv_titleImage);
            this.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

}
