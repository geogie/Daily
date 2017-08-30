package com.georgeren.daily.mvp.postscontent;

import com.georgeren.daily.mvp.base.IBasePresenter;
import com.georgeren.daily.mvp.base.IBaseView;

/**
 * Created by georgeRen on 2017/8/30.
 */

public interface IPostsContent {
    interface View extends IBaseView<Presenter> {

        /**
         * 加载网页
         */
        void onSetWebView(String url);

        /**
         * 显示加载动画
         */
        void onShowLoading();

        /**
         * 隐藏加载
         */
        void onHideLoading();

        /**
         * 显示网络错误
         */
        void onShowNetError();
    }

    interface Presenter extends IBasePresenter {

        /**
         * 请求数据
         */
        void doRequestData(int slug);
    }
}
