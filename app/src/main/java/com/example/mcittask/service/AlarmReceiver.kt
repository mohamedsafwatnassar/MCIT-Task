package com.example.mcittask.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        var ringtoneHelper: RingtoneHelper? = null
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(context!!.applicationContext, notification)
        ringtone.play()

        ringtoneHelper = object : RingtoneHelper {
            override fun stopRingtone() {
                if (ringtone.isPlaying) {
                    ringtone.stop()
                }
            }
        }
    }

    interface RingtoneHelper {
        fun stopRingtone()
    }
}