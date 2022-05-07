package com.example.photogallery.ui.gallery

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.photogallery.R

class PhotoGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, PhotoGalleryFragment.newInstance())
                .commit()
        }
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, PhotoGalleryActivity::class.java)
        }
    }
}