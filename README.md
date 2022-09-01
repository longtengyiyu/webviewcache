# android webview加载H5富文本缓存实现

使用webview加载下面这段H5富文本标签，里面包含图片、视频等标签，会有网络差的情况需要缓存到本地。

```
<h1><span style="font-size:36px">这款右斜纹牛仔连衣裙特别采用撕裂和修补、梭织贴布、刺绣和串珠细节，打造出 Polo 的专属风格。 </span></h1><ol><li><span style="font-size:36px">上身修身下身宽松式廓形。长及膝盖。</span></li><li><span style="font-size:36px">​M 号尺码后衣长 41.9 厘米，胸围 102.9 厘米，腰围 88.9 厘米，臀围 105.4 厘米，袖长 61.0 厘米。每个尺码之间袖长相差 1.9 厘米。​</span></li><li><span style="font-size:36px">​宽角领。纽扣式门襟。右领和衣襟采用对比色缝线。</span></li></ol><ul><li><span style="font-size:36px">长袖，纽扣式单袖口。右侧袖子处有几何刺绣。左侧袖口有珠状刺绣。</span></li><li><span style="font-size:36px"><span style="color:#d26b6b">胸前两侧有纽扣式翻盖口袋。右侧口袋对比色翻盖。左胸、右臀和右肘处有撕裂和修补补片。右侧下摆处有法兰绒梭织补片。</span></span></li><li><span style="font-size:36px"><em>背面有纽扣式腰袢。后左肩有小鸟刺绣。背后和后下摆做旧设计。左臀处有三个箭头刺绣。后下摆右侧有对比色补片。</em></span></li><li><span style="font-size:36px"><span style="letter-spacing:2px">外层：棉。撕裂和修补补片：棉、亚麻。</span></span></li><li><span style="font-size:36px"><span style="line-height:1.5">使用皮革专用清洁剂干洗。进口。</span></span></li><li><span style="font-size:64px">模特身高 178 厘米，胸围 81.3 厘米，腰围 61.0 厘米，臀围 86.4 厘米。她穿 S 号尺码。</span></li><li><span style="font-size:36px"><sub>产品图片及颜色可能因拍照光线、</sub>环境及角度误差或屏幕设定等影响，可能与实物产品颜色有所差异，请以实物为准。</span>💛💛💚💚</li></ul><ol><li>下面是网络图片</li></ol><p></p><div class="media-wrap image-wrap"><img class="media-wrap image-wrap" src="http://terminalfs.rongyi.com/system/prod/file/202207141509273451.jpg" width="400px" height="400px" style="width:400px;height:400px"/></div><p></p><ol><li>网络视频</li><li></li></ol><div class="media-wrap video-wrap"><video controls="" class="media-wrap video-wrap" src="http://terminalfs.rongyi.com/system/prod/file/202206281656208323.mp4"></video></div><p></p><pre><code>&lt;video width=&quot;320&quot; height=&quot;240&quot; controls&gt;<br/>  &lt;source src=&quot;movie.mp4&quot;  type=&quot;video/mp4&quot;&gt;<br/>  &lt;source src=&quot;movie.ogg&quot;  type=&quot;video/ogg&quot;&gt;<br/>  您的浏览器不支持 HTML5 video 标签。<br/>&lt;/video&gt;</code></pre>
```

使用webview自带的缓存机制开启缓存

```
    mWebSettings.setAppCacheEnabled(true);
    //默认模式
    mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
    //也可设置此缓存模式，在有网的情况加载否则加载缓存
    //mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    mWebSettings.setDomStorageEnabled(true);
    mWebSettings.setDatabaseEnabled(true);
    //对数据进行512mb缓存，防止本地加载失败
    String cacheDirPath = Contacts.APP_CACHE_PATH;
    mWebSettings.setDatabasePath(cacheDirPath);
    mWebSettings.setAppCachePath(cacheDirPath);
    mWebSettings.setAppCacheMaxSize(512 * 1024 * 1024);
```

webview自带的缓存会有很多弊端，比如只会缓存图片，我们需要缓存其他数据就不行了。还有就是多次加载的场景会出现加载图片出现裂图。

此时，需要我们自己去下载，实现缓存，监听WebViewClient的shouldInterceptRequest，我们会得到加载链接中的所有资源，我们将这些资源归纳下载，完成后替换

```
@Nullable
      @Override
      public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          String url = request.getUrl().toString();
          Log.d(TAG, "shouldInterceptRequest url -->" + url);
          collectResource(url);
        }
        return super.shouldInterceptRequest(view, request);
      }
```

```
//开始下载
      downloadResourcesManager.downloadResources(resourceList, (completed, failed, total, u, status) -> {
        Log.d(TAG, "asyncDownloadResource url -->" + u);
        if (completed >= total){
          //下载完成,替换之前的url
          for (Resource source : resourceList) {
            String name = FileUtils.getFileName(source.getUrl());
            String localPicUrl = FileUtils.getLocalPath(name);
            Log.d(TAG, "asyncDownloadResource localPicUrl -->" + localPicUrl + " url:" + source.getUrl());
            url = url.replace(source.getUrl(), localPicUrl);
          }
          //刷新
          if (isNeedRefresh){
            load(url);
          }
        }
      });
```

