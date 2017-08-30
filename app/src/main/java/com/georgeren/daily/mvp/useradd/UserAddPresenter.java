package com.georgeren.daily.mvp.useradd;

import com.georgeren.daily.IApi;
import com.georgeren.daily.RetrofitFactory;
import com.georgeren.daily.database.dao.ZhuanlanDao;
import com.georgeren.daily.mvp.bean.ZhuanlanBean;
import com.georgeren.daily.mvp.zhuanlan.ZhuanlanPresenter;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by georgeRen on 2017/8/30.
 * 添加专栏presenter
 */

public class UserAddPresenter implements IUserAdd.Presenter{
    private IUserAdd.View view;
    private ZhuanlanDao dao = new ZhuanlanDao();
    private List<ZhuanlanBean> list;

    public UserAddPresenter(IUserAdd.View view) {
        this.view = view;
    }
    @Override
    public void doCheckInputId(String input) {
        view.onShowLoading();
        RetrofitFactory.getRetrofit().create(IApi.class).getZhuanlanBeanRx(input)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<ZhuanlanBean>() {
                    @Override
                    public void accept(@NonNull ZhuanlanBean bean) throws Exception {
                        dao.add(ZhuanlanPresenter.TYPE_USERADD, bean);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ZhuanlanBean>() {
                    @Override
                    public void accept(@NonNull ZhuanlanBean bean) throws Exception {
                        view.onAddSuccess();
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
        Observable
                .create(new ObservableOnSubscribe<List<ZhuanlanBean>>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<List<ZhuanlanBean>> e) throws Exception {
                        list = dao.query(ZhuanlanPresenter.TYPE_USERADD);
                        e.onNext(list);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(view.<List<ZhuanlanBean>>bindToLife())
                .subscribe(new Consumer<List<ZhuanlanBean>>() {
                    @Override
                    public void accept(@NonNull List<ZhuanlanBean> list) throws Exception {
                        view.onSetAdapter(list);
                        view.onHideLoading();
                    }
                });
    }

    @Override
    public void onFail() {
        view.onHideLoading();
        view.onShowNetError();
    }

    @Override
    public void doRefresh() {
        view.onShowLoading();
        doSetAdapter();
    }

    @Override
    public void doRemoveItem(final int position) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
                final ZhuanlanBean bean = list.get(position);
                dao.removeSlug(bean.getSlug());
                doSetAdapter();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void doRemoveItemCancel(final ZhuanlanBean bean) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
                dao.add(ZhuanlanPresenter.TYPE_USERADD, bean);
                doSetAdapter();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }
}
