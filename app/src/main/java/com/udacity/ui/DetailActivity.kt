package com.udacity.ui

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.R
import com.udacity.utils.Constants.intent_name
import com.udacity.utils.Constants.intent_status
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        getValuesFromIntent()

        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()
    }


    private fun getValuesFromIntent() {
        tvFileNameValue.text = intent.getStringExtra(intent_name)
        tvStatusValue.text = intent.getStringExtra(intent_status)
        intent.removeExtra("name")
        intent.removeExtra("status")
    }

    fun intentToMainActivity(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
