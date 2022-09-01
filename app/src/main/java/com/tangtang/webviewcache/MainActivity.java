package com.tangtang.webviewcache;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wv = findViewById(R.id.web_view);
        initWebView();
    }

    public void initWebView(){
        WebViewUtils utils = new WebViewUtils(wv, this);
        utils.loadUrl(Contacts.HTML);
    }

}