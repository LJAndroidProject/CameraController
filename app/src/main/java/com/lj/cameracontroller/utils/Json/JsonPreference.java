/**
 * 
 */
package com.lj.cameracontroller.utils.Json;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


/**
 * 缓存工具类 对存json类型的Preference做进一步封装
 */
public class JsonPreference {

	private final String LOCAL_CACHE_FILE = "com.eshore.xxx.preferences";
	private Context ctx;
	private SharedPreferences sp = null;
	private static JsonPreference instance;

	private JsonPreference() {
	}

	private JsonPreference(Context ctx) {
		this.ctx = ctx;
	}

	public synchronized static JsonPreference getInstance(Context ctx) {
		if (instance == null) {
			instance = new JsonPreference(ctx);
		}
		return instance;
	}

	public void clearCache() {
		SharedPreferences.Editor editor = ctx.getSharedPreferences(LOCAL_CACHE_FILE, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}

	public <T> void saveDao(String key, T t) {
		String json = JsonMapperUtils.toJson(t);
		sp = ctx.getSharedPreferences(LOCAL_CACHE_FILE, Context.MODE_PRIVATE);
		sp.edit().putString(key, json).commit();
	}

	public <T> T getDao(String key, Class<T> clazz) {
		T t = null;
		sp = ctx.getSharedPreferences(LOCAL_CACHE_FILE, Context.MODE_PRIVATE);
		String json = sp.getString(key, null);
		if (!TextUtils.isEmpty(json)) {
			t = JsonMapperUtils.toObject(json, clazz);
		}

		return t;
	}

	public void saveString(String key, String value) {
		sp = ctx.getSharedPreferences(LOCAL_CACHE_FILE, Context.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
	}

	public String getString(String key) {
		sp = ctx.getSharedPreferences(LOCAL_CACHE_FILE, Context.MODE_PRIVATE);
		String value = sp.getString(key, null);
		return value;
	}

	public void saveBoolean(String key, boolean value) {
		sp = ctx.getSharedPreferences(LOCAL_CACHE_FILE, Context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();
	}

	public boolean getBoolean(String key) {
		sp = ctx.getSharedPreferences(LOCAL_CACHE_FILE, Context.MODE_PRIVATE);
		boolean value = sp.getBoolean(key, false);
		return value;
	}
	
	
	public void saveInt(String key, int value) {
		sp = ctx.getSharedPreferences(LOCAL_CACHE_FILE, Context.MODE_PRIVATE);
		sp.edit().putInt(key, value).commit();
	}

	public int getInt(String key) {
		sp = ctx.getSharedPreferences(LOCAL_CACHE_FILE, Context.MODE_PRIVATE);
		int value = sp.getInt(key, 0);
		return value;
	}
	
	
	
	
	
	
	
	

//	/**
//	 * 渠道标志
//	 */
//	public String getChannelIdentification() {
//		sp = ctx.getSharedPreferences(LOCAL_CACHE_FILE, Context.MODE_PRIVATE);
//
//		String channel = sp.getString(Common.CHANNEL_NAME, null);
//
//		if (TextUtils.isEmpty(channel)) {
//			channel = FileUtils.readMETAINFO(ctx);
//			saveChannelIdentification(channel);
//		}
//
//		return channel;
//	}
//
//	public void saveChannelIdentification(String channel) {
//		sp = ctx.getSharedPreferences(LOCAL_CACHE_FILE, Context.MODE_PRIVATE);
//		sp.edit().putString(Common.CHANNEL_NAME, channel).commit();
//	}

	/**
	 * 定位的地址信息
	 */
	public static final String KEY_POSITION_ADDR_INFO = "key_position_addr_info";

	public void savePositionInfo(String addrInfo) {
		sp = ctx.getSharedPreferences(LOCAL_CACHE_FILE, Context.MODE_PRIVATE);
		sp.edit().putString(KEY_POSITION_ADDR_INFO, addrInfo).commit();
	}

	public String getPositionInfo() {
		sp = ctx.getSharedPreferences(LOCAL_CACHE_FILE, Context.MODE_PRIVATE);
		return sp.getString(KEY_POSITION_ADDR_INFO, null);
	}

	/**
	 * 缓存引导页标识
	 */
	public final String KEY_INSTRUCTION_FLAG = "key_instruction_flag";

	public void setInstructionFlag(Context ctx, String version) {
		sp = ctx.getSharedPreferences(LOCAL_CACHE_FILE, Context.MODE_PRIVATE);
		sp.edit().putString(KEY_INSTRUCTION_FLAG, version).commit();
	}

	public String getInstructionFlag(Context ctx) {
		sp = ctx.getSharedPreferences(LOCAL_CACHE_FILE, Context.MODE_PRIVATE);
		return sp.getString(KEY_INSTRUCTION_FLAG, null);
	}

}
