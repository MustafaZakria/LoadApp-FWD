package com.udacity.ui

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.udacity.R
import com.udacity.models.ButtonState
import com.udacity.models.DownloadedFile
import com.udacity.utils.Constants.appURL
import com.udacity.utils.Constants.download_notification_channel_id
import com.udacity.utils.Constants.download_notification_channel_name
import com.udacity.utils.Constants.fail
import com.udacity.utils.Constants.glideURL
import com.udacity.utils.Constants.retrofitURL
import com.udacity.utils.Constants.success
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private var downloadedFile = DownloadedFile()

    private var notificationDesc = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        onClickDownloadButton()
        observeButtonState()
        createChannel(
            download_notification_channel_id,
            download_notification_channel_name
        )
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                    .apply {
                        setShowBadge(false)
                    }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "File downloaded"

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private fun observeButtonState() {
        custom_button.buttonState.observe(this, Observer {
            if (it == ButtonState.Completed && downloadedFile.status != "") {
                val notificationManager = ContextCompat.getSystemService(
                    this,
                    NotificationManager::class.java
                ) as NotificationManager
                notificationManager.sendNotification(notificationDesc, this, downloadedFile)
                downloadedFile = DownloadedFile()
            }
        })

    }

    private fun onClickDownloadButton() {
        custom_button.setOnClickListener {
            if (radioGroup.checkedRadioButtonId == -1) {
                Toast.makeText(this, this.getString(R.string.downloadToast), Toast.LENGTH_SHORT)
                    .show()
            } else {
                when (radioGroup.checkedRadioButtonId) {
                    R.id.radioDownloadGlide -> {
                        downloadedFile.url = glideURL
                        downloadedFile.fileName = this.getString(R.string.glide_download)
                        notificationDesc = this.getString(R.string.notification_glide_description)
                    }
                    R.id.radioDownloadApp -> {
                        downloadedFile.url = appURL
                        downloadedFile.fileName = this.getString(R.string.load_app_download)
                        notificationDesc = this.getString(R.string.notification_app_description)
                    }
                    R.id.radioDownloadRetrofit -> {
                        downloadedFile.url = retrofitURL
                        downloadedFile.fileName = this.getString(R.string.retrofit_download)
                        notificationDesc =
                            this.getString(R.string.notification_retrofit_description)
                    }
                }

                download()
            }

        }
    }

    lateinit var downloadManager: DownloadManager

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                val query = DownloadManager.Query()
                query.setFilterById(id)
                val cursor: Cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {
                    if (cursor.count > 0) {
                        val statusOfTheDownload =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) //8 success, 16 fail
                        if (statusOfTheDownload == 8)
                            downloadedFile.status = success
                        else
                            downloadedFile.status = fail
                    }
                }
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(downloadedFile.url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

}
