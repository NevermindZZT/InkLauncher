package com.letter.inklauncher.utils

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import com.letter.inklauncher.R

/**
 * 通知工具
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
object NotificationUtils {

    /**
     * 获取返回桌面通知
     * @param context Context context
     * @return Notification 返回桌面通知
     */
    fun getHomeNotification(context: Context): Notification {
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

}