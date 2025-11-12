package com.atrack.manager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private WebView webView;
    private static final String HOME_HOST = "server.atrack.com.pk";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the app layout edge-to-edge but still handle safe insets
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);
        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);

        setContentView(R.layout.activity_main);

        CoordinatorLayout rootLayout = findViewById(R.id.rootLayout);
        webView = findViewById(R.id.webView);

        // Apply padding equal to system navigation area (gesture/nav bar)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(0, 0, 0, bottomInset);
            return insets;
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleUrl(request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrl(url);
            }

            private boolean handleUrl(String url) {
                try {
                    Uri uri = Uri.parse(url);
                    String host = uri.getHost() == null ? "" : uri.getHost();

                    // intent:// URIs
                    if (url.startsWith("intent://")) {
                        try {
                            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                            if (intent != null) {
                                startActivity(intent);
                                return true;
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Could not parse intent:// URI", e);
                            openExternal(url);
                            return true;
                        }
                    }

                    // geo: URIs
                    if ("geo".equals(uri.getScheme())) {
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(mapIntent);
                        return true;
                    }

                    // Google Maps links
                    if (host.contains("google.com") && url.contains("/maps")) {
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(mapIntent);
                        return true;
                    }
                    if (host.contains("maps.app.goo")) {
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(mapIntent);
                        return true;
                    }

                    // Keep links from your own domain inside WebView
                    if (host.equalsIgnoreCase(HOME_HOST) || host.endsWith("." + HOME_HOST)) {
                        return false;
                    }

                    // External links -> open outside
                    openExternal(url);
                    return true;

                } catch (ActivityNotFoundException ex) {
                    Log.w(TAG, "No activity to handle URL: " + url, ex);
                    return false;
                } catch (Exception e) {
                    Log.e(TAG, "Error handling URL: " + url, e);
                    return false;
                }
            }

            private void openExternal(String url) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    Log.w(TAG, "No app to open URL: " + url, e);
                }
            }
        });

        webView.loadUrl("https://server.atrack.com.pk");
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
