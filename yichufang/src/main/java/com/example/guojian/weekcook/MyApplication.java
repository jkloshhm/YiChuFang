package com.example.guojian.weekcook;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by guojian on 11/16/16.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //imageLoader.init(ImageLoaderConfiguration.createDefault(MainActivity.this));
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration =
                new ImageLoaderConfiguration.Builder(getApplicationContext())
                //.createDefault(this);
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100)
                .writeDebugLogs()
                .build();
        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);
    }
}
