package com.lj.cameracontroller.utils;

import android.content.Context;

import com.lj.cameracontroller.utils.Json.JsonPreference;


/**
 * 存储仓库 
 * 1.临时缓存 
 * 2.SharedPreference 
 * 3.数据库
 */
public class StorageFactory {

	private static StorageFactory mInstance = null;

	private StorageFactory() {
	}

	public static synchronized StorageFactory getInstance() {
		if (mInstance == null) {
			mInstance = new StorageFactory();
		}
		return mInstance;
	}

//	/** 临时存储 */
//	public TempCache getTempCache() {
//		return TempCache.getInstance();
//	}

	/** 持久存储，SharedPreference */
	public JsonPreference getSharedPreference(Context context) {
		return JsonPreference.getInstance(context);
	}
//
//	/** 持久存储，database */
//	public DBHelper getDBHelper(Context context) {
//		return DBHelper.getInstance(context);
//	}
}
