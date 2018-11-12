package com.waterfairy.bluetoothchart.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by water_fairy on 2016/7/29.
 */
public class PermissionUtils {

    private static HashMap<Integer, Boolean> requestHashMap;
    private static HashMap<Integer, String> requestPermissionHashMap;

    /**
     * 位置权限,定位/蓝牙
     */
    public final static int REQUEST_LOCATION = 1;

    /**
     * 文件读写
     */
    public final static int REQUEST_STORAGE = 2;

    /**
     * 相机
     */
    public final static int REQUEST_CAMERA = 3;

    /**
     * 录音
     */
    public final static int REQUEST_RECORD = 4;

    /**
     * 手机状态
     */
    public final static int REQUEST_PHONE_STATE = 5;
    /**
     * apk安装权限
     */
    public final static int REQUEST_INSTALL_APK = 6;


    private static OnRequestPermissionListener onRequestPermissionListener;//权限请求监听

    /**
     * 申请权限
     *
     * @param activity    Activity
     * @param requestCode 请求类型
     */
    public static boolean requestPermission(Activity activity, int requestCode) {
        boolean hasPermission = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) hasPermission = true;
        else {
            String[] permissions = null;
            String permission = null;
            switch (requestCode) {
                case REQUEST_LOCATION:
                    permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
                    permission = Manifest.permission.ACCESS_COARSE_LOCATION;
                    break;
                case REQUEST_CAMERA:
                    permissions = new String[]{Manifest.permission.CAMERA};
                    permission = Manifest.permission.CAMERA;
                    break;
                case REQUEST_STORAGE:
                    permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                    break;
                case REQUEST_RECORD:
                    permissions = new String[]{Manifest.permission.RECORD_AUDIO};
                    permission = Manifest.permission.RECORD_AUDIO;
                    break;
                case REQUEST_PHONE_STATE:
                    permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
                    permission = Manifest.permission.READ_PHONE_STATE;
                    break;
                case REQUEST_INSTALL_APK:
                    permissions = new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES};
                    permission = Manifest.permission.REQUEST_INSTALL_PACKAGES;
                    break;
            }

            if (permissions != null) {
                hasPermission = requestPermission(activity, permissions, permission, requestCode);
            }
            if (requestHashMap == null) requestHashMap = new HashMap<>();
            requestHashMap.put(requestCode, hasPermission);
            if (requestPermissionHashMap == null) requestPermissionHashMap = new HashMap<>();
            requestPermissionHashMap.put(requestCode, permission);
        }
        return hasPermission;
    }

    /**
     * @param activity    activity
     * @param permissions 权限组
     * @param permission  权限
     * @param requestCode requestCode  Activity中 会返回权限申请状态(类似startActivityForResult)
     */

    public static boolean requestPermission(Activity activity,
                                            @NonNull String[] permissions,
                                            @NonNull String permission,
                                            int requestCode) {
        int permissionCode = checkPermission(activity, permission);
        boolean hasPermission = false;
        if (!(hasPermission = (permissionCode == PackageManager.PERMISSION_GRANTED))) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
        return hasPermission;
    }

    /**
     * 检查权限
     *
     * @param context    activity
     * @param permission 某个权限
     * @return {
     */
    public static int checkPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission);
    }

    public static boolean onRequestPermissionsResultForCamera(String resultPermissions[], int grantResults[]) {
        return onRequestPermissionsResult(Manifest.permission.CAMERA, resultPermissions, grantResults);
    }

    public static boolean onRequestPermissionsResultForLocation(String resultPermissions[], int grantResults[]) {
        return onRequestPermissionsResult(Manifest.permission.ACCESS_COARSE_LOCATION, resultPermissions, grantResults);
    }

    public static boolean onRequestPermissionsResultForRecord(String resultPermissions[], int grantResults[]) {
        return onRequestPermissionsResult(Manifest.permission.RECORD_AUDIO, resultPermissions, grantResults);
    }

    public static boolean onRequestPermissionsResultForSDCard(String resultPermissions[], int grantResults[]) {
        return onRequestPermissionsResult(Manifest.permission.WRITE_EXTERNAL_STORAGE, resultPermissions, grantResults);
    }

    /**
     * @param requestPermission
     * @param resultPermissions
     * @param grantResults
     * @return
     */
    public static boolean onRequestPermissionsResult(String requestPermission, String resultPermissions[], int grantResults[]) {
        if (resultPermissions.length > 0) {
            for (int i = 0; i < resultPermissions.length; i++) {
                if (TextUtils.equals(resultPermissions[i], requestPermission)) {
                    if (grantResults.length > i) {
                        return grantResults[i] == PackageManager.PERMISSION_GRANTED;
                    }
                }
            }
        }
        return false;
    }

    /**
     * version-2 改版使用
     * <p>
     * 1.setRequestPermissionListener   设置监听
     * 2.requestPermissions             请求权限
     * 3.onRequestPermissionsResult     权限结果返回
     * <p>
     * --4.checkAllPermission           提供手动检查权限
     */
    public static void setRequestPermissionListener(OnRequestPermissionListener listener) {
        onRequestPermissionListener = listener;
    }

    /**
     * @param activity
     * @param requestCodes
     * @return
     * @throws Exception
     */
    public static boolean requestPermissions(Activity activity, int[] requestCodes) throws Exception {
        if (requestCodes == null) throw new Exception("没有请求权限");
        if (requestHashMap == null) requestHashMap = new HashMap<>();
        for (int requestCode : requestCodes) {
            boolean hasPermission = requestPermission(activity, requestCode);
            if (onRequestPermissionListener != null && hasPermission) {
                onRequestPermissionListener.onRequestPermission(requestCode, true);
            }
            requestHashMap.put(requestCode, hasPermission);
        }
        return checkAllPermission();
    }

    /**
     * @param requestCode
     * @param resultPermissions
     * @param grantResults
     * @return
     */
    public static void onRequestPermissionsResult(int requestCode, String resultPermissions[], int grantResults[]) throws Exception {
        if (resultPermissions.length > 0) {
            for (int i = 0; i < resultPermissions.length; i++) {
                if (TextUtils.equals(resultPermissions[i], requestPermissionHashMap.get(requestCode))) {
                    if (grantResults.length > i) {
                        boolean state = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                        if (onRequestPermissionListener != null)
                            onRequestPermissionListener.onRequestPermission(requestCode, state);
                    }
                }
            }
        }
    }


    /**
     * 检查所有的权限
     *
     * @return
     * @throws Exception
     */
    public static boolean checkAllPermission() throws Exception {
        if (requestHashMap == null) throw new Exception("没有请求权限");
        Set<Integer> integers = requestHashMap.keySet();
        for (Integer next : integers) {
            Boolean aBoolean = requestHashMap.get(next);
            if (!aBoolean) {
                return false;
            }
        }
        return true;
    }


    public interface OnRequestPermissionListener {
        /**
         * 所有的权限请求结果
         *
         * @param state
         */
        void onRequestPermission(int requestCode, boolean state);
    }
}
