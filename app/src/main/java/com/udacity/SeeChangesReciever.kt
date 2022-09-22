package com.udacity

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity

class SeeChangesReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val bundle = intent?.getBundleExtra("bundle")

        val newIntent = Intent(context, DetailActivity::class.java)
        newIntent.putExtra("bundle", bundle)
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(context!!, newIntent, null)

        val notificationManager = getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()
    }

}
