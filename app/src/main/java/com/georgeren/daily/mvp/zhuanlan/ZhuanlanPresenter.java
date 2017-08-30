package com.georgeren.daily.mvp.zhuanlan;

import com.georgeren.daily.IApi;
import com.georgeren.daily.InitApp;
import com.georgeren.daily.R;
import com.georgeren.daily.RetrofitFactory;
import com.georgeren.daily.database.dao.ZhuanlanDao;
import com.georgeren.daily.mvp.bean.ZhuanlanBean;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by georgeRen on 2017/8/29.
 *
 */

public class ZhuanlanPresenter implements IZhuanlan.Presenter {
    public static final int TYPE_PRODUCT = 0;// 产品·创业·互联网
    public static final int TYPE_MUSIC = 1;// 生活·旅游·杂志
    public static final int TYPE_LIFE = 2;// 音乐·影视·摄影
    public static final int TYPE_EMOTION = 3;// 健康·感情·心理
    public static final int TYPE_FINANCE = 4;// 专业领域
    public static final int TYPE_ZHIHU = 5;// 知乎·程序员
    public static final int TYPE_USERADD = 6;// 自定义
    private static final String TAG = "ZhuanlanPresenter";
    private IZhuanlan.View view;
    private ZhuanlanDao dao = new ZhuanlanDao();
    private Call<ZhuanlanBean> call;
    private String[] ids;
    private int type;

    public ZhuanlanPresenter(IZhuanlan.View view, int type) {
        this.view = view;
        this.type = type;
    }
    @Override
    public void doLoading() {
        view.onShowLoading();

        Observable
                .create(new ObservableOnSubscribe<List<ZhuanlanBean>>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<List<ZhuanlanBean>> e) throws Exception {// RxCachedThreadScheduler-1
                        Logger.d("type:"+type);
                        e.onNext(dao.query(type));
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<List<ZhuanlanBean>, Observable<List<ZhuanlanBean>>>() {
                    @Override
                    public Observable<List<ZhuanlanBean>> apply(@NonNull List<ZhuanlanBean> list) throws Exception {// RxCachedThreadScheduler-1
                        Logger.d("flatMap:"+type);
                        if (null != list && list.size() > 0) {
                            return Observable.just(list);
                        } else {
                            list = retrofitRequest();
                            return Observable.just(list);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(view.<List<ZhuanlanBean>>bindToLife())
                .subscribe(new Consumer<List<ZhuanlanBean>>() {
                    @Override
                    public void accept(@NonNull List<ZhuanlanBean> list) throws Exception {// main
                        Logger.d("subscribe:"+type);
                        if (null != list && list.size() > 0) {
                            doSetAdapter(list);
                        } else {
                            doShowFail();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        doShowFail();
                    }
                });
    }

    @Override
    public void doSetAdapter(List<ZhuanlanBean> list) {
        view.onSetAdapter(list);
        view.onHideLoading();
    }

    @Override
    public void doShowFail() {
        view.onHideLoading();
        view.onShowNetError();
    }

    @Override
    public void doRefresh() {
        view.onShowLoading();
        view.onRequestData();
    }

    @Override
    public void onDestroy() {
        if (call != null && call.isCanceled()) {
            call.cancel();
        }
    }


    private List<ZhuanlanBean> retrofitRequest() {

        switch (type) {
            case TYPE_PRODUCT:
                ids = InitApp.AppContext.getResources().getStringArray(R.array.product);
                break;
            case TYPE_MUSIC:
                ids = InitApp.AppContext.getResources().getStringArray(R.array.music);
                break;
            case TYPE_LIFE:
                ids = InitApp.AppContext.getResources().getStringArray(R.array.life);
                break;
            case TYPE_EMOTION:
                ids = InitApp.AppContext.getResources().getStringArray(R.array.emotion);
                break;
            case TYPE_FINANCE:
                ids = InitApp.AppContext.getResources().getStringArray(R.array.profession);
                break;
            case TYPE_ZHIHU:
                ids = InitApp.AppContext.getResources().getStringArray(R.array.zhihu);
                break;
        }

        final List<ZhuanlanBean> list = new ArrayList<>();

        IApi IApi = RetrofitFactory.getRetrofit().create(IApi.class);
        for (String id : ids) {
            call = IApi.getZhuanlanBean(id);
            try {
                Response<ZhuanlanBean> response = call.execute();
                if (response.isSuccessful()) {
                    list.add(response.body());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (ZhuanlanBean bean : list) {
            dao.add(type, bean);
        }

        return list;
    }

}
