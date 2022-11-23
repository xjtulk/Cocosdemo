package com.cocos.lib;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * Created by hejunwei on 2018/3/6.
 */

public class ApplicationWrapper extends Application {

    private static volatile ApplicationWrapper sInstance;
    protected int mProcess;
    private boolean mIsInPlugin;
    private Handler mGlobalHandler;
    private String mProcessName;

    public static ApplicationWrapper getInstance() {
        // 为保证插件使用正常，还得这么写
        if (sInstance == null) {
            synchronized (ApplicationWrapper.class) {
                if (sInstance == null) {
                    sInstance = new ApplicationWrapper();
                }
            }
        }
        return sInstance;
    }

    public boolean isInPlugin() {
        return mIsInPlugin;
    }

    @Override
    public void attachBaseContext(Context base) {
        if (getBaseContext() == null) {
            super.attachBaseContext(base);
            mIsInPlugin = !(base instanceof Application);
            HandlerThread ht = new HandlerThread("commonGlobal");
            ht.start();
            mGlobalHandler = new Handler(ht.getLooper());
        }
    }

    @Override
    public Context getApplicationContext() {
        return getBaseContext().getApplicationContext();
    }

    public void detachBaseContext() { //for plugin reflect
        if (mIsInPlugin) {
            sInstance = null;
        }
    }

    @Override
    public ComponentName startService(Intent service) {
        try {
            return super.startService(service);
        } catch (SecurityException e) {
            /**
             * http://bbs.coloros.com/thread-174655-1-1.html
             经过和OPPO工程师沟通，Service无法启动而奔溃的原因是OPPO手机自动熄屏一段时间后，会启用系统自带的电量优化管理，禁止一切自启动的app（用户设置的自启动白名单除外）。所以，类似的崩溃常常集中在用户休息之后的夜里或者凌晨，但是并不影响用户平时的正常使用。
             处理建议：在服务启动的地方进行try catch防止崩溃。
             */
            e.printStackTrace();
            return null;
        } catch (IllegalStateException e) {
            /**
             * Android 8.0以后，App在后台时，某些情况下PlayService被干掉并自动重新绑定时会抛IllegalStateException
             */
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            if (e instanceof TransactionTooLargeException) {
                HashMap<String, String> map = new HashMap<>();
                map.put("service", service.toString());
                if (service.getExtras() != null) {
                    map.put("bundle", service.getExtras().toString());
                }
                // track点都已经废弃
//                StatisticUtils.track("cm_16", map);
            }
            e.printStackTrace();
            throw new RuntimeException(e.getMessage() + "," + e + "," + service + "," + service.getExtras());
        }
    }

    // http://crash.163.com/dashboard.do#!crashDetail/?appId=A009600174&version=6.3.2_1565879805&crashTypeId=28659103&time=twoMonths
    private boolean stopServiceInner(Intent name) throws RemoteException {
        return super.stopService(name);
    }

    @Override
    public boolean stopService(Intent name) {
        try {
            return stopServiceInner(name);
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        } catch (NullPointerException | RemoteException e) {
            return false;
        }
    }

    public Handler getGlobalHandler() {
        return mGlobalHandler;
    }

    public int getProcess() {
        return mProcess;
    }

    public void setProcess(int mProcess) {
        this.mProcess = mProcess;
    }

    public String appProcessName() {
        return mProcessName;
    }

    public void setProcessName(String processName) {
        mProcessName = processName;
    }

}
