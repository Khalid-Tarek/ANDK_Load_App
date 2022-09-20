package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.database.DatabaseUtils
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.core.os.bundleOf
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.Exception
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {


    private var downloadID: Long = 0

    private var URL = ""
    private var downloadTitle: String = ""

    private lateinit var downloadManager: DownloadManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(
            getString(R.string.my_notification_channel_id),
            getString(R.string.my_notification_channel_name)
        )

        custom_button.setOnClickListener {
            if(URL == "")
                Toast.makeText(this, "Please choose an option", Toast.LENGTH_SHORT).show()
            else
                download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
           val c = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))

            c.moveToNext()
            val bundle = bundleOf(
                "status" to downloadTitle,
                "title" to c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE))
            )
            c.close()

            notificationManager.sendNotification("Download Complete! id: $downloadID", applicationContext, bundle)
        }
    }

    private fun getStatusString(status: Int) : String {
        return when(status){
            DownloadManager.STATUS_FAILED -> "Failed"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Running"
            DownloadManager.STATUS_SUCCESSFUL -> "Successful"
            else -> "Unknown"
        }
    }

    private fun download() {
        custom_button.buttonState = ButtonState.Loading
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Time for breakfast"

            notificationManager.createNotificationChannel(notificationChannel)

        }

    }
    fun onRadioButtonClicked(view: View) {
        if(view is RadioButton){
            if(view.isChecked){
                when(view.id){
                    R.id.radioGlide -> {
                        URL = "https://github.com/bumptech/glide"
                        downloadTitle = applicationContext.getString(R.string.stringGlide)
                    }
                    R.id.radioRepository -> {
                        URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                        downloadTitle = applicationContext.getString(R.string.stringRepository)
                    }
                    R.id.radioRetrofit -> {
                        URL = "https://github.com/square/retrofit"
                        downloadTitle = applicationContext.getString(R.string.stringRetrofit)
                    }
                }
            }
        }
    }

}
