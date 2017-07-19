package com.lj.cameracontroller.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;
import com.lj.cameracontroller.R;
import com.lj.cameracontroller.base.BaseActivity;
import com.lj.cameracontroller.entity.IPCLoginInfoResp;
import com.lj.cameracontroller.entity.ResultResponse;
import com.lj.cameracontroller.view.PlaySurfaceView;
import com.lj.cameracontroller.view.TitleView;

import org.MediaPlayer.PlayM4.Player;

import java.util.LinkedHashMap;

/**
 * Created by ljw on 2017/7/17 0017.
 */

public class IPCPlayControlActivity extends BaseActivity implements SurfaceHolder.Callback{

    private final String TAG = "IPCPlayControlActivity";
    private TitleView titleView;
    private SurfaceView IPCSurfaceView;
    private ProgressBar IPCPro;
    private IPCLoginInfoResp ipcLoginInfoResponse;

    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;

    private int				m_iLogID				= -1;				// return by NET_DVR_Login_v30
    private int 			m_iPlayID				= -1;				// return by NET_DVR_RealPlay_V30
    private int				m_iPlaybackID			= -1;				// return by NET_DVR_PlayBackByTime
    private int				m_iPort					= -1;				// play port
    private	int 			m_iStartChan 			= 0;				// start channel no
    private int				m_iChanNum				= 0;				//channel number

    private boolean			m_bTalkOn				= false;
    private boolean			m_bPTZL					= false;
    private boolean			m_bMultiPlay			= false;
    private static PlaySurfaceView [] playView = new PlaySurfaceView[4];
    private boolean			m_bNeedDecode			= true;
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
        loginIPC();
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
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        result.put("ipc_sn","SN0123456789");
        result.put("user_name","admin");
        result.put("user_password","123456aB");
        result.put("server_address","ds.somantou365.online");
        result.put("server_port","20001");
        Gson gson = new Gson();
        String json = gson.toJson(result);
        LinkedHashMap<String, String> jsonString  = new LinkedHashMap<String, String>();
        jsonString.put("result", json);
        jsonString.put("code", "1");
        jsonString.put("message", "ok");
        String jsonsssss = gson.toJson(jsonString);
        System.out.println(jsonsssss);
        ResultResponse resultResponse = gson.fromJson(jsonsssss, ResultResponse.class);
        ipcLoginInfoResponse = gson.fromJson(resultResponse.result, IPCLoginInfoResp.class);
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

    private void loginIPC(){
        new Thread(){
            @Override
            public void run() {
                if(null != ipcLoginInfoResponse){
                    try
                    {
                        if(m_iLogID < 0)
                        {
                            // login on the device
                            m_iLogID = loginDevice();
                            if (m_iLogID < 0)
                            {
                                Log.e(TAG, "This device logins failed!");
                                return;
                            }
                            // get instance of exception callback and set
                            ExceptionCallBack oexceptionCbf = getExceptiongCbf();
                            if (oexceptionCbf == null)
                            {
                                Log.e(TAG, "ExceptionCallBack object is failed!");
                                return ;
                            }

                            if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf))
                            {
                                Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
                                return;
                            }
                            Log.i(TAG, "Login sucess ****************************1***************************");
                            preview();
                        }
                        else
                        {
                            // whether we have logout
                            if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID))
                            {
                                Log.e(TAG, " NET_DVR_Logout is failed!");
                                return;
                            }
                            m_iLogID = -1;
                        }
                    }
                catch (Exception err)
                    {
                        Log.e(TAG, "error: " + err.toString());
                    }
                }else{
                    Toast.makeText(IPCPlayControlActivity.this,"数据异常无法加载监控数据",Toast.LENGTH_LONG);
                }
            }
        }.start();
    }

    private void preview(){
        try
        {
            try{
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
                        hideSoftInputFromWindow(IPCPlayControlActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }catch (NullPointerException nullex){

        }
            if(m_iLogID < 0)
            {
                Log.e(TAG,"please login on device first");
                return ;
            }
            if(m_bNeedDecode)
            {
                if(m_iChanNum > 1)//preview more than a channel
                {
                    if(!m_bMultiPlay)
                    {
                        startMultiPreview();
                        m_bMultiPlay = true;
                    }
                    else
                    {
                        stopMultiPreview();
                        m_bMultiPlay = false;
                    }
                }
                else	//preivew a channel
                {
                    if(m_iPlayID < 0)
                    {
                        startSinglePreview();
                    }
                    else
                    {
                        stopSinglePreview();
                    }
                }
            }
            else
            {

            }
        }
        catch (Exception err)
        {
            Log.e(TAG, "error: " + err.toString());
        }
    }


    private int loginDevice()
    {
        // get instance
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30)
        {
            Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
            return -1;
        }
        String strIP = ipcLoginInfoResponse.getServer_address();
        int	nPort = Integer.parseInt(ipcLoginInfoResponse.getServer_port());
        String strUser = ipcLoginInfoResponse.getUser_name();
        String strPsd = ipcLoginInfoResponse.getUser_password();
        // call NET_DVR_Login_v30 to login on, port 8000 as default
        Log.e(TAG,strIP+","+nPort+","+strUser+","+strPsd);
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(strIP, nPort, strUser, strPsd, m_oNetDvrDeviceInfoV30);
        if (iLogID < 0)
        {
            Log.e(TAG, "NET_DVR_Login is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        if(m_oNetDvrDeviceInfoV30.byChanNum > 0)
        {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
        }
        else if(m_oNetDvrDeviceInfoV30.byIPChanNum > 0)
        {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
        }
        Log.i(TAG, "NET_DVR_Login is Successful!");

        return iLogID;
    }

    private void startMultiPreview()
    {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int i = 0;
        for(i = 0; i < 4; i++)
        {
            if(playView[i] == null)
            {
                playView[i] = new PlaySurfaceView(this);
                playView[i].setParam(metric.widthPixels);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = playView[i].getCurHeight() - (i/2) * playView[i].getCurHeight();
                params.leftMargin = (i%2) * playView[i].getCurWidth();
                params.gravity = Gravity.BOTTOM | Gravity.LEFT;
                addContentView(playView[i], params);
            }
            playView[i].startPreview(m_iLogID, m_iStartChan + i);
        }
        m_iPlayID = playView[0].m_iPreviewHandle;
    }
    private void stopMultiPreview()
    {
        int i = 0;
        for(i = 0; i < 4;i++)
        {
            playView[i].stopPreview();
        }
        m_iPlayID = -1;
    }

    private void startSinglePreview()
    {
        if(m_iPlaybackID >= 0)
        {
            Log.i(TAG, "Please stop palyback first");
            return ;
        }
        RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
        if (fRealDataCallBack == null)
        {
            Log.e(TAG, "fRealDataCallBack object is failed!");
            return ;
        }
        Log.i(TAG, "m_iStartChan:" +m_iStartChan);

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = m_iStartChan;
        previewInfo.dwStreamType = 1; //substream
        previewInfo.bBlocked = 1;
        // HCNetSDK start preview
        m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID, previewInfo, fRealDataCallBack);
        if (m_iPlayID < 0)
        {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return ;
        }

        Log.i(TAG, "NetSdk Play sucess ***********************3***************************");
    }

    private void stopSinglePreview()
    {
        if ( m_iPlayID < 0)
        {
            Log.e(TAG, "m_iPlayID < 0");
            return;
        }

        //  net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID))
        {
            Log.e(TAG, "StopRealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

        m_iPlayID = -1;
        stopSinglePlayer();
    }
    private void stopSinglePlayer()
    {
        Player.getInstance().stopSound();
        // player stop play
        if (!Player.getInstance().stop(m_iPort))
        {
            Log.e(TAG, "stop is failed!");
            return;
        }

        if(!Player.getInstance().closeStream(m_iPort))
        {
            Log.e(TAG, "closeStream is failed!");
            return;
        }
        if(!Player.getInstance().freePort(m_iPort))
        {
            Log.e(TAG, "freePort is failed!" + m_iPort);
            return;
        }
        m_iPort = -1;
    }
    private ExceptionCallBack getExceptiongCbf()
    {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack()
        {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle)
            {
                System.out.println("recv exception, type:" + iType);
            }
        };
        return oExceptionCbf;
    }

    private RealPlayCallBack getRealPlayerCbf()
    {
        RealPlayCallBack cbf = new RealPlayCallBack()
        {
            public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize)
            {
                // player channel 1
                IPCPlayControlActivity.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
            }
        };
        return cbf;
    }

    public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode)
    {
        if(!m_bNeedDecode)
        {
            //   Log.i(TAG, "iPlayViewNo:" + iPlayViewNo + ",iDataType:" + iDataType + ",iDataSize:" + iDataSize);
        }
        else
        {
            if(HCNetSDK.NET_DVR_SYSHEAD == iDataType)
            {
                if(m_iPort >= 0)
                {
                    return;
                }
                m_iPort = Player.getInstance().getPort();
                if(m_iPort == -1)
                {
                    Log.e(TAG, "getPort is failed with: " + Player.getInstance().getLastError(m_iPort));
                    return;
                }
                Log.i(TAG, "getPort succ with: " + m_iPort);
                if (iDataSize > 0)
                {
                    if (!Player.getInstance().setStreamOpenMode(m_iPort, iStreamMode))  //set stream mode
                    {
                        Log.e(TAG, "setStreamOpenMode failed");
                        return;
                    }
                    if (!Player.getInstance().openStream(m_iPort, pDataBuffer, iDataSize, 2*1024*1024)) //open stream
                    {
                        Log.e(TAG, "openStream failed");
                        return;
                    }
                    if (!Player.getInstance().play(m_iPort,IPCSurfaceView .getHolder()))
                    {
                        Log.e(TAG, "play failed");
                        return;
                    }
                    if(!Player.getInstance().playSound(m_iPort))
                    {
                        Log.e(TAG, "playSound failed with error code:" + Player.getInstance().getLastError(m_iPort));
                        return;
                    }
                }
            }
            else
            {
                if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize))
                {
//		    		Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
                    for(int i = 0; i < 4000 && m_iPlaybackID >=0 ; i++)
                    {
                        if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize))
                            Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
                        else
                            break;
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        }
                    }
                }

            }
        }

    }
}
