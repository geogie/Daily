package com.georgeren.daily.utils;

import android.support.v7.util.DiffUtil;

import com.georgeren.daily.bean.PostsListBean;
import com.georgeren.daily.mvp.bean.ZhuanlanBean;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by georgeRen on 2017/8/30.
 * 摒弃：adapter无脑刷新
 * http://blog.csdn.net/zxt0601/article/details/52562770
 */

public class DiffCallback extends DiffUtil.Callback {

    public static final int POSTSLIST = 0;// 详情
    public static final int ZHUANLAN = 1;// 专栏
    private Items oldItems, newItems;
    private int type;

    private DiffCallback(Items oldItems, Items newItems, int type) {
        this.oldItems = oldItems;
        this.newItems = newItems;
        this.type = type;
    }

    public static void create(Items oldItems, Items newItems, int type, MultiTypeAdapter adapter) {
        DiffCallback diffCallback = new DiffCallback(oldItems, newItems, type);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffCallback, true);
        result.dispatchUpdatesTo(adapter);
    }

    @Override
    public int getOldListSize() {
        return oldItems != null ? oldItems.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newItems != null ? newItems.size() : 0;
    }

    /**
     * item 是否是同一个
     * @param oldItemPosition
     * @param newItemPosition
     * @return
     */
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        try {
            if (type == POSTSLIST) {// 根据title判断是否是 同一个数据
                boolean equals = ((PostsListBean) oldItems.get(oldItemPosition)).getTitle()
                        .equals(((PostsListBean) newItems.get(newItemPosition)).getTitle());
                return equals;
            }
            if (type == ZHUANLAN) {// 根据name判断是否是 同一个数据
                boolean equals = ((ZhuanlanBean) oldItems.get(oldItemPosition)).getName()
                        .equals(((ZhuanlanBean) newItems.get(newItemPosition)).getName());
                return equals;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 内容是否相同
     * @param oldItemPosition
     * @param newItemPosition
     * @return
     */
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        try {
            if (type == POSTSLIST) {
                boolean equals = ((PostsListBean) oldItems.get(oldItemPosition)).getContent()
                        .equals(((PostsListBean) newItems.get(newItemPosition)).getContent());
                return equals;
            }
            if (type == ZHUANLAN) {
                boolean equals = ((ZhuanlanBean) oldItems.get(oldItemPosition)).getIntro()
                        .equals(((ZhuanlanBean) newItems.get(newItemPosition)).getIntro());
                return equals;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
