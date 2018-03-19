package com.wix.reactnativenotifications.fcm;

import android.content.Context;

public interface INotificationsGcmApplication {
    IFcmToken getGcmToken(Context context);
}
