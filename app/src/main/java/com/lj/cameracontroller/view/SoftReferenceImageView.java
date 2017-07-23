package com.lj.cameracontroller.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lj.cameracontroller.base.BaseApplication;
import com.lj.cameracontroller.utils.FileUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * 异步加载图片
 * 
 * 改用第三方控件ImageLoader加载图片（2015-9-1 by crb）
 * 
 */
public class SoftReferenceImageView extends ImageView {

	/** ******************************默认设置 start ***********************************/

	private boolean IS_SAVE_LOCAL = false; // 是否把图片保存本地文件，默认false
	private static final String Cache_Dir = "ipc/picture/"; // SD卡 目录名称
															// ，默认background

	/** ******************************默认设置 end **********************************/

	private Context context;
	private View view;
	private ScaleType scaleType;

	/**
	 * Default image shown while loading or on url not found
	 */
	private Integer mDefaultImage;

	/**
	 * 内存图片软引用缓冲
	 */
	public static LinkedHashMap<String, SoftReference<Bitmap>> imageCache = new LinkedHashMap<String, SoftReference<Bitmap>>(
			20);

	public SoftReferenceImageView(Context context) {
		super(context);
		this.context = context;
	}

	public SoftReferenceImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public SoftReferenceImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}
	
	/**
	 * Sets default local image shown when remote one is unavailable
	 * 
	 * @param resid
	 */
	public void setDefaultImage(Integer resid) {

		mDefaultImage = resid;
		setImageResource(resid);

	}

	/**
	 * 加载默认图片
	 */
	public void loadDefaultImage() {
		if (mDefaultImage != null) {
			setImageResource(mDefaultImage);
		}
	}

	/**
	 * 设置正在加载时的View，图片加载结束后隐藏
	 * 
	 * @param view
	 *            加载时的View
	 */
	public void setLoadingView(View view) {
		this.view = view;
		this.view.setVisibility(View.VISIBLE);
	}

	/**
	 * 入口2 设置图片URL
	 * 
	 * @param imgUrl
	 */
	public void setImageUrlAndSaveLocal(String imgUrl,
			boolean isSaveNative, ScaleType scaleType) {
		IS_SAVE_LOCAL = isSaveNative;
		this.scaleType = scaleType;
		if (TextUtils.isEmpty(imgUrl)) {
			loadDefaultImage();
			return;
		}


		if (imgUrl.startsWith("http")) {
			// 加载网络图片
//			new DownloadTask().execute(imgUrl, imagePath);
			
		} else {
//			compressBitmap(imgUrl, imagePath);
			
			imgUrl = "file:///" + imgUrl;
		}
		// 初始化异步显示图片控件的配置
//		initImageLoader();
		ImageLoader.getInstance().cancelDisplayTask(this);
		ImageLoader.getInstance().displayImage(imgUrl, this);
		if (null != SoftReferenceImageView.this.scaleType) {
			SoftReferenceImageView.this
					.setScaleType(SoftReferenceImageView.this.scaleType);
		}

	}





	/**
	 * 从SD卡加载图片
	 * 
	 * @param imagePath
	 * @return
	 */
	public static Bitmap getImageFromLocal(String imagePath) {
		try {
			File file = new File(imagePath);
			if (file.exists()) {
				Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
				file.setLastModified(System.currentTimeMillis());
				return bitmap;
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 保存图片到SD卡
	 * 
	 * @param imagePath
	 * @param bm
	 */
	public static void saveImageToLocal(String imagePath, Bitmap bm) {

		if (bm == null || imagePath == null || "".equals(imagePath)) {
			return;
		}

		File f = new File(imagePath);
		if (f.exists()) {
			return;
		} else {
			try {
				File parentFile = f.getParentFile();
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
				f.createNewFile();
				FileOutputStream fos;
				fos = new FileOutputStream(f);
				bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();
			} catch (FileNotFoundException e) {
				f.delete();
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				f.delete();
			}
		}
	}

	/**
	 * 获得
	 * 
	 * @param context
	 *            The context to use
	 * @return The external cache dir
	 */
	@SuppressLint("NewApi")
	public static String getExternalCacheDir(Context context) {
		// android 2.2 以后才支持的特性
		if (hasExternalCacheDir()) {
			return context.getExternalCacheDir().getPath() + File.separator
					+ Cache_Dir;
		}
		// 2.2以前我们需要自己构造
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/" + Cache_Dir;
		return Environment.getExternalStorageDirectory().getPath() + cacheDir;
	}

	public static boolean hasExternalCacheDir() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	

	/**
	 * 检测SD卡是否可用
	 * 
	 * @return true为可用，否则为不可用
	 */
	public static boolean sdCardIsAvailable() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED))
			return false;
		return true;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.i("ABC onMeasure", "widthMeasureSpec    " + getMeasuredWidth() + "heightMeasureSpec  "+  getMeasuredHeight());
	}

}