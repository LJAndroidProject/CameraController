package com.lj.cameracontroller.activity;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;

import com.hikvision.netsdk.HCNetSDK;
import com.lj.cameracontroller.R;
import com.lj.cameracontroller.base.BaseActivity;
import com.lj.cameracontroller.view.TitleView;

import org.MediaPlayer.PlayM4.Player;

/**
 * Created by ljw on 2017/7/17 0017.
 */

public class IPCPlayControlActivity extends BaseActivity implements SurfaceHolder.Callback{

    private final String TAG = "IPCPlayControlActivity";
    private TitleView titleView;
    private SurfaceView IPCSurfaceView;
    private ProgressBar IPCPro;

    private int				m_iPort					= -1;				// play port
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipc_control);
        if (!initeSdk())
        {
            this.finish();
            return;
        }

        if (!initeActivity())
        {
            this.finish();
            return;
        }
        initView();
    }

    private boolean initeSdk()
    {
        //init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init())
        {
            Log.e(TAG, "HCNetSDK init is failed!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/",true);
        return true;
    }

    private boolean initeActivity()
    {
        initView();
        IPCSurfaceView.getHolder().addCallback(this);
        return true;
    }

    /**初始化界面*/
    private void initView(){
        titleView=(TitleView) findViewById(R.id.tv_top);
        titleView.iv_back.setVisibility(View.VISIBLE);
        titleView.setTv_title("摄像头");
        IPCSurfaceView = (SurfaceView) findViewById(R.id.IPCPlaySurFaceView);
        IPCPro = (ProgressBar) findViewById(R.id.IPCPlayPro);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        IPCSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        Log.i(TAG, "surface is created" + m_iPort);
        if (-1 == m_iPort)
        {
            return;
        }
        Surface surface = holder.getSurface();
        if (true == surface.isValid()) {
            if (false == Player.getInstance().setVideoWindow(m_iPort, 0, holder)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "Player setVideoWindow release!" + m_iPort);
        if (-1 == m_iPort)
        {
            return;
        }
        if (true == holder.getSurface().isValid()) {
            if (false == Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
