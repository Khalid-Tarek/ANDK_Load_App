package com.udacity

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService

class SeeChangesReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val bundle = intent?.getBundleExtra("")

        val notificationManager = getSystemService(
            context!!,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()
    }

}
