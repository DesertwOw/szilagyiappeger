package hu.radosdev.szilagyiapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import hu.radosdev.szilagyiapp.data.entity.ChildMenuItem
import hu.radosdev.szilagyiapp.data.entity.MainMenuItem
import hu.radosdev.szilagyiapp.data.fcm.InAppMessageManager
import hu.radosdev.szilagyiapp.menu.MenuAdapter
import hu.radosdev.szilagyiapp.menu.MenuViewModel
import hu.radosdev.szilagyiapp.notifications.NotificationActivity
import hu.radosdev.szilagyiapp.splash.SplashActivity
import hu.radosdev.szilagyiapp.util.Constants


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private val menuViewModel: MenuViewModel by viewModels()
    private lateinit var webView: WebView
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var inAppMessageManager: InAppMessageManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inAppMessageManager = InAppMessageManager()
        inAppMessageManager.initialize(findViewById(R.id.in_app_notification_layout))

        drawerLayout = findViewById(R.id.drawer_layout)
        val menuIcon = findViewById<ImageView>(R.id.menu_icon)
        val homeIcon = findViewById<ImageView>(R.id.menu_home)
        //val notificationIcon = findViewById<ImageView>(R.id.notifi_icon)

        webView = findViewById(R.id.webview)

        menuIcon.setOnClickListener { toggleDrawer() }

        homeIcon.setOnClickListener {
            webView.loadUrl(Constants.BASE_URL)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.menu_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        menuAdapter = MenuAdapter(
            emptyList(),
            ::handleChildMenuItemClick,
            this,
            ::showLoadingScreen,
            ::hideLoadingScreen
        )
        recyclerView.adapter = menuAdapter

        setupWebView()

        menuViewModel.loadMenuItems()
        menuViewModel.menuItems.observe(this){
            items: List<MainMenuItem> ->
            menuAdapter.updateMenu(items)
        }

        /*
        notificationIcon.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

         */
    }

    private fun showLoadingScreen() {
        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra(Constants.IS_LOADING_SCREEN, true)
        startActivity(intent)
    }

    private fun hideLoadingScreen() {
        SplashActivity.finishIfActive()
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
                    webView.visibility = View.VISIBLE
                }
            }
        }

        webView.webChromeClient = WebChromeClient()
        webView.loadUrl(Constants.BASE_URL)
    }

    private fun toggleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun handleChildMenuItemClick(childMenuItem: ChildMenuItem) {
        webView.loadUrl(childMenuItem.url)
        toggleDrawer()
    }

}


