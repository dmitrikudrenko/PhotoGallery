package com.example.photogallery.ui

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import com.example.photogallery.scheduler.PhotoWorker

abstract class VisibleFragment : Fragment() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            resultCode = Activity.RESULT_CANCELED
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(PhotoWorker.ACTION_NOTIFICATION_NEW_PHOTOS)
        requireActivity().registerReceiver(
            receiver, filter,
            PhotoWorker.PERMISSION, null
        )
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(receiver)
    }
}