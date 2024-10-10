package hu.radosdev.szilagyiapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import hu.radosdev.szilagyiapp.data.entity.ChildMenuItem
import hu.radosdev.szilagyiapp.data.entity.MainMenuItem
import hu.radosdev.szilagyiapp.menu.MenuAdapter
import hu.radosdev.szilagyiapp.menu.MenuViewModel
import hu.radosdev.szilagyiapp.notifications.NotificationActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private val menuViewModel: MenuViewModel by viewModels()
    private lateinit var webView: WebView
    private lateinit var menuAdapter: MenuAdapter
    private val TAG = "FCM"
    private val defaultUrl = "https://www.szilagyi-eger.hu/" // Default URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up drawer layout and menu icon for toggling
        drawerLayout = findViewById(R.id.drawer_layout)
        val menuIcon = findViewById<ImageView>(R.id.menu_icon)
        val homeIcon = findViewById<ImageView>(R.id.menu_home)
        val notifIcon = findViewById<ImageView>(R.id.notifi_icon)

        // Initialize the WebView (this is the crucial part)
        webView = findViewById(R.id.webview)  // Make sure the ID matches your layout

        // Existing code for menu icon
        menuIcon.setOnClickListener { toggleDrawer() }

        // Home icon click listener to return to default WebView page
        homeIcon.setOnClickListener {
            webView.loadUrl(defaultUrl) // Load default URL on home icon click
        }

        // Set up RecyclerView for menu items
        val recyclerView = findViewById<RecyclerView>(R.id.menu_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        menuAdapter = MenuAdapter(emptyList(), ::handleMenuItemClick, ::handleChildMenuItemClick)
        recyclerView.adapter = menuAdapter

        // WebView setup
        setupWebView()

        // Fetch FCM token in background
        lifecycleScope.launch {
            fetchFcmToken()
        }

        // Notification icon click listener to navigate to NotificationActivity
        notifIcon.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupWebView(){
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // JavaScript to hide header and breadcrumb
                val hideElementsScript = """
                (function() {
                    var header = document.querySelector('header');
                    if (header) {
                        header.style.display = 'none';
                    }
                    var breadcrumbNav = document.querySelector('nav[aria-label="breadcrumb"]');
                    if (breadcrumbNav) {
                        breadcrumbNav.style.display = 'none';
                    }
                })();
            """.trimIndent()

                webView.evaluateJavascript(hideElementsScript) {
                    // After the JavaScript runs, display the WebView
                    webView.visibility = View.VISIBLE
                }
            }
        }

        webView.webChromeClient = WebChromeClient()
        webView.loadUrl(defaultUrl)
    }

    private fun toggleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    // Handle main menu item click
    private fun handleMenuItemClick(mainMenuItem: MainMenuItem) {
        if (mainMenuItem.childs.isNullOrEmpty()) {
            mainMenuItem.childs?.firstOrNull()?.let { childMenuItem ->
                webView.loadUrl(childMenuItem.url)
                toggleDrawer()
            }
        } else {
            // Submenu exists, do something here
        }
    }

    // Handle child menu item click
    private fun handleChildMenuItemClick(childMenuItem: ChildMenuItem) {
        webView.loadUrl(childMenuItem.url)
        toggleDrawer()
    }

    private suspend fun fetchFcmToken() {
        val token = FirebaseMessaging.getInstance().token.await()
        Log.d(TAG, "FCM Token: $token")
    }
}


