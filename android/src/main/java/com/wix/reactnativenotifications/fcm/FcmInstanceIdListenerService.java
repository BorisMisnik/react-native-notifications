package com.wix.reactnativenotifications.fcm;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Instance-ID + token refreshing handling service. Contacts the GCM to fetch the updated token.
 *
 * @author amitd
 */
public class FcmInstanceIdListenerService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        // Google recommends running this from an intent service.
        Intent intent = new Intent(this, FcmInstanceIdRefreshHandlerService.class);
        startService(intent);
    }
}
