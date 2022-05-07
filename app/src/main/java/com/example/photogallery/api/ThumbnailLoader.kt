package com.example.photogallery.api

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.collection.LruCache
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.photogallery.data.FlickrFetcher
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ThumbnailLoader"
private const val MESSAGE_DOWNLOAD = 1

class ThumbnailLoader<in T>(
    private val flickrFetcher: FlickrFetcher,
    private val responseHandler: Handler,
    private val onThumbnailDownloaded: (T, Bitmap) -> Unit
) : HandlerThread(TAG) {
    private var hasQuit = false
    private val requestMap = ConcurrentHashMap<T, String>()
    private lateinit var requestHandler: Handler

    private val cache = LruCache<String, Bitmap>(100)

    val fragmentLifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun setup() {
            Log.d(TAG, "setup")
            start()
            looper
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun tearDown() {
            Log.d(TAG, "tearDown")
            quit()
        }
    }

    val viewLifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun tearDown() {
            requestHandler.removeMessages(MESSAGE_DOWNLOAD)
            requestMap.clear()
        }
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    handleRequest(target)
                }
            }
        }
    }

    private fun handleRequest(target: T) {
        val url = requestMap[target] ?: return
        val bitmap = if (cache[url] != null) cache[url]!! else flickrFetcher.fetchPhotoContent(url) ?: return
        responseHandler.post {
            cache.put(url, bitmap)
            if (requestMap[target] != url || hasQuit) {
                return@post
            }
            requestMap.remove(target)
            onThumbnailDownloaded(target, bitmap)
        }
    }

    fun queue(target: T, url: String) {
        Log.d(TAG, "queue: $url")
        requestMap[target] = url
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
            .sendToTarget()
    }
}