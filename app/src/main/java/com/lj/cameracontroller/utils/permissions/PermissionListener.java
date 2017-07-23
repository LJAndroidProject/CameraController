package com.lj.cameracontroller.utils.permissions;

import java.util.List;

/**
 * Created by Administrator on 2017/3/6.
 */
public interface PermissionListener {
    void onGranted();

    void onDenied(List<String> deniedPermission);
}
