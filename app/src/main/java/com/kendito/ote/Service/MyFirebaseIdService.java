package com.kendito.ote.Service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.kendito.ote.Common;

/**
 * Created by xykit on 19-03-2018.
 */

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Common.currentToken = refreshedToken;
    }
}
