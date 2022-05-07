package com.example.photogallery

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.photogallery.ui.gallery.PhotoGalleryActivity

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (resultCode != Activity.RESULT_CANCELED) {
            showNotification(context)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification(context: Context) {
        val nm = NotificationManagerCompat.from(context)
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            PhotoGalleryActivity.intent(context),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val nb = NotificationCompat.Builder(
            context,
            PhotoGalleryApplication.NOTIFICATION_CHANNEL
        )
            .setAutoCancel(true)
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setTicker("New photos ticker")
            .setContentTitle("Photos gallery")
            .setContentText("You have new photos")
            .setContentIntent(contentIntent)
        nm.notify(1, nb.build())
    }
}