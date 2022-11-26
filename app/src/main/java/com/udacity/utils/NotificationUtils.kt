/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity.utils

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.R
import com.udacity.models.DownloadedFile
import com.udacity.ui.DetailActivity
import com.udacity.utils.Constants.REQUEST_CODE
import com.udacity.utils.Constants.download_notification_channel_id
import com.udacity.utils.Constants.intent_name
import com.udacity.utils.Constants.intent_status
import com.udacity.utils.Constants.notification_id


@SuppressLint("WrongConstant")
fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    downloadedFile: DownloadedFile
) {

    val detailIntent = Intent(applicationContext, DetailActivity::class.java)

    detailIntent.putExtra(intent_name, downloadedFile.fileName)
    detailIntent.putExtra(intent_status, downloadedFile.status)

    val detailPendingIntent: PendingIntent = PendingIntent.getActivity(
        applicationContext,
        REQUEST_CODE,
        detailIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        download_notification_channel_id
    )

        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setSmallIcon(R.drawable.ic_download)
        .setAutoCancel(true)

        .addAction(
            R.drawable.ic_download,
            applicationContext.getString(R.string.check_status),
            detailPendingIntent
        )

        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(notification_id, builder.build())

}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
