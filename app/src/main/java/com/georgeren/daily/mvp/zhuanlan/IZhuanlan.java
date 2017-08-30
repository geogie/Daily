package com.georgeren.daily.mvp.zhuanlan;

import com.georgeren.daily.mvp.base.IBasePresenter;
import com.georgeren.daily.mvp.base.IBaseView;
import com.georgeren.daily.mvp.bean.ZhuanlanBean;

import java.util.List;

/**
 * Created by georgeRen on 2017/8/29.
 */

public interface IZhuanlan {
    interface View extends IBaseView<Presenter> {

        /**
         * 请求数据
         */
        void onRequestData();

        /**
         * 设置适配器
         */
        void onSetAdapter(List<ZhuanlanBean> list);

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
         * 获取专栏类型
         */
        void doLoading();

        /**
         * 设置适配器
         */
        void doSetAdapter(List<ZhuanlanBean> list);

        /**
         * 显示查询数据失败
         */
        void doShowFail();

        /**
         * 正在刷新
         */
        void doRefresh();

        /**
         * 取消网络请求
         */
        void onDestroy();
    }
}
