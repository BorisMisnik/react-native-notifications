package com.wix.reactnativenotifications.core.notifications;

import android.content.Context;

import com.wix.reactnativenotifications.core.JsIOHelper;

import static com.wix.reactnativenotifications.Defs.NOTIFICATION_RECEIVED_EVENT_NAME;

public class RemoteNotification {

    private final NotificationProps mNotificationProps;
    private final JsIOHelper mJsIOHelper;
    private Context mContext;

    protected RemoteNotification(NotificationProps notificationProps, JsIOHelper jsIOHelper) {
        mNotificationProps = notificationProps;
        mJsIOHelper = jsIOHelper;
    }

    public RemoteNotification(Context context, NotificationProps notificationProps) {
        this(notificationProps, new JsIOHelper(context));
        this.mContext = context;
    }

    public void onReceived() {
        sendReceivedEvent();
        LocalNotification.get(mContext, mNotificationProps).post(null);
    }

    private void sendReceivedEvent() {
        mJsIOHelper.sendEventToJS(NOTIFICATION_RECEIVED_EVENT_NAME, mNotificationProps.asBundle());
    }
}
