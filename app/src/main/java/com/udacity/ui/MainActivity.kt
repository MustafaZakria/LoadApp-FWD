package com.udacity.ui

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.R
import com.udacity.utils.Constants.appURL
import com.udacity.utils.Constants.glideURL
import com.udacity.utils.Constants.retrofitURL
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        onClickDownloadButton()
    }

    private fun onClickDownloadButton() {
        custom_button.setOnClickListener {
            if (radioGroup.checkedRadioButtonId == -1) {
                Toast.makeText(this, this.getString(R.string.downloadToast), Toast.LENGTH_SHORT)
                    .show()
            } else {
                when (radioGroup.checkedRadioButtonId) {
                    R.id.radioDownloadGlide -> URL = glideURL
                    R.id.radioDownloadApp -> URL = appURL
                    R.id.radioDownloadRetrofit -> URL = retrofitURL
                }
                download()
            }

        }
    }

    lateinit var downloadManager: DownloadManager

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(id == downloadID) {
                val query = DownloadManager.Query()
                query.setFilterById(id)
                val cursor: Cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {
                    if (cursor.count > 0) {
                        val statusOfTheDownload =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) //8 success, 16 fail
                        Log.d("***", statusOfTheDownload.toString())
                    }
                }
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }

    companion object {
        private var URL = ""
        private const val CHANNEL_ID = "channelId"
    }

}
