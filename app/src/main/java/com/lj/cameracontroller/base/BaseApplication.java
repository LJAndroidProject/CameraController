package com.lj.cameracontroller.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.lj.cameracontroller.constant.UserApi;
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
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
        setBugly();
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


    //初始化Bugly
    private void setBugly() {
        // 获取当前包名
        String packageName = getApplicationContext().getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        CrashReport.initCrashReport(getApplicationContext(), UserApi.Bugly_ID, true, strategy);
    }
    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
