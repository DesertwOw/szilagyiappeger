package hu.radosdev.szilagyiapp.data.fcm

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.util.Constants

class WebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        webView = findViewById(R.id.notif_web_view)
        progressBar = findViewById(R.id.progress_bar_web)

        setupWebView()
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                webView.visibility = View.GONE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                val hideElementsScript = """
                    (function() {
                        var header = document.querySelector('header');
                        if (header) header.style.display = 'none';
                        var breadcrumbNav = document.querySelector('nav[aria-label="breadcrumb"]');
                        if (breadcrumbNav) breadcrumbNav.style.display = 'none';
                
                        // Additional code to hide specified elements
                        document.getElementsByClassName('container')[0].style.display='none'; 
                        document.querySelector('.bg-brown').style.display='none'; 
                    })();
                """.trimIndent()


                webView.evaluateJavascript(hideElementsScript) {
                    progressBar.visibility = View.GONE
                    webView.visibility = View.VISIBLE
                }
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                progressBar.visibility = View.GONE
                Toast.makeText(this@WebViewActivity, "Failed to load page", Toast.LENGTH_SHORT).show()
            }
        }

        webView.webChromeClient = WebChromeClient()

        // Retrieve and load URL from intent, handle missing URL
        val url = intent.getStringExtra(Constants.URL)
        if (!url.isNullOrEmpty()) {
            webView.loadUrl(url)
        } else {
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show()
        }
    }
}
