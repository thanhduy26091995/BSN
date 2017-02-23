package com.hbbmobile.bsnapp.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hbbmobile.bsnapp.R;

/**
 * Created by buivu on 17/01/2017.
 */
public class NewsFragment extends Fragment {

    private String postUrl = "http://bstyle.vn/";
    private WebView webView;
    private ProgressBar progressBar;
    private float m_downX;
    private boolean flag = false;
    private int count = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main_browser, container, false);

        webView = (WebView) rootView.findViewById(R.id.webView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);


        if (!TextUtils.isEmpty(getActivity().getIntent().getStringExtra("postUrl"))) {
            postUrl = getActivity().getIntent().getStringExtra("postUrl");
        }


        initWebViews();
       // webView.setWebViewClient(new WebViewClient());
        renderPost();

        return rootView;
    }

    private void initWebViews() {
        webView.setWebViewClient(new WebViewClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /**
                 * Check for the url, if the url is from same domain
                 * open the url in the same activity as new intent
                 * else pass the url to browser activity
                 * */
                if (Utils.isSameDomain(postUrl, url)) {
                    openAppInBrowser(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getPointerCount() > 1) {
                    //multi touch
                    return true;
                }
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        m_downX = motionEvent.getX();
                    }
                    break;
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        motionEvent.setLocation(m_downX, motionEvent.getY());
                    }
                    break;
                }
                return false;
            }
        });
    }

    private void renderPost() {
        webView.loadUrl(postUrl);
    }

    private void openAppInBrowser(String url) {
        Intent intent = new Intent(getActivity(), BrowserActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        public MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }
    }

}
