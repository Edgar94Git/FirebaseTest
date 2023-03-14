package com.ereyes.firebasetest

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/****
 * Project: FirebaseTest
 * From: com.ereyes.firebasetest
 * Created by Edgar Reyes Gonzalez on 3/14/2023 at 8:36 AM
 * All rights reserved 2023.
 ****/
class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Looper.prepare()
        Handler().post{
            Toast.makeText(baseContext, message.notification?.title, Toast.LENGTH_SHORT ).show()
        }
        Looper.loop()
    }
}