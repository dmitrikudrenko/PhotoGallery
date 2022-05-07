package com.example.photogallery.ui.web

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.photogallery.R

class PhotoPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_page)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragment_container, PhotoPageFragment.newInstance(
                        intent.getParcelableExtra(
                            EXTRA_URI
                        )!!
                    )
                )
                .commit()
        }
    }

    override fun onBackPressed() {
        if (!(supportFragmentManager.findFragmentById(R.id.fragment_container) as PhotoPageFragment).onBackPressed()) {
            super.onBackPressed()
        }
    }

    companion object {
        private const val EXTRA_URI = "uri"

        fun newIntent(context: Context, uri: Uri): Intent {
            return Intent(context, PhotoPageActivity::class.java).putExtra(EXTRA_URI, uri)
        }
    }
}