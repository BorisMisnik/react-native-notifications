package com.wix.reactnativenotifications.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wix.reactnativenotifications.core.notifications.NotificationProps;
import com.wix.reactnativenotifications.core.notifications.RemoteNotification;

import java.util.Map;

import static com.wix.reactnativenotifications.Defs.LOGTAG;

public class FcmMessageHandlerService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Log.d(LOGTAG, "New message from GCM: " + dataToString(message.getData()));
        final NotificationProps notificationProps = NotificationProps.fromRemoteMessage(this, message);
        new RemoteNotification(this, notificationProps).onReceived();
    }

    public String dataToString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder(1024);
        for (String key : map.keySet()) {
            sb.append(key).append("=").append(map.get(key)).append(", ");
        }
        return sb.toString();
    }
}
