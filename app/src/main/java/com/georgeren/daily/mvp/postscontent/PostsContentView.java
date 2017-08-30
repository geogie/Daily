package com.georgeren.daily.mvp.postscontent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.bumptech.glide.Glide;
import com.georgeren.daily.IApi;
import com.georgeren.daily.InitApp;
import com.georgeren.daily.R;
import com.georgeren.daily.injector.component.DaggerPostsContentComponent;
import com.georgeren.daily.injector.module.PostsContentModule;
import com.georgeren.daily.mvp.base.BaseActivity;
import com.georgeren.daily.utils.SettingUtil;

import static com.georgeren.daily.bean.PostsListBean.POSTSLISTBEAN_SLUG;
import static com.georgeren.daily.bean.PostsListBean.POSTSLISTBEAN_TITLE;
import static com.georgeren.daily.bean.PostsListBean.POSTSLISTBEAN_TITLEIMAGE;

/**
 * Created by georgeRen on 2017/8/30.
 */

public class PostsContentView extends BaseActivity<IPostsContent.Presenter> implements IPostsContent.View {
    private WebView webView;
    private MaterialDialog dialog;
    private CollapsingToolbarLayout toolbarLayout;
    private ImageView ivHeader;

    private String title;
    private int slug;

    public static void launch(String titleImage, String title, int slug) {
        InitApp.AppContext.startActivity(new Intent(InitApp.AppContext, PostsContentView.class)
                .putExtra(POSTSLISTBEAN_TITLEIMAGE, titleImage)
                .putExtra(POSTSLISTBEAN_TITLE, title)
                .putExtra(POSTSLISTBEAN_SLUG, slug)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void onSetWebView(String url) {
        webView.loadDataWithBaseURL(null, url, "text/html", "utf-8", null);
    }

    @Override
    public void onShowLoading() {
        dialog.show();
    }

    @Override
    public void onHideLoading() {
        dialog.dismiss();
    }

    @Override
    public void onShowNetError() {
        dialog.dismiss();
        Snackbar.make(webView, R.string.network_error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected int attachLayout() {
        return R.layout.activity_postscontent;
    }

    @Override
    protected void initViews() {
        ivHeader = (ImageView) findViewById(R.id.iv_titleimage);
        Toolbar toolbar_title = (Toolbar) findViewById(R.id.toolbar_title);
        webView = (WebView) findViewById(R.id.webview_content);
        FloatingActionButton fab_share = (FloatingActionButton) findViewById(R.id.fab_share);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_layout);
        final NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.scrollView);

        initToolBar(toolbar_title, true, null);

        toolbar_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0, 0);
            }
        });

        fab_share.setBackgroundTintList(ColorStateList.valueOf(SettingUtil.getInstance().getColor()));
        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent()
                        .setAction(Intent.ACTION_SEND)
                        .setType("text/plain");
                String shareText = title + " " + IApi.POST_URL + slug;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
            }
        });

        toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        dialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .content(R.string.md_loading)
                .theme(SettingUtil.getInstance().getIsNightMode() ? Theme.DARK : Theme.LIGHT)
                .cancelable(true)
                .build();

        initWebClient();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        String titleImage = intent.getStringExtra(POSTSLISTBEAN_TITLEIMAGE);
        title = intent.getStringExtra(POSTSLISTBEAN_TITLE);
        slug = intent.getIntExtra(POSTSLISTBEAN_SLUG, 0);

        toolbarLayout.setTitle(title);
        if (TextUtils.isEmpty(titleImage)) {
            ivHeader.setImageResource(R.drawable.error_image);
            ivHeader.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            Glide.with(this).load(titleImage).centerCrop().into(ivHeader);
        }

        presenter.doRequestData(slug);
    }

    @Override
    protected void initInjector() {
        DaggerPostsContentComponent.builder()
                .postsContentModule(new PostsContentModule(this))
                .build()
                .inject(this);
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebClient() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        // 缩放,设置为不能缩放可以防止页面上出现放大和缩小的图标
        settings.setBuiltInZoomControls(false);
        // 缓存
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启DOM storage API功能
        settings.setDomStorageEnabled(true);
        // 开启application Cache功能
        settings.setAppCacheEnabled(false);
        // 不调用第三方浏览器即可进行页面反应
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });
    }
}
