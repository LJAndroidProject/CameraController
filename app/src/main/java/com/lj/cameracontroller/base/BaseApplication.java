package com.lj.cameracontroller.base;

import android.app.Application;
import android.content.Context;

import com.lj.cameracontroller.utils.FileUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

/**
 * Created by 刘劲松 on 2017/6/28.
 * App的基类
 */

public class BaseApplication extends Application {

    //全局静态应用上下文对象
    private static Context AppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext = getApplicationContext();
        initImageLoader();
    }

    //TODO 静态全局获取应用上下文对象
    public static Context getAppContext(){
        return AppContext;
    }

    // 初始化异步显示图片控件的配置
    public void initImageLoader(){
        // 使用universal-image-loader
        try {
            //add by chenrb 2015-12-30，使用缓存
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)  //1.8.6包使用时候，括号里面传入参数true
                    .cacheOnDisk(true)    //1.8.6包使用时候，括号里面传入参数true
                    .build();
            //end add by chenrb 2015-12-30，使用缓存

            File cacheFile = new File(FileUtils.getExternalCacheDir(this));
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                    // .offOutOfMemoryHandling()
                    .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    // 1.5 Mb
                    .discCacheFileNameGenerator(new Md5FileNameGenerator())
//					.discCache(
//							new TotalSizeLimitedDiscCache(cacheFile
//									/*RemoteImageView.getCacheImageFileDir(this, RemoteImageView.FILEDIR_STR)*/, 10000000)) // 10000000
                    .diskCache(
                            new UnlimitedDiscCache(cacheFile, null,
                                    new Md5FileNameGenerator()))// 自定义缓存路径
                    .memoryCache(new FIFOLimitedMemoryCache(5000000)) // 5000000
                    .defaultDisplayImageOptions(defaultOptions)//add by chenrb 2015-12-30，使用缓存
                    .writeDebugLogs() // Not
                    .build();

            // Initialize ImageLoader with configuration.
            ImageLoader.getInstance().init(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
