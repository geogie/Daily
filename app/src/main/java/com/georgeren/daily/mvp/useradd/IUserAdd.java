package com.georgeren.daily.mvp.useradd;

import com.georgeren.daily.mvp.base.IBasePresenter;
import com.georgeren.daily.mvp.base.IBaseView;
import com.georgeren.daily.mvp.bean.ZhuanlanBean;

import java.util.List;

/**
 * Created by georgeRen on 2017/8/30.
 */

public interface IUserAdd {
    interface View extends IBaseView<Presenter> {

        void onCheckInputId();

        void onSetAdapter(List<ZhuanlanBean> list);

        void onAddSuccess();

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

        void doCheckInputId(String input);

        void doSetAdapter();

        void onFail();

        void doRefresh();

        void doRemoveItem(int position);

        void doRemoveItemCancel(ZhuanlanBean bean);
    }
}
