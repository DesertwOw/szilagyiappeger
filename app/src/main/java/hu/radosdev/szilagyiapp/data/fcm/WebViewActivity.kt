package hu.radosdev.szilagyiapp.data.fcm

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import hu.radosdev.szilagyiapp.R

class WebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        webView = findViewById(R.id.notif_web_view)
        webView.webViewClient = WebViewClient()

        // Az URL átadása az intentből
        val url = intent.getStringExtra("url")
        url?.let {
            webView.loadUrl(it)
        }
    }
}