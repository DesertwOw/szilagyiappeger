package hu.radosdev.szilagyiapp

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
        menuIcon.setOnClickListener { toggleDrawer() }

        // Set up RecyclerView for menu items
        val recyclerView = findViewById<RecyclerView>(R.id.menu_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the MenuAdapter
        menuAdapter = MenuAdapter(emptyList(), ::handleMenuItemClick, ::handleChildMenuItemClick)
        recyclerView.adapter = menuAdapter

        webView = findViewById(R.id.webview)
        webView.visibility = View.INVISIBLE

        setupWebView()


        // Home icon click listener to return to default WebView page
        homeIcon.setOnClickListener {
            webView.loadUrl(defaultUrl) // Load default URL on home icon click
        }

        // Observe menu items from ViewModel
        menuViewModel.loadMenuItems()
        menuViewModel.menuItems.observe(this) { items: List<MainMenuItem> ->
            menuAdapter.updateMenu(items)
        }

        lifecycleScope.launch {
            fetchFcmToken()
        }
    }

    private fun setupWebView(){
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // JavaScript, amely elrejti a header-t
                val hideElementsScript = """
                (function() {
                    // Hide the header
                    var header = document.querySelector('header');
                    if (header) {
                        header.style.display = 'none';
                    }

                    // Hide the breadcrumb navigation
                    var breadcrumbNav = document.querySelector('nav[aria-label="breadcrumb"]');
                    if (breadcrumbNav) {
                        breadcrumbNav.style.display = 'none';
                    }
                })();
            """.trimIndent()

                webView.evaluateJavascript(hideElementsScript) {
                    // Miután a JavaScript lefutott, jelenítsük meg a WebView-t
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
            // Load the first child URL or any action
            mainMenuItem.childs?.firstOrNull()?.let { childMenuItem ->
                webView.loadUrl(childMenuItem.url)
                toggleDrawer() // Close drawer after navigation
            }
        } else {
            // Submenu exists
            // You might want to do something specific here, like showing the submenu
        }
    }

    // Handle child menu item click
    private fun handleChildMenuItemClick(childMenuItem: ChildMenuItem) {
        webView.loadUrl(childMenuItem.url)
        toggleDrawer() // Close drawer after navigation
    }

    private suspend fun fetchFcmToken() {
        val token = FirebaseMessaging.getInstance().token.await() // Coroutine-al várjuk a token-t
        Log.d(TAG, "FCM Token: $token")
    }



}

