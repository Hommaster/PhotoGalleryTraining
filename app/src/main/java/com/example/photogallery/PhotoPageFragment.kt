package com.example.photogallery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.photogallery.databinding.FragmentPhotoPageBinding

class PhotoPageFragment: Fragment() {

    private val args: PhotoPageFragmentArgs by navArgs()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPhotoPageBinding.inflate(
            inflater,
            container,
            false
        )
        binding.apply {
            progressBar.max = 100
            webView.apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadUrl(args.photoPageUri.toString())
                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        if (newProgress == 100) {
                            progressBar.visibility = View.GONE
                        } else {
                            progressBar.visibility = View.VISIBLE
                            progressBar.progress = newProgress
                        }
                    }

                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        val parent = requireActivity() as AppCompatActivity
                        parent.supportActionBar?.subtitle = title
                    }
                }
            }
            buttonNext.setOnClickListener {
                if(webView.canGoForward())
                    webView.goForward()
            }
            buttonBack.setOnClickListener {
                if(webView.canGoBack()) {
                    webView.goBack()
                } else {
                    @Suppress("DEPRECATION")
                    activity?.onBackPressed()
                }
            }
            return binding.root
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val parent = requireActivity() as AppCompatActivity
        parent.supportActionBar?.subtitle = null
    }
}