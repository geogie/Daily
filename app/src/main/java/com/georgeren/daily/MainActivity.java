package com.georgeren.daily;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.color.CircleView;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.georgeren.daily.mvp.base.BaseActivity;
import com.georgeren.daily.mvp.base.IBasePresenter;
import com.georgeren.daily.mvp.useradd.UserAddView;
import com.georgeren.daily.mvp.zhuanlan.ZhuanlanPresenter;
import com.georgeren.daily.mvp.zhuanlan.ZhuanlanView;
import com.georgeren.daily.utils.SettingUtil;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class MainActivity extends BaseActivity<IBasePresenter> implements NavigationView.OnNavigationItemSelectedListener
        , ColorChooserDialog.ColorCallback {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private long exitTime;
    private Observable<Boolean> observable;
    private SwitchCompat switchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observable = RxBus.getInstance().register(Boolean.class);
        observable.subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean isNightMode) throws Exception {
                showAnimation();
                refreshUI();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unregister(Boolean.class, observable);
        super.onDestroy();
    }

    @Override
    protected int attachLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolBar(toolbar, false, null);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        switchInput = navigationView.getMenu().findItem(R.id.app_bar_switch).getActionView().findViewById(R.id.switch_input);
        switchInput.setChecked(SettingUtil.getInstance().getIsNightMode());

        setUpSwitch();

        switchInput.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isNightMode) {
                SettingUtil.getInstance().setIsNightMode(isNightMode);
                setUpSwitch();
                if (isNightMode) {
                    setTheme(R.style.DarkTheme);
                } else {
                    setTheme(R.style.LightTheme);
                }
                RxBus.getInstance().post(isNightMode);
            }
        });
    }

    @Override
    protected void initData() {
        replaceFragment(ZhuanlanPresenter.TYPE_PRODUCT);
        navigationView.setCheckedItem(R.id.nav_product);
    }

    @Override
    protected void initInjector() {

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_product) {// 产品
            replaceFragment(ZhuanlanPresenter.TYPE_PRODUCT);

        } else if (id == R.id.nav_life) {// 生活
            replaceFragment(ZhuanlanPresenter.TYPE_LIFE);

        } else if (id == R.id.nav_music) {// 音乐
            replaceFragment(ZhuanlanPresenter.TYPE_MUSIC);

        } else if (id == R.id.nav_emotion) {// 健康
            replaceFragment(ZhuanlanPresenter.TYPE_EMOTION);

        } else if (id == R.id.nav_profession) {// 专业
            replaceFragment(ZhuanlanPresenter.TYPE_FINANCE);

        } else if (id == R.id.nav_zhihu) {// 知乎
            replaceFragment(ZhuanlanPresenter.TYPE_ZHIHU);

        } else if (id == R.id.nav_user_add) {// 自定义
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new UserAddView()).commit();

        } else if (id == R.id.nav_color_chooser) {// 切换主题
            createColorChooserDialog();

        } else if (id == R.id.nav_about) {// 关于
            startActivity(new Intent(this, AboutActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(selectedColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(CircleView.shiftColorDown(selectedColor));
            getWindow().setNavigationBarColor(selectedColor);
        }
        if (!dialog.isAccentMode()) {
            SettingUtil.getInstance().setColor(selectedColor);
        }
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {
        setUpSwitch();
    }

    private void showAnimation() {
        final View decorview = getWindow().getDecorView();
        Bitmap cacheBitmap = getCacheBitmapFromView(decorview);
        if (decorview instanceof ViewGroup && cacheBitmap != null) {
            final View view = new View(this);
            view.setBackground(new BitmapDrawable((getResources()), cacheBitmap));
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            ((ViewGroup) decorview).addView(view, layoutParams);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
            objectAnimator.setDuration(300);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ((ViewGroup) decorview).removeView(view);
                }
            });
            objectAnimator.start();
        }
    }

    private Bitmap getCacheBitmapFromView(View view) {
        final boolean drawingCacheEnable = true;
        view.setDrawingCacheEnabled(drawingCacheEnable);
        view.buildDrawingCache(drawingCacheEnable);
        final Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }
        return bitmap;
    }

    protected void refreshUI() {
        Resources.Theme theme = getTheme();
        TypedValue rootViewBackground = new TypedValue();
        TypedValue textColorPrimary = new TypedValue();
        theme.resolveAttribute(R.attr.rootViewBackground, rootViewBackground, true);
        theme.resolveAttribute(R.attr.textColorPrimary, textColorPrimary, true);
        Resources resources = getResources();
        navigationView.setBackgroundResource(rootViewBackground.resourceId);
        navigationView.setItemBackgroundResource(rootViewBackground.resourceId);
        navigationView.setItemTextColor(resources.getColorStateList(textColorPrimary.resourceId));
        navigationView.setItemIconTintList(resources.getColorStateList(textColorPrimary.resourceId));
    }

    /**
     * 复用：产品、生活、音乐、健康、专业、知乎等复用
     * @param type
     */
    private void replaceFragment(int type) {
        ZhuanlanView fragment = ZhuanlanView.newInstance(type);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
    }
    /**
     * 初始化 Toolbar
     *
     * @param toolbar
     * @param homeAsUpEnabled
     * @param title
     */
    protected void initToolBar(Toolbar toolbar, boolean homeAsUpEnabled, String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUpEnabled);
    }
    private void setUpSwitch() {
        boolean isNightMode = SettingUtil.getInstance().getIsNightMode();
        if (isNightMode) {
            switchInput.setThumbTintList(ColorStateList.valueOf(SettingUtil.getInstance().getColor()));
        } else {
            Resources.Theme theme = getTheme();
            Resources resources = getResources();
            TypedValue textColorPrimary = new TypedValue();
            theme.resolveAttribute(R.attr.textColorPrimary, textColorPrimary, true);
            switchInput.setThumbTintList(resources.getColorStateList(textColorPrimary.resourceId));
        }
    }
    private void createColorChooserDialog() {
        new ColorChooserDialog.Builder(this, R.string.md_color_chooser_title)
                .doneButton(R.string.md_done)
                .cancelButton(R.string.md_cancel)
                .allowUserColorInput(true)
                .allowUserColorInputAlpha(false)
                .theme(SettingUtil.getInstance().getIsNightMode() ? Theme.DARK : Theme.LIGHT)
                .customButton(R.string.md_custom)
                .show();
    }
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if ((currentTime - exitTime) < 2000) {
            super.onBackPressed();
        } else {
            Snackbar.make(drawerLayout, getString(R.string.double_click_exit), Snackbar.LENGTH_SHORT).show();
            exitTime = currentTime;
        }
    }
}
