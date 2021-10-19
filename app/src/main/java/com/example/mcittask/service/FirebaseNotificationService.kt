package com.example.mcittask.service

import android.app.*
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.example.mcittask.MainActivity
import com.example.mcittask.R
import com.example.mcittask.constants.AppConstants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class FirebaseNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {
            val map: Map<String, String> = remoteMessage.data

            val message = map["message"]

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                createOreoNotification(message!!)
            } else {
                createNormalNotification(message!!)
            }
        }
    }

    private fun createNormalNotification(message: String) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, MainActivity::class.java)

        intent.putExtra("notification", "clicked")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, AppConstants.CHANNEL_ID)
            .setContentTitle("Push Notification")
            .setContentText(message)
            .setAutoCancel(true)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        manager.notify(Random().nextInt(85 - 65), builder.build())

        setAlarm()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOreoNotification(message: String) {
        val channel = NotificationChannel(
            AppConstants.CHANNEL_ID,
            AppConstants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
        )

        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("notification", "clicked")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = Notification.Builder(this, AppConstants.CHANNEL_ID)
            .setContentTitle("Push Notification")
            .setContentText(message)
            .setAutoCancel(true)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(Random().nextInt(85 - 65), notification)

        setAlarm()
    }

    private fun setAlarm() {
        val alarmManager =
            applicationContext.getSystemService(LifecycleService.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent)
    }

}