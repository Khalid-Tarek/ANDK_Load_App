package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat

const val CHANNEL_ID = 5
const val REQUEST_CODE = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, bundle: Bundle?) {

    val contentIntent = Intent(applicationContext, SeeChangesReceiver::class.java)
    contentIntent.putExtra("bundle", bundle)
    val contentPendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        REQUEST_CODE,
        contentIntent,
        PendingIntent.FLAG_ONE_SHOT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.my_notification_channel_id)
    ).setSmallIcon(R.drawable.ic_assistant_black_24dp)
    .setContentTitle(applicationContext
    .getString(R.string.notification_title))
    .setContentText(messageBody)
    .addAction(
        R.drawable.ic_assistant_black_24dp,
        applicationContext.getString(R.string.notification_button),
        contentPendingIntent
    )
    .setAutoCancel(true)

    notify(CHANNEL_ID, builder.build())
}

