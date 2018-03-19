package com.wix.reactnativenotifications.fcm;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.firebase.iid.FirebaseInstanceId;

import static com.wix.reactnativenotifications.Defs.FCM_SENDER_ID_ATTR_NAME;
import static com.wix.reactnativenotifications.Defs.LOGTAG;
import static com.wix.reactnativenotifications.Defs.TOKEN_RECEIVED_EVENT_NAME;

public class FcmToken implements IFcmToken {

    final protected Context mAppContext;

    protected static String sToken;

    protected FcmToken(Context appContext) {
        if (!(appContext instanceof ReactApplication)) {
            throw new IllegalStateException("Application instance isn't a react-application");
        }
        mAppContext = appContext;
    }

    public static IFcmToken get(Context context) {
        Context appContext = context.getApplicationContext();
        if (appContext instanceof INotificationsGcmApplication) {
            return ((INotificationsGcmApplication) appContext).getGcmToken(context);
        }
        return new FcmToken(appContext);
    }

    @Override
    public void onNewTokenReady() {
        synchronized (mAppContext) {
            refreshToken();
        }
    }

    @Override
    public void onManualRefresh() {
        synchronized (mAppContext) {
            if (sToken == null) {
                Log.i(LOGTAG, "Manual token refresh => asking for new token");
                refreshToken();
            } else {
                Log.i(LOGTAG, "Manual token refresh => publishing existing token (" + sToken + ")");
                sendTokenToJS();
            }
        }
    }

    @Override
    public void onAppReady() {
        synchronized (mAppContext) {
            if (sToken == null) {
                Log.i(LOGTAG, "App initialized => asking for new token");
                refreshToken();
            } else {
                // Except for first run, this should be the case.
                Log.i(LOGTAG, "App initialized => publishing existing token (" + sToken + ")");
                sendTokenToJS();
            }
        }
    }

    protected void refreshToken() {
        try {
            sToken = getNewToken();
        } catch (Exception e) {
            Log.e(LOGTAG, "Failed to retrieve new token", e);
            return;
        }

        sendTokenToJS();
    }

    @NonNull
    protected String getNewToken() throws Exception {
        FirebaseInstanceId instanceId = FirebaseInstanceId.getInstance();
        Log.d(LOGTAG, "FCM is refreshing token... instanceId=" + instanceId.getId());

        try {
            final String registrationToken = instanceId.getToken();
            Log.i(LOGTAG, "FCM has a new token: instanceId=" + instanceId.getId() + ", token=" + registrationToken);
            return registrationToken;
        } catch (Exception e) {
            throw new Exception("FATAL: Failed to fetch a fresh new token, instanceId=" + instanceId.getId(), e);
        }
    }

    protected String getSenderId() {
        final String senderId = getSenderIdFromManifest();
        if (senderId == null) {
            throw new IllegalStateException("Sender ID not found in manifest. Did you forget to add it as the value of a '" + FCM_SENDER_ID_ATTR_NAME + "' meta-data field?");
        }
        return senderId;
    }

    protected String getSenderIdFromManifest() {
        final ApplicationInfo appInfo;
        try {
            appInfo = mAppContext.getPackageManager().getApplicationInfo(mAppContext.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(FCM_SENDER_ID_ATTR_NAME);
        } catch (PackageManager.NameNotFoundException e) {
            // Should REALLY never happen cause we're querying for our own package.
            Log.e(LOGTAG, "Failed to resolve sender ID from manifest", e);
            return null;
        }
    }

    protected void sendTokenToJS() {
        final ReactInstanceManager instanceManager = ((ReactApplication) mAppContext).getReactNativeHost().getReactInstanceManager();
        final ReactContext reactContext = instanceManager.getCurrentReactContext();

        // Note: Cannot assume react-context exists cause this is an async dispatched service.
        if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(TOKEN_RECEIVED_EVENT_NAME, sToken);
        }
    }
}
