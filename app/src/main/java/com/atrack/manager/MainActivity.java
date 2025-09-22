package com.atrack.manager;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // use our layout with logo + WebView

        WebView webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();

        // ✅ Must-haves
        webSettings.setJavaScriptEnabled(true);        // enable JS
        webSettings.setDomStorageEnabled(true);        // enable local/session storage
        webSettings.setAllowFileAccess(true);          // allow loading files
        webSettings.setAllowContentAccess(true);       // allow content access
        webSettings.setLoadsImagesAutomatically(true); // ensure images load
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // ✅ Important: allow HTTPS page to load custom.js
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Debugging (inspect via chrome://inspect)
        WebView.setWebContentsDebuggingEnabled(true);

        // Keep navigation inside the WebView
        webView.setWebViewClient(new WebViewClient());

        // Load your server
        webView.loadUrl("https://server.atrack.com.pk");
    }
}
