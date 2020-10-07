package com.letter.inklauncher.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.createNotificationChannel
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.preference.PreferenceManager
import com.letter.inklauncher.R

/**
 * notification service
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class NotificationService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                getString(R.string.notification_channel_intent_id),
                getString(R.string.notification_channel_intent_name),
                NotificationManager.IMPORTANCE_MAX)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("enable_back_to_home_notification", false)) {
            startForeground(-1, getIntentNotification(this))
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(-1)
            } else {
                stopForeground(true)
            }
        }
        return START_STICKY
    }
}

/**
 * 获取前台通知
 * @param context Context context
 * @return Notification 前台通知
 */
private fun getIntentNotification(context: Context): Notification {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        addCategory(Intent.CATEGORY_HOME)
    }
    val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder(context.applicationContext,
            context.getString(R.string.notification_channel_intent_id))
    } else {
        Notification.Builder(context.applicationContext)
    }
    builder.apply {
        setContentTitle(context.getString(R.string.notification_back_to_home_title))
        setContentText(context.getString(R.string.notification_back_to_home_content))
        setWhen(System.currentTimeMillis())
        setSmallIcon(R.drawable.ic_notify_small)
        setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_round))
        setContentIntent(pi)
        setAutoCancel(true)
    }
    return builder.build()
}