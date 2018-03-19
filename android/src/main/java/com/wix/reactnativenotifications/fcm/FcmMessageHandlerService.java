package com.wix.reactnativenotifications.fcm;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wix.reactnativenotifications.core.notification.IPushNotification;
import com.wix.reactnativenotifications.core.notification.PushNotification;

import java.util.Map;

import static com.wix.reactnativenotifications.Defs.LOGTAG;

public class FcmMessageHandlerService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(LOGTAG, "New message from GCM: " + dataToString(remoteMessage.getData()));

        try {
            final IPushNotification notification = PushNotification.get(getApplicationContext(), dataToBundle(remoteMessage.getData()));
            notification.onReceived();
        } catch (IPushNotification.InvalidNotificationException e) {
            // A GCM message, yes - but not the kind we know how to work with.
            Log.v(LOGTAG, "GCM message handling aborted", e);
        }
    }

    @Override
    public void onDeletedMessages() {
    }

    @Override
    public void onMessageSent(String s) {
    }

    @Override
    public void onSendError(String s, Exception e) {
    }

    public String dataToString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder(1024);
        for (String key : map.keySet()) {
            sb.append(key).append("=").append(map.get(key)).append(", ");
        }
        return sb.toString();
    }

    public Bundle dataToBundle(Map<String, String> map) {
        Bundle bundle = new Bundle();
        for (String key : map.keySet()) {
            bundle.putString(key, map.get(key));
        }
        return bundle;
    }
}
