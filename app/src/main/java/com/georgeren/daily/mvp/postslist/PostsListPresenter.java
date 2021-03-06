package com.georgeren.daily.mvp.postslist;

import com.georgeren.daily.IApi;
import com.georgeren.daily.RetrofitFactory;
import com.georgeren.daily.bean.PostsListBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by georgeRen on 2017/8/30.
 * 专栏presenter
 */

public class PostsListPresenter implements IPostsList.Presenter{
    private IPostsList.View view;
    private List<PostsListBean> list = new ArrayList<>();

    public PostsListPresenter(IPostsList.View view) {
        this.view = view;
    }

    /**
     * 请求数据：包含加载更多
     * @param slug 类型
     * @param offset 加载更多偏移量
     */
    @Override
    public void doRequestData(String slug, int offset) {
        RetrofitFactory.getRetrofit().create(IApi.class).getPostsListRx(slug, offset)
                .subscribeOn(Schedulers.io())// 子线程中网络获取数据
                .observeOn(AndroidSchedulers.mainThread())// 主线程中拿数据
                .compose(view.<List<PostsListBean>>bindToLife())// 绑定生命周期
                .subscribe(new Consumer<List<PostsListBean>>() {
                    @Override
                    public void accept(@NonNull List<PostsListBean> postsListBeen) throws Exception {
                        list.addAll(postsListBeen);
                        doSetAdapter();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        onFail();
                    }
                });
    }

    @Override
    public void doSetAdapter() {
        view.onSetAdapter(list);
        view.onHideLoading();
    }

    @Override
    public void doRefresh() {
        list.clear();
        view.onRequestData();
    }

    @Override
    public void onFail() {
        view.onHideLoading();
        view.onShowNetError();
    }
}
