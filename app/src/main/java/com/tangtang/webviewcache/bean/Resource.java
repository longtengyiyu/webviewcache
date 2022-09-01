package com.tangtang.webviewcache.bean;

import android.text.TextUtils;

import com.tangtang.webviewcache.Contacts;
import com.tangtang.webviewcache.FileUtils;

public class Resource implements Downloadable {

    private String url;
    private String local;

    @Override
    public String url() {
        return url;
    }

    @Override
    public String local() {
        if (TextUtils.isEmpty(local)){
            local = Contacts.APP_ROOT_PATH + FileUtils.getFileName(url);
        }
        return local;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }
}
