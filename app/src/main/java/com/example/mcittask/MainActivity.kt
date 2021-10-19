package com.example.mcittask

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.mcittask.databinding.ActivityMainBinding
import com.example.mcittask.service.AlarmReceiver
import com.example.mcittask.viewModel.MainViewModel

class MainActivity : AppCompatActivity(), AlarmReceiver.RingtoneHelper {

    private lateinit var viewRef: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewRef = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewRef.root)

        btnListener()

        getIntentFromNotification(intent)
    }

    private fun btnListener() {
        viewRef.btnMakeNotification.setOnClickListener {
            viewModel.createToken(this as Context)
        }
    }

    private fun getIntentFromNotification(intent: Intent) {
        val extras = intent.extras

        if (extras != null) {
            if (extras.containsKey("notification")) {
                stopRingtone()
            } else {
                Log.e("onclick", "Exception onclick")
            }
        }
    }

    override fun stopRingtone() {
        if (AlarmReceiver.ringtoneHelper != null) {
            AlarmReceiver.ringtoneHelper!!.stopRingtone()
        }
    }

}