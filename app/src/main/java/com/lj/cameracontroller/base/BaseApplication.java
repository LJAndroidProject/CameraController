package com.lj.cameracontroller.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.lj.cameracontroller.entity.UserInfo;
import com.lj.cameracontroller.utils.FileUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

/**
 * Created by 刘劲松 on 2017/6/28.
 * App的基类
 */

public class BaseApplication extends Application {

    //全局静态应用上下文对象
    private static Context AppContext;
    public static UserInfo userInfo;

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

    //TODO 静态全局获取用户信息
    public static UserInfo getUserInfo(){
        return userInfo;
    }
    public void initImageLoader(){
        // 使用universal-image-loader
        try {
            //add by chenrb 2015-12-30，使用缓存
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)  //1.8.6包使用时候，括号里面传入参数true
                    .cacheOnDisk(true)    //1.8.6包使用时候，括号里面传入参数true
                    .build();
            //end add by chenrb 2015-12-30，使用缓存

            File cacheFile = new File(FileUtils.getExternalCacheDir(AppContext));
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(AppContext)
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
            Log.e("初始化报错","e="+e.toString());
            e.printStackTrace();
        }

    }
}
