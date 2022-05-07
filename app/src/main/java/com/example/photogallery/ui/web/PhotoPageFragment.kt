package com.example.photogallery.ui.web

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.photogallery.R
import com.example.photogallery.ui.VisibleFragment

class PhotoPageFragment: VisibleFragment() {
    lateinit var webView: WebView
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.progress_horizontal)
        webView = view.findViewById(R.id.web_view)
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = newProgress
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                (requireActivity() as AppCompatActivity).supportActionBar?.title = title
            }
        }
        webView.loadUrl(requireArguments().getParcelable<Uri>(ARG_URI).toString())
    }

    fun onBackPressed(): Boolean {
        return if (webView.canGoBack()) {
            webView.goBack()
            true;
        } else {
            false
        }
    }

    companion object {
        private const val ARG_URI = "uri"

        fun newInstance(uri: Uri): PhotoPageFragment {
            return PhotoPageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_URI, uri)
                }
            }
        }
    }
}