package com.app_muslim.surah_yasin.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app_muslim.surah_yasin.R
import com.app_muslim.surah_yasin.app.App.Companion.CHANNEL_ID
import com.app_muslim.surah_yasin.data.preference.CorePreference
import com.app_muslim.surah_yasin.data.preference.SettingPreference
import com.app_muslim.surah_yasin.view.activity.MainActivity
import java.util.*

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val DHIKR_REMINDER_ID = 1
        private const val DAY_TO_NEXT_PUSH = 2
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            showNotification(it)
            scheduleNextPushNotification(it)
        }
    }

    private fun showNotification(context: Context) {
        val settingPreference = SettingPreference(CorePreference.getInstance(context))
        if (settingPreference.notification) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.getBroadcast(
                    context, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            else PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_logo)
                .setContentTitle(context.getString(R.string.label_notification_title))
                .setContentText(context.getString(R.string.label_notification_text))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))

            NotificationManagerCompat.from(context)
                .notify(DHIKR_REMINDER_ID, notificationBuilder.build())
        }
    }

    private fun scheduleNextPushNotification(context: Context) {
        val settingPreference = SettingPreference(CorePreference.getInstance(context))

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val alarmPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        else PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = settingPreference.timeNotification
        }
        calendar.add(Calendar.DAY_OF_MONTH, DAY_TO_NEXT_PUSH)
        settingPreference.timeNotification = calendar.timeInMillis
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmPendingIntent)
    }

}