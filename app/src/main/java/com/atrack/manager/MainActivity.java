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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private WebView webView;
    // Put your web domain here so in-app navigation stays in webview
    private static final String HOME_HOST = "server.atrack.com.pk";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // required for many web apps
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Important for responsive pages
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        // Optional - zoom support
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // Improve compatibility for links that open new windows or call window.open
        webView.setWebChromeClient(new WebChromeClient());

        // Custom WebViewClient to intercept intent://, geo:, maps and external links
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final String url = request.getUrl().toString();
                return handleUrl(url);
            }

            // For older devices (API < 24)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrl(url);
            }

            private boolean handleUrl(String url) {
                try {
                    Uri uri = Uri.parse(url);

                    // Handle intent:// URIs
                    if (url.startsWith("intent://")) {
                        try {
                            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                            if (intent != null) {
                                startActivity(intent);
                                return true;
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Could not parse intent:// uri", e);
                            // fallback: open as browser
                            openExternal(url);
                            return true;
                        }
                    }

                    // Handle geo: URIs (maps)
                    if ("geo".equals(uri.getScheme())) {
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(mapIntent);
                        return true;
                    }

                    // Common google maps link forms
                    String host = uri.getHost() == null ? "" : uri.getHost();
                    if (host.contains("google.com") && url.contains("/maps")) {
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(mapIntent);
                        return true;
                    }
                    if (host.contains("maps.app.goo")) {
                        // e.g. short links
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(mapIntent);
                        return true;
                    }

                    // If the link points to your domain, keep it in the WebView
                    if (host.equalsIgnoreCase(HOME_HOST) || host.endsWith("." + HOME_HOST)) {
                        return false; // let WebView load it
                    }

                    // Otherwise open external browser/app
                    openExternal(url);
                    return true;

                } catch (ActivityNotFoundException ex) {
                    Log.w(TAG, "No activity to handle URL: " + url, ex);
                    return false;
                } catch (Exception e) {
                    Log.e(TAG, "Error handling url: " + url, e);
                    return false;
                }
            }

            private void openExternal(String url) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    Log.w(TAG, "No app to open url: " + url, e);
                }
            }
        });

        // load your site
        webView.loadUrl("https://server.atrack.com.pk");
    }

    // Back button navigates WebView history instead of finishing activity
    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
