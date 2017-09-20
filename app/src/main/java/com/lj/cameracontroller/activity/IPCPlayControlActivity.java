package com.lj.cameracontroller.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.INT_PTR;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_JPEGPARA;
import com.hikvision.netsdk.NET_DVR_PRESET_NAME;
import com.hikvision.netsdk.NET_DVR_PRESET_NAME_ARRAY;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.PTZCommand;
import com.hikvision.netsdk.RealPlayCallBack;
import com.lj.cameracontroller.R;
import com.lj.cameracontroller.adapter.PresetPupAdapter;
import com.lj.cameracontroller.base.BaseActivity;
import com.lj.cameracontroller.base.HDateGsonAdapter;
import com.lj.cameracontroller.constant.UserApi;
import com.lj.cameracontroller.entity.ControlEntity;
import com.lj.cameracontroller.entity.DeviceListEntity;
import com.lj.cameracontroller.entity.HttpRequest;
import com.lj.cameracontroller.entity.IPCLoginInfoResp;
import com.lj.cameracontroller.entity.IPCLoginResponse;
import com.lj.cameracontroller.entity.UserInfo;
import com.lj.cameracontroller.interfacecallback.IHttpUtilsCallBack;
import com.lj.cameracontroller.utils.HttpUtils;
import com.lj.cameracontroller.utils.Logger;
import com.lj.cameracontroller.utils.StorageFactory;
import com.lj.cameracontroller.utils.TimeUtils;
import com.lj.cameracontroller.view.ActionSheetDialog;
import com.lj.cameracontroller.view.MyAlertDialog;
import com.lj.cameracontroller.view.MyDialog;
import com.lj.cameracontroller.view.PlaySurfaceView;
import com.lj.cameracontroller.view.PresetListPupwindow;
import com.lj.cameracontroller.view.TitleView;

import org.MediaPlayer.PlayM4.Player;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.os.Environment.getExternalStorageState;
import static com.lj.cameracontroller.constant.UserApi.file;

/**
 * Created by ljw on 2017/7/17 0017.
 */

public class IPCPlayControlActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener, PresetPupAdapter.PresetPupAdapterListener, View.OnTouchListener {

    private final String TAG = "IPCPlayControlActivity";
    public TitleView titleView;
    private SurfaceView IPCSurfaceView;
    private ProgressBar IPCPro;
    private IPCLoginInfoResp ipcLoginInfoResponse;
    private TextView tvresolution, tvUp, tvDown, tvLeft, tvRight, tv_sound, tv_resolution;
    private ImageView tvZoomAdd, tvZoomReduce, tvFocusingAdd, tvFocusingReduce, tvIrisAdd, tvIrisReduce, tvpreset, tvSound, tvUpDown, tvphotograph;
    private LinearLayout ptzControl;
    private DeviceListEntity.DeviceEntity IPCDeviceData;
    private UserInfo userInfo = null;
    private String userId = "";
    private String accessToken = "";
    private PresetPupAdapter presetPupAdapter;
    private LinearLayout llpreset, llSound, llResolution, llUpDowm, llPhotograph;

    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;//设备参数

    private NET_DVR_PRESET_NAME_ARRAY m_presetNameArray = new NET_DVR_PRESET_NAME_ARRAY();//预置点数组列表

    private boolean isSaveRealData = false;

    private String savaRealDataPath = "";


    private int m_iLogID = -1;                // return by NET_DVR_Login_v30
    private int m_iPlayID = -1;                // return by NET_DVR_RealPlay_V30
    private int m_iPlaybackID = -1;                // return by NET_DVR_PlayBackByTime
    private int m_iPort = -1;                // play port
    private int m_iStartChan = 0;                // start channel no
    private int m_iChanNum = 0;                //channel number

    private boolean m_bTalkOn = false;
    private boolean m_bPTZL = false;
    private boolean m_bMultiPlay = false;
    private static PlaySurfaceView[] playView = new PlaySurfaceView[4];
    private boolean m_bNeedDecode = true;
    /**
     * resolutionType为清晰度设置，0 = 高清 = 主码流，1 = 流程 = 子码流，初始化时默认流程
     */
    private int resolutionType = 1;
    /**
     * soundType为声音状态 0=静音 1=开启声音
     */
    private int soundType = 0;

    private ActionSheetDialog SheetDialog;
    private MyAlertDialog myAlertDialog;
    private String titleContent = ""; //标题内容
    /**
     * 1:竖屏   2:横屏 判断屏幕以旋转的方向
     */
    private int orientation;


    //TODO 机器人部分
    private RelativeLayout rl_jiqiren,rl_jiqiren_lianxu;//机器人上下左右控制界面
    private TextView tv_jiqirenbtn_up, tv_jiqirenbtn_left, tv_jiqirenbtn_right, tv_jiqirenbtn_down,
            tv_jiqirenbtn_left_lianxu,tv_jiqirenbtn_right_lianxu,tv_jiqirenbtn_down_lianxu,tv_jiqirenbtn_up_lianxu;
    private LinearLayout ll_shujukongzhi;//数据控制输入
    private EditText et_guidao, et_gaodu, et_zhidingzhandian, et_zhidinggaodu;
    private ImageView ipc_control_tv_shenssuoadd, ipc_control_tv_shenssuoreduce; //机器人伸缩加减控制
    private ImageView ipc_jiqiren_kzms, ipc_jiqiren_gdyzw, ipc_jiqiren_sdkz, ipc_jiqiren_gaodyzw, ipc_jiqiren_jjzt, ipc_jiqiren_qhcd,
            ipc_jiqiren_dandianbujin,ipc_jiqiren_lianxubujun;//机器人控制tai
    private boolean isqiehuan=false;
    private MyDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orientation = getResources().getConfiguration().orientation;
        setContentView(R.layout.activity_ipc_control);
        myAlertDialog = new MyAlertDialog(this);
//        if(1==orientation){
//            setContentView(R.layout.activity_ipc_control);
//        }else if(2==orientation){
//            setContentView(R.layout.activity_ipc_control_land);
//        }

        if (!initeSdk()) {
//            this.finish();
//            return;
        }

        if (!initeActivity()) {
//            this.finish();
//            return;
        }
//        loginIPC();
    }


    private boolean initeSdk() {
        //init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK init is failed!");
            Toast.makeText(this, "初始化失败,错误码：" + HCNetSDK.getInstance().NET_DVR_GetLastError(), Toast.LENGTH_LONG).show();
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);
        return true;
    }

    private boolean initeActivity() {
        initView();
        dialog=new MyDialog(this,R.style.Dialog);
        IPCSurfaceView.getHolder().addCallback(this);
        userInfo = StorageFactory.getInstance().getSharedPreference(this).getDao(UserApi.USERINFOR, UserInfo.class);
        if (null != userInfo && null != userInfo.getResult()) {
            userId = userInfo.getResult().getUser_id();
            accessToken = userInfo.getResult().getAccess_token();
        } else {
            Toast.makeText(this, "用户信息异常", Toast.LENGTH_LONG).show();
        }
        Intent intent = getIntent();
        Bundle bundle;
        if (null != intent.getExtras()) {
            bundle = intent.getExtras();
            if (null != bundle.getSerializable("data")) {
                IPCDeviceData = (DeviceListEntity.DeviceEntity) bundle.getSerializable("data");
                titleContent = IPCDeviceData.getGds_name() + "-" + IPCDeviceData.getPdf_name() + "-" + IPCDeviceData.getIpc_name();
                titleView.setTv_title(IPCDeviceData.getGds_name() + "-" + IPCDeviceData.getPdf_name() + "-" + IPCDeviceData.getIpc_name());
                getIPCLoginInfo();
            } else {
                Toast.makeText(this, "摄像头数据异常", Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }

    /**
     * 初始化界面
     */
    private void initView() {
        titleView = (TitleView) findViewById(R.id.tv_top);
//        titleView.iv_back.setVisibility(View.VISIBLE);
        TextView tvTitle = titleView.getTvTitleView();
        tvTitle.setSingleLine(true);
        tvTitle.setPadding((int) getResources().getDimension(R.dimen.title_bar_backimage_width), 0, 0, 0);
        IPCSurfaceView = (SurfaceView) findViewById(R.id.IPCPlaySurFaceView);
//        IPCSurfaceView.setRotation(180);
        IPCPro = (ProgressBar) findViewById(R.id.IPCPlayPro);
        llpreset = (LinearLayout) findViewById(R.id.ll_control_tv_preset);
        tvpreset = (ImageView) findViewById(R.id.ipc_control_tv_preset);
        llSound = (LinearLayout) findViewById(R.id.ll_control_tv_sound);
        tvSound = (ImageView) findViewById(R.id.ipc_control_tv_sound);
        tv_sound = (TextView) findViewById(R.id.tv_sound);
        llResolution = (LinearLayout) findViewById(R.id.ll_control_tv_resolution);
        tvresolution = (TextView) findViewById(R.id.ipc_control_tv_resolution);
        tv_resolution = (TextView) findViewById(R.id.tv_resolution);
        llUpDowm = (LinearLayout) findViewById(R.id.ll_control_tv_updown);
        tvUpDown = (ImageView) findViewById(R.id.ipc_control_tv_updown);
        llPhotograph = (LinearLayout) findViewById(R.id.ll_control_tv_photograph);
        tvphotograph = (ImageView) findViewById(R.id.ipc_control_tv_photograph);
        tvZoomAdd = (ImageView) findViewById(R.id.ipc_control_tv_zoomadd);
        tvZoomReduce = (ImageView) findViewById(R.id.ipc_control_tv_zoomreduce);
        tvFocusingAdd = (ImageView) findViewById(R.id.ipc_control_tv_focusingadd);
        tvFocusingReduce = (ImageView) findViewById(R.id.ipc_control_tv_focusingreduce);
        tvIrisAdd = (ImageView) findViewById(R.id.ipc_control_tv_irisadd);
        tvIrisReduce = (ImageView) findViewById(R.id.ipc_control_tv_irisreduce);
        tvUp = (TextView) findViewById(R.id.tv_btn_up);
        tvDown = (TextView) findViewById(R.id.tv_btn_down);
        tvLeft = (TextView) findViewById(R.id.tv_btn_left);
        tvRight = (TextView) findViewById(R.id.tv_btn_right);
        ptzControl = (LinearLayout) findViewById(R.id.ll_ipc_ptz_control);

        //TODO 机器人部分
        rl_jiqiren = (RelativeLayout) findViewById(R.id.rl_jiqiren);
        tv_jiqirenbtn_up = (TextView) findViewById(R.id.tv_jiqirenbtn_up);
        tv_jiqirenbtn_left = (TextView) findViewById(R.id.tv_jiqirenbtn_left);
        tv_jiqirenbtn_right = (TextView) findViewById(R.id.tv_jiqirenbtn_right);
        tv_jiqirenbtn_down = (TextView) findViewById(R.id.tv_jiqirenbtn_down);
        //TODO 机器人连续部分
        rl_jiqiren_lianxu = (RelativeLayout) findViewById(R.id.rl_jiqiren_lianxu);
        tv_jiqirenbtn_up_lianxu = (TextView) findViewById(R.id.tv_jiqirenbtn_up_lianxu);
        tv_jiqirenbtn_left_lianxu = (TextView) findViewById(R.id.tv_jiqirenbtn_left_lianxu);
        tv_jiqirenbtn_right_lianxu = (TextView) findViewById(R.id.tv_jiqirenbtn_right_lianxu);
        tv_jiqirenbtn_down_lianxu = (TextView) findViewById(R.id.tv_jiqirenbtn_down_lianxu);
        ipc_jiqiren_dandianbujin = (ImageView) findViewById(R.id.ipc_jiqiren_dandianbujin);
        ipc_jiqiren_lianxubujun = (ImageView) findViewById(R.id.ipc_jiqiren_lianxubujun);

        ll_shujukongzhi = (LinearLayout) findViewById(R.id.ll_shujukongzhi);
        et_guidao = (EditText) findViewById(R.id.et_guidao);
        et_gaodu = (EditText) findViewById(R.id.et_gaodu);
        et_zhidingzhandian = (EditText) findViewById(R.id.et_zhidingzhandian);
        et_zhidinggaodu = (EditText) findViewById(R.id.et_zhidinggaodu);

        ipc_control_tv_shenssuoadd = (ImageView) findViewById(R.id.ipc_control_tv_shenssuoadd);
        ipc_control_tv_shenssuoreduce = (ImageView) findViewById(R.id.ipc_control_tv_shenssuoreduce);
        ipc_jiqiren_kzms = (ImageView) findViewById(R.id.ipc_jiqiren_kzms);
        ipc_jiqiren_gdyzw = (ImageView) findViewById(R.id.ipc_jiqiren_gdyzw);
        ipc_jiqiren_sdkz = (ImageView) findViewById(R.id.ipc_jiqiren_sdkz);
        ipc_jiqiren_gaodyzw = (ImageView) findViewById(R.id.ipc_jiqiren_gaodyzw);
        ipc_jiqiren_jjzt = (ImageView) findViewById(R.id.ipc_jiqiren_jjzt);
        ipc_jiqiren_qhcd = (ImageView) findViewById(R.id.ipc_jiqiren_qhcd);

        tv_jiqirenbtn_up.setOnClickListener(this);
        tv_jiqirenbtn_left.setOnClickListener(this);
        tv_jiqirenbtn_right.setOnClickListener(this);
        tv_jiqirenbtn_down.setOnClickListener(this);

        tv_jiqirenbtn_up_lianxu.setOnClickListener(this);
        tv_jiqirenbtn_left_lianxu.setOnClickListener(this);
        tv_jiqirenbtn_right_lianxu.setOnClickListener(this);
        tv_jiqirenbtn_down_lianxu.setOnClickListener(this);
        ipc_jiqiren_dandianbujin.setOnClickListener(this);
        ipc_jiqiren_lianxubujun.setOnClickListener(this);

        ipc_control_tv_shenssuoadd.setOnClickListener(this);
        ipc_control_tv_shenssuoreduce.setOnClickListener(this);
        ipc_jiqiren_kzms.setOnClickListener(this);
        ipc_jiqiren_gdyzw.setOnClickListener(this);
        ipc_jiqiren_sdkz.setOnClickListener(this);
        ipc_jiqiren_gaodyzw.setOnClickListener(this);
        ipc_jiqiren_jjzt.setOnClickListener(this);
        ipc_jiqiren_qhcd.setOnClickListener(this);

//        tvpreset.setOnClickListener(this);
//        tvSound.setOnClickListener(this);
//        tvresolution.setOnClickListener(this);
//        tvUpDown.setOnClickListener(this);
//        tvphotograph.setOnClickListener(this);
        llpreset.setOnClickListener(this);
        llSound.setOnClickListener(this);
        llResolution.setOnClickListener(this);
        llUpDowm.setOnClickListener(this);
        llPhotograph.setOnClickListener(this);

        tvUp.setOnTouchListener(this);
        tvDown.setOnTouchListener(this);
        tvLeft.setOnTouchListener(this);
        tvRight.setOnTouchListener(this);
        tvZoomAdd.setOnTouchListener(this);
        tvZoomReduce.setOnTouchListener(this);
        tvFocusingAdd.setOnTouchListener(this);
        tvFocusingReduce.setOnTouchListener(this);
        tvIrisAdd.setOnTouchListener(this);
        tvIrisReduce.setOnTouchListener(this);


        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) IPCSurfaceView.getLayoutParams();
        relativeParams.height = width * 9 / 16;
        IPCSurfaceView.setLayoutParams(relativeParams);
        Player.getInstance().stopSound();
//        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
//        result.put("ipc_sn","SN0123456789");
//        result.put("user_name","admin");
//        result.put("user_password","123456aB");
//        result.put("server_address","ds.somantou365.online");
//        result.put("server_port","30001");
//        Gson gson = new Gson();
//        String json = gson.toJson(result);
//        LinkedHashMap<String, Object> jsonString  = new LinkedHashMap<String, Object>();
//        jsonString.put("result", json);
//        jsonString.put("code", "1");
//        jsonString.put("message", "ok");
//        String jsonsssss = gson.toJson(jsonString);
//        System.out.println(jsonsssss);
//        IPCLoginResponse resultResponse = gson.fromJson(jsonsssss, IPCLoginResponse.class);
//        ipcLoginInfoResponse = resultResponse.result;

        titleView.setrightBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = Message.obtain();
                message.what = 0;
                myHandler.sendMessage(message);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (isSaveRealData) {
            if (isSaveRealData) {
                tvUpDown.setImageResource(R.mipmap.rec_start);
                isSaveRealData = false;
                HCNetSDK.getInstance().NET_DVR_StopSaveRealData(m_iPlayID);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(savaRealDataPath))));
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StopPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StartPreview();
    }


    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    myAlertDialog.ValidationOperationDialog().setMsg(titleContent)
                            .setNoButtonGone(true)
                            .setPositiveButton(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myAlertDialog.dismiss();
                                }
                            });
                    myAlertDialog.show();
                    break;
            }
        }
    };

    /***
     * 机器人控制接口
     *
     * @param cmd 控制指令(小写)[ pause 紧急暂停 forward 前进   forwards 连接前进 back 后退   backs 连接后退 up 升   ups 连接升 down 降   downs 连接降 extend 伸 stretch 缩]
     */
    private void controlJQR(String cmd) {
        if (null == IPCDeviceData) {
            return;
        } else if ("0".equals(IPCDeviceData.getRobot_code())||null==IPCDeviceData.getRobot_code()) {
            Toast.makeText(IPCPlayControlActivity.this, "机器人不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog.show();
        HttpUtils.get(UserApi.CONTROR + "?heartcode=5A05015EF0" + "&robotcode=" + IPCDeviceData.getRobot_code() + "&cmd=" + cmd, new IHttpUtilsCallBack() {
            @Override
            public void onFailure(HttpRequest request, IOException e) {
                dialog.dismiss();
                Logger.e(TAG, e.toString());
            }

            @Override
            public void onSuccess(String result) throws Exception {
                dialog.dismiss();
                if (!TextUtils.isEmpty(result)) {
                    Gson gson = HDateGsonAdapter.createGson();
                    Logger.e(TAG, result);
                    ControlEntity entity = gson.fromJson(result, ControlEntity.class);
                    if (entity.state == 1) {
                        Toast.makeText(IPCPlayControlActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                    } else if (entity.state == -1) {
                        Toast.makeText(IPCPlayControlActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onProgress(float progress, long total) {
                dialog.dismiss();
            }
        });
    }

    private void getIPCLoginInfo() {

        if (null == userInfo || null == userInfo.getResult()) {
            return;
        }
        dialog.show();
        HttpUtils.get(UserApi.IPC_LOGIN_INFO + "?access_token=" + accessToken
                + "&model=get_ipc_info_login"
                + "&ipc_sn=" + IPCDeviceData.getIpc_sn(), new IHttpUtilsCallBack() {
            @Override
            public void onFailure(HttpRequest request, IOException e) {
                dialog.dismiss();
                Logger.e(TAG, e.toString());
            }

            @Override
            public void onSuccess(String result) throws Exception {
                dialog.dismiss();
                if (!TextUtils.isEmpty(result)) {
                    Gson gson = HDateGsonAdapter.createGson();
                    Logger.e(TAG, result);
                    IPCLoginResponse IPCLoginResponse = gson.fromJson(result, IPCLoginResponse.class);
                    ipcLoginInfoResponse = IPCLoginResponse.result;
                    loginIPC();
                }
            }

            @Override
            public void onProgress(float progress, long total) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        IPCSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        Log.i(TAG, "surface is created" + m_iPort);
        if (-1 == m_iPort) {
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
        if (-1 == m_iPort) {
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
        if (-1 == m_iPort) {
            return;
        }
        if (true == holder.getSurface().isValid()) {
            if (false == Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }

    private void loginIPC() {
        new Thread() {
            @Override
            public void run() {
//                if(null != ipcLoginInfoResponse){
                try {
                    if (m_iLogID < 0) {
                        // login on the device
                        m_iLogID = loginDevice();
                        if (m_iLogID < 0) {
                            Log.e(TAG, "This device logins failed!");
                            return;
                        }
                        // get instance of exception callback and set
                        ExceptionCallBack oexceptionCbf = getExceptiongCbf();
                        if (oexceptionCbf == null) {
                            Log.e(TAG, "ExceptionCallBack object is failed!");
                            return;
                        }

                        if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf)) {
                            Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
                            return;
                        }
                        Log.i(TAG, "Login sucess ****************************1***************************");
                        StartPreview();
                    } else {
                        // whether we have logout
                        if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID)) {
                            Log.e(TAG, " NET_DVR_Logout is failed!");
                            return;
                        }
                        m_iLogID = -1;
                    }
                } catch (Exception err) {
                    Log.e(TAG, "error: " + err.toString());
                }
//                }else{
//                    Toast.makeText(IPCPlayControlActivity.this,"数据异常无法加载监控数据",Toast.LENGTH_LONG);
//                }
            }
        }.start();
    }

    /**
     * 开始播放
     */
    private void StartPreview() {
        IPCPro.setVisibility(View.VISIBLE);
        try {
            try {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                        hideSoftInputFromWindow(IPCPlayControlActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (NullPointerException nullex) {

            }
            if (m_iLogID < 0) {
                Log.e(TAG, "please login on device first");
                return;
            }
            if (m_bNeedDecode) {
                startSinglePreview();
            }
        } catch (Exception err) {
            Log.e(TAG, "error: " + err.toString());
        }
    }

    private void StopPreview() {
        try {
            try {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                        hideSoftInputFromWindow(IPCPlayControlActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (NullPointerException nullex) {

            }
            if (m_bNeedDecode) {
                stopSinglePreview();
            }
        } catch (Exception err) {
            Log.e(TAG, "error: " + err.toString());
        }
    }


    private int loginDevice() {
        // get instance
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30) {
            Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
            return -1;
        }
        String strIP = ipcLoginInfoResponse.getServer_address();
        int nPort = Integer.parseInt(ipcLoginInfoResponse.getServer_port());
        String strUser = ipcLoginInfoResponse.getUser_name();
        String strPsd = ipcLoginInfoResponse.getUser_password();
//        String strIP = "ds.somantou365.online";
//        int	nPort = 30003;
//        String strUser = "sfa";
//        String strPsd = "12Cd56aB";
        // call NET_DVR_Login_v30 to login on, port 8000 as default
        Log.e(TAG, strIP + "," + nPort + "," + strUser + "," + strPsd);
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(strIP, nPort, strUser, strPsd, m_oNetDvrDeviceInfoV30);
        if (iLogID < 0) {
            Log.e(TAG, "NET_DVR_Login is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
        }
        Log.i(TAG, "NET_DVR_Login is Successful!");

        return iLogID;
    }

    private void startMultiPreview() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int i = 0;
        for (i = 0; i < 4; i++) {
            if (playView[i] == null) {
                playView[i] = new PlaySurfaceView(this);
                playView[i].setParam(metric.widthPixels);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = playView[i].getCurHeight() - (i / 2) * playView[i].getCurHeight();
                params.leftMargin = (i % 2) * playView[i].getCurWidth();
                params.gravity = Gravity.BOTTOM | Gravity.LEFT;
                addContentView(playView[i], params);
            }
            playView[i].startPreview(m_iLogID, m_iStartChan + i);
        }
        m_iPlayID = playView[0].m_iPreviewHandle;
    }

    private void stopMultiPreview() {
        int i = 0;
        for (i = 0; i < 4; i++) {
            playView[i].stopPreview();
        }
        m_iPlayID = -1;
    }

    private void startSinglePreview() {
        if (m_iPlaybackID >= 0) {
            Log.i(TAG, "Please stop palyback first");
            return;
        }
        RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
        if (fRealDataCallBack == null) {
            Log.e(TAG, "fRealDataCallBack object is failed!");
            return;
        }
        Log.i(TAG, "m_iStartChan:" + m_iStartChan);

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = m_iStartChan;
        previewInfo.dwStreamType = resolutionType; //substream
        previewInfo.bBlocked = 1;
        // HCNetSDK start preview
        m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID, previewInfo, fRealDataCallBack);
        if (m_iPlayID < 0) {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }
        IPCPro.setVisibility(View.GONE);
        Log.i(TAG, "NetSdk Play sucess ***********************3***************************");
    }

    private void stopSinglePreview() {
        if (m_iPlayID < 0) {
            Log.e(TAG, "m_iPlayID < 0");
            return;
        }

        //  net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID)) {
            Log.e(TAG, "StopRealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

        m_iPlayID = -1;
        stopSinglePlayer();
    }

    private void stopSinglePlayer() {
        Player.getInstance().stopSound();
        // player stop play
        if (!Player.getInstance().stop(m_iPort)) {
            Log.e(TAG, "stop is failed!");
            return;
        }

        if (!Player.getInstance().closeStream(m_iPort)) {
            Log.e(TAG, "closeStream is failed!");
            return;
        }
        if (!Player.getInstance().freePort(m_iPort)) {
            Log.e(TAG, "freePort is failed!" + m_iPort);
            return;
        }
        m_iPort = -1;
    }

    private ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("recv exception, type:" + iType);
            }
        };
        return oExceptionCbf;
    }

    private RealPlayCallBack getRealPlayerCbf() {
        RealPlayCallBack cbf = new RealPlayCallBack() {
            public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
                // player channel 1
                IPCPlayControlActivity.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
            }
        };
        return cbf;
    }

    public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
        if (!m_bNeedDecode) {
            //   Log.i(TAG, "iPlayViewNo:" + iPlayViewNo + ",iDataType:" + iDataType + ",iDataSize:" + iDataSize);
        } else {
            if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
                if (m_iPort >= 0) {
                    return;
                }
                m_iPort = Player.getInstance().getPort();
                if (m_iPort == -1) {
                    Log.e(TAG, "getPort is failed with: " + Player.getInstance().getLastError(m_iPort));
                    return;
                }
                Log.i(TAG, "getPort succ with: " + m_iPort);
                if (iDataSize > 0) {
                    if (!Player.getInstance().setStreamOpenMode(m_iPort, iStreamMode))  //set stream mode
                    {
                        Log.e(TAG, "setStreamOpenMode failed");
                        return;
                    }
                    if (!Player.getInstance().openStream(m_iPort, pDataBuffer, iDataSize, 2 * 1024 * 1024)) //open stream
                    {
                        Log.e(TAG, "openStream failed");
                        return;
                    }
                    if (!Player.getInstance().play(m_iPort, IPCSurfaceView.getHolder())) {
                        Log.e(TAG, "play failed");
                        return;
                    }
                    if (!Player.getInstance().playSound(m_iPort)) {
                        Log.e(TAG, "playSound failed with error code:" + Player.getInstance().getLastError(m_iPort));
                        return;
                    }
                    if (0 == soundType) {
                        Player.getInstance().stopSound();
                    } else if (1 == soundType) {
                        Player.getInstance().playSound(m_iPort);
                    }
                }
            } else {
                if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
//		    		Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
                    for (int i = 0; i < 4000 && m_iPlaybackID >= 0; i++) {
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

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        NET_DVR_PRESET_NAME[] struPresetName = null;
        if (viewId == R.id.ll_control_tv_preset) {//预置点按钮
            byte[] arrayOutBuf = new byte[8 * 1024];
            INT_PTR intPtr = new INT_PTR();
            String strInput = new String("<PTZAbility >" +
                    "  <channelNO>" +
                    " " + m_iStartChan +
                    "  </channelNO>" +
                    "</PTZAbility>");
            byte[] arrayInBuf = new byte[8 * 1024];
            arrayInBuf = strInput.getBytes();
            HCNetSDK.getInstance().NET_DVR_GetXMLAbility(m_iLogID, HCNetSDK.DEVICE_ABILITY_INFO, arrayInBuf, strInput.length(), arrayOutBuf, 8 * 1024, intPtr);
            InputStream sbs = new ByteArrayInputStream(arrayOutBuf);
            // 实例化一个文档构建器工厂
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            // 通过文档构建器工厂获取一个文档构建器
            DocumentBuilder builder = null;
            Document document = null;
            try {
                builder = factory.newDocumentBuilder();
                // 通过文档通过文档构建器构建一个文档实例
                document = builder.parse(sbs);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 获取XML文件根节点
            Element root = document.getDocumentElement();
            // 获得所有子节点
            NodeList childNodes = root.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node childNode = (Node) childNodes.item(j);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element) childNode;
                    if ("Preset".equals(childElement.getNodeName())) {
                        NodeList PresetNodes = childElement.getChildNodes();
                        for (int i = 0; i < PresetNodes.getLength(); i++) {
                            Node PresetNode = PresetNodes.item(i);
                            if (PresetNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element PresetElement = (Element) PresetNode;
                                if ("presetNum".equals(PresetElement.getNodeName())) {
                                    struPresetName = new NET_DVR_PRESET_NAME[Integer.valueOf(PresetElement.getAttribute("max"))];
                                }
                            }
                        }
                    }
                }
            }
            if (null != struPresetName && struPresetName.length > 0) {
//                PresetListPupwindow presetListPupwindow = new PresetListPupwindow(this,struPresetName,tvpreset.getWidth(),ptzControl.getHeight());
//                presetListPupwindow.showAsDropDown(tvpreset,0,0);
//                presetPupAdapter = presetListPupwindow.getPresetPupAdapter();
//                presetPupAdapter.setPresetPupAdapterListener(this);
                SheetDialog = new ActionSheetDialog(IPCPlayControlActivity.this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem(getResources().getString(R.string.str_yushedian) + 1, ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                HCNetSDK.getInstance().NET_DVR_PTZPreset(m_iPlayID, 39, 1);
                            }
                        })
                        .addSheetItem(getResources().getString(R.string.str_yushedian) + 2, ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                HCNetSDK.getInstance().NET_DVR_PTZPreset(m_iPlayID, 39, 2);
                            }
                        })
                        .addSheetItem(getResources().getString(R.string.str_yushedian) + 3, ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                HCNetSDK.getInstance().NET_DVR_PTZPreset(m_iPlayID, 39, 3);
                            }
                        })
                        .addSheetItem(getResources().getString(R.string.str_yushedian) + 4, ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                HCNetSDK.getInstance().NET_DVR_PTZPreset(m_iPlayID, 39, 4);
                            }
                        })
                        .addSheetItem(getResources().getString(R.string.str_yushedian) + 5, ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                HCNetSDK.getInstance().NET_DVR_PTZPreset(m_iPlayID, 39, 5);
                            }
                        });
                SheetDialog.show();
            } else {
                Toast.makeText(this, "预置点获取失败", Toast.LENGTH_LONG).show();
            }

        } else if (viewId == R.id.ll_control_tv_sound) {//声音按钮
            SheetDialog = new ActionSheetDialog(IPCPlayControlActivity.this)
                    .builder()
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(true)
                    .addSheetItem(getResources().getString(R.string.str_sound), ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
                            Player.getInstance().playSound(m_iPort);
                            tvSound.setImageResource(R.mipmap.sound_icon);
                            tv_sound.setText(R.string.str_sound);
                            soundType = 1;
                        }
                    })
                    .addSheetItem(getResources().getString(R.string.str_mute), ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
                            tvSound.setImageResource(R.mipmap.sound_up);
                            tv_sound.setText(R.string.str_mute);
                            Player.getInstance().stopSound();
                            soundType = 0;

                        }
                    });
            SheetDialog.show();
//            if (0 == soundType){
//                tvSound.setImageResource(R.mipmap.sound_icon);
//                tv_sound.setText(R.string.str_sound);
//                Player.getInstance().playSound(m_iPort);
//                soundType = 1;
//            }else if(1 == soundType){
//                tvSound.setImageResource(R.mipmap.sound_up);
//                tv_sound.setText(R.string.str_mute);
//                Player.getInstance().stopSound();
//                soundType = 0;
//            }
        } else if (viewId == R.id.ll_control_tv_resolution) {//分辨率按钮
            SheetDialog = new ActionSheetDialog(IPCPlayControlActivity.this)
                    .builder()
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(true)
                    .addSheetItem(getResources().getString(R.string.str_fluent), ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    tvresolution.setText(R.string.str_fluent);
                                    tv_resolution.setText(R.string.str_fluent);
                                    resolutionType = 1;
                                    StopPreview();
                                    StartPreview();
                                }
                            })
                    .addSheetItem(getResources().getString(R.string.str_hightdifinition), ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    tvresolution.setText(R.string.str_hightdifinition);
                                    tv_resolution.setText(R.string.str_hightdifinition);
                                    resolutionType = 0;
                                    StopPreview();
                                    StartPreview();
                                }
                            });
            SheetDialog.show();
//            if(0 == resolutionType){//点击为高清播放时
//
//            }else if(1 == resolutionType){//点击为流程播放时
//
//            }
        } else if (viewId == R.id.ll_control_tv_updown) {//上下 视频翻转按钮
            String path = new String(getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/dlkj");
            File dirFirstFolder = new File(path);
            if (!dirFirstFolder.exists()) {
                dirFirstFolder.mkdirs();
            }
            if (isSaveRealData) {
                tvUpDown.setImageResource(R.mipmap.rec_start);
                isSaveRealData = false;
                HCNetSDK.getInstance().NET_DVR_StopSaveRealData(m_iPlayID);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(savaRealDataPath))));
            } else {
                tvUpDown.setImageResource(R.mipmap.rec_stop);
                isSaveRealData = true;
                String fileName = path + "/" + TimeUtils.getDateYMDHMS() + ".mp4";
                Toast.makeText(this, "开始录像", Toast.LENGTH_SHORT).show();
                HCNetSDK.getInstance().NET_DVR_SaveRealData(m_iPlayID, fileName);
            }
        } else if (viewId == R.id.ll_control_tv_photograph) {//拍照按钮
            NET_DVR_JPEGPARA strJpeg = new NET_DVR_JPEGPARA();
            strJpeg.wPicQuality = 0xff;
            strJpeg.wPicSize = 2;
//            String path = new String(getExternalStorageDirectory().getAbsolutePath()+"/somantou");
            String path = new String(getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/dlkj");
            File dirFirstFolder = new File(path);
            if (!dirFirstFolder.exists()) {
                dirFirstFolder.mkdirs();
            }
            String imageName = TimeUtils.getDateYMDHMS();
            String imagePath = path + "/" + imageName + ".jpg";
            boolean isSuccess = HCNetSDK.getInstance().NET_DVR_CaptureJPEGPicture(m_iLogID, m_iStartChan, strJpeg, imagePath);
            if (isSuccess) {
                Toast.makeText(this, "拍照保存成功", Toast.LENGTH_LONG).show();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(imagePath))));
            } else {
                Toast.makeText(this, "拍照失败，请重试", Toast.LENGTH_LONG).show();
            }
            Logger.e(TAG, "" + isSuccess + "...." + HCNetSDK.getInstance().NET_DVR_GetLastError() + "...." + path);
        } else if (viewId == R.id.tv_jiqirenbtn_up) { //机器人上按钮(单步)
            controlJQR("up");
        } else if (viewId == R.id.tv_jiqirenbtn_left) { //机器人左按钮(单步)
            controlJQR("back");
        } else if (viewId == R.id.tv_jiqirenbtn_right) {//机器人右按钮(单步)
            controlJQR("forward");
        } else if (viewId == R.id.tv_jiqirenbtn_down) {//机器人下按钮(单步)
            controlJQR("down");
        }  else if (viewId == R.id.tv_jiqirenbtn_up_lianxu) { //机器人上按钮（连续）
            controlJQR("ups");
        } else if (viewId == R.id.tv_jiqirenbtn_left_lianxu) { //机器人左按钮（连续）
            controlJQR("backs");
        } else if (viewId == R.id.tv_jiqirenbtn_right_lianxu) {//机器人右按钮（连续）
            controlJQR("forwards");
        } else if (viewId == R.id.tv_jiqirenbtn_down_lianxu) {//机器人下按钮（连续）
            controlJQR("downs");
        }else if (viewId == R.id.ipc_control_tv_shenssuoadd) { //机器人伸缩按钮中伸按钮
            controlJQR("extend");
        } else if (viewId == R.id.ipc_control_tv_shenssuoreduce) {//机器人伸缩按钮中缩按钮
            controlJQR("stretch");
        } else if (viewId == R.id.ipc_jiqiren_kzms) {//机器人控制模式按钮
//            controlJQR("extend");
        } else if (viewId == R.id.ipc_jiqiren_gdyzw) {//机器人轨道预置顶按钮
//            controlJQR("extend");
        } else if (viewId == R.id.ipc_jiqiren_sdkz) {//机器人速度控制按钮
//            controlJQR("extend");
        } else if (viewId == R.id.ipc_jiqiren_gaodyzw) {//机器人高度预置顶按钮
//            controlJQR("extend");
        } else if (viewId == R.id.ipc_jiqiren_jjzt) {//机器人紧急暂停按钮
            controlJQR("pause");
        } else if (viewId == R.id.ipc_jiqiren_qhcd) {//机器人切换界面按钮(暂时弃用)
//            if(isqiehuan==false){ //表示还没点
//                rl_jiqiren.setVisibility(View.GONE);
////                ll_shujukongzhi.setVisibility(View.VISIBLE);
//                isqiehuan=true;
//            }else{
//                rl_jiqiren.setVisibility(View.VISIBLE);
////                ll_shujukongzhi.setVisibility(View.GONE);
//                isqiehuan=false;
//            }

        }else if(viewId == R.id.ipc_jiqiren_dandianbujin){  //点击单点步进，左边控制台布局则要显示成成单点布局
            ipc_jiqiren_dandianbujin.setVisibility(View.GONE);
            ipc_jiqiren_lianxubujun.setVisibility(View.VISIBLE);
            rl_jiqiren.setVisibility(View.VISIBLE);
            rl_jiqiren_lianxu.setVisibility(View.GONE);
        }else if(viewId == R.id.ipc_jiqiren_lianxubujun){  //点击连续步进，左边控制台布局则要显示成成连续布局
            ipc_jiqiren_lianxubujun.setVisibility(View.GONE);
            ipc_jiqiren_dandianbujin.setVisibility(View.VISIBLE);
            rl_jiqiren.setVisibility(View.GONE);
            rl_jiqiren_lianxu.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int viewId = view.getId();
        try {
            if (viewId == R.id.ipc_control_tv_zoomadd) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {//焦距变大
                    tvZoomAdd.setImageResource(R.mipmap.btn1a);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControl(m_iPlayID, PTZCommand.ZOOM_IN, 0);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    tvZoomAdd.setImageResource(R.mipmap.btn1);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControl(m_iPlayID, PTZCommand.ZOOM_IN, 1);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                }
            } else if (viewId == R.id.ipc_control_tv_zoomreduce) {//焦距变小
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    tvZoomReduce.setImageResource(R.mipmap.btn2a);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControl(m_iPlayID, PTZCommand.ZOOM_OUT, 0);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    tvZoomReduce.setImageResource(R.mipmap.btn2);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControl(m_iPlayID, PTZCommand.ZOOM_OUT, 1);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                }
            } else if (viewId == R.id.ipc_control_tv_focusingadd) {//焦点前调
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    tvFocusingAdd.setImageResource(R.mipmap.btn1a);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControl(m_iPlayID, PTZCommand.FOCUS_NEAR, 0);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    tvFocusingAdd.setImageResource(R.mipmap.btn1);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControl(m_iPlayID, PTZCommand.FOCUS_NEAR, 1);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                }
            } else if (viewId == R.id.ipc_control_tv_focusingreduce) {//焦点后条
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    tvFocusingReduce.setImageResource(R.mipmap.btn2a);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControl(m_iPlayID, PTZCommand.FOCUS_FAR, 0);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    tvFocusingReduce.setImageResource(R.mipmap.btn2);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControl(m_iPlayID, PTZCommand.FOCUS_FAR, 1);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                }
            } else if (viewId == R.id.ipc_control_tv_irisadd) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {//光圈扩大
                    tvIrisAdd.setImageResource(R.mipmap.btn1a);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControl(m_iPlayID, PTZCommand.IRIS_OPEN, 0);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    tvIrisAdd.setImageResource(R.mipmap.btn1);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControl(m_iPlayID, PTZCommand.IRIS_OPEN, 1);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                }
            } else if (viewId == R.id.ipc_control_tv_irisreduce) {//光圈缩小
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    tvIrisReduce.setImageResource(R.mipmap.btn2a);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControl(m_iPlayID, PTZCommand.IRIS_CLOSE, 0);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    tvIrisReduce.setImageResource(R.mipmap.btn2);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControl(m_iPlayID, PTZCommand.IRIS_CLOSE, 1);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                }
            } else if (viewId == R.id.tv_btn_up) {//摄像头上
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    tvUp.setBackgroundResource(R.mipmap.btn3a);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(m_iPlayID, PTZCommand.TILT_UP, 0, 4);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    tvUp.setBackgroundResource(R.mipmap.btn3);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(m_iPlayID, PTZCommand.TILT_UP, 1, 4);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                }
            } else if (viewId == R.id.tv_btn_down) {//摄像头下
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    tvDown.setBackgroundResource(R.mipmap.btn6a);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(m_iPlayID, PTZCommand.TILT_DOWN, 0, 4);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    tvDown.setBackgroundResource(R.mipmap.btn6);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(m_iPlayID, PTZCommand.TILT_DOWN, 1, 4);//，0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                }
            } else if (viewId == R.id.tv_btn_left) {//摄像头左
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    tvLeft.setBackgroundResource(R.mipmap.btn4a);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(m_iPlayID, PTZCommand.PAN_LEFT, 0, 4);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    tvLeft.setBackgroundResource(R.mipmap.btn4);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(m_iPlayID, PTZCommand.PAN_LEFT, 1, 4);//，0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                }
            } else if (viewId == R.id.tv_btn_right) {//摄像头右
                tvRight.setBackgroundResource(R.mipmap.btn5a);
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(m_iPlayID, PTZCommand.PAN_RIGHT, 0, 4);//，0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    tvRight.setBackgroundResource(R.mipmap.btn5);
                    boolean isSuccess = HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(m_iPlayID, PTZCommand.PAN_RIGHT, 1, 4);//0- 开始，1- 停止
                    Logger.e(TAG, isSuccess + "....." + HCNetSDK.getInstance().NET_DVR_GetLastError());
                }
            }
            return true;
        } catch (Exception e) {


            return false;
        }
    }

    @Override
    public void clickItem(int dwPresetIndex) {
        Toast.makeText(this, "预置点", Toast.LENGTH_LONG).show();
        HCNetSDK.getInstance().NET_DVR_PTZPreset(m_iPlayID, 39, dwPresetIndex);
    }

}
