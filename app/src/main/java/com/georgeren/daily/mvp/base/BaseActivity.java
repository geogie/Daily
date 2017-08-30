package com.georgeren.daily.mvp.base;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.color.CircleView;
import com.georgeren.daily.R;
import com.georgeren.daily.utils.SettingUtil;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by georgeRen on 2017/8/28.
 */

public abstract class BaseActivity <T extends IBasePresenter> extends RxAppCompatActivity implements IBaseView<T>{
    @Inject
    protected T presenter;
    protected MultiTypeAdapter adapter;
    protected boolean canLoadMore;

    protected abstract int attachLayout();
    protected  abstract void initViews();
    protected abstract void initData();
    protected abstract void initInjector();
    protected void initTheme(){
        boolean isNigtMode = SettingUtil.getInstance().getIsNightMode();
        if (isNigtMode){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.LightTheme);
        }
    }
    protected void initToolBar(Toolbar toolbar, boolean homeAsUpEnable, String title){
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUpEnable);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        initInjector();
        setContentView(attachLayout());
        initViews();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int color = SettingUtil.getInstance().getColor();
        if (getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(CircleView.shiftColorDown(color));
            getWindow().setNavigationBarColor(color);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * 绑定生命周期
     */
    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return bindUntilEvent(ActivityEvent.DESTROY);
    }
}
