package hu.radosdev.szilagyiapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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
import hu.radosdev.szilagyiapp.util.Constants

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private val menuViewModel: MenuViewModel by viewModels()
    private lateinit var webView: WebView
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var inAppMessageManager: InAppMessageManager
    private lateinit var progressBar: ProgressBar
    private lateinit var homeIcon: ImageView
    private lateinit var toolbarTitle: TextView
    private lateinit var errorLayout: View // View for error message and button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inAppMessageManager = InAppMessageManager()
        inAppMessageManager.initialize(findViewById(R.id.in_app_notification_layout))
        drawerLayout = findViewById(R.id.drawer_layout)
        val menuIcon = findViewById<ImageView>(R.id.menu_icon)
        homeIcon = findViewById(R.id.home)
        toolbarTitle = findViewById(R.id.toolbar_title)
        webView = findViewById(R.id.webview)
        progressBar = findViewById(R.id.progress_bar)
        errorLayout = findViewById(R.id.error_layout)

        val retryButton = findViewById<Button>(R.id.retry_button)
        retryButton.setOnClickListener {
            errorLayout.visibility = View.GONE
            webView.loadUrl(Constants.BASE_URL)
        }

        // WebView setup
        setupWebView()

        menuIcon.setOnClickListener { toggleDrawer() }

        homeIcon.setOnClickListener {
            webView.loadUrl(Constants.BASE_URL)
            resetToolbarToHome()
        }

        val menuLogo = findViewById<ImageView>(R.id.menu_logo)
        menuLogo.setOnClickListener {
            animateLogoAndLoadUrl(menuLogo)
            webView.loadUrl(Constants.BASE_URL)
            resetToolbarToHome()
        }

        // RecyclerView for menu
        val recyclerView = findViewById<RecyclerView>(R.id.menu_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        menuAdapter = MenuAdapter(mutableListOf(), ::handleChildMenuItemClick, this)
        recyclerView.adapter = menuAdapter

        // Load menu items and observe
        menuViewModel.loadMenuItems()
        menuViewModel.menuItems.observe(this) { items: List<MainMenuItem> ->
            val updatedItems = items.toMutableList()
            updatedItems.add(MainMenuItem(title = Constants.SUPPORTERS, childs = null))
            menuAdapter.updateMenu(updatedItems)
        }

        // Load incoming URL or default
        val incomingUrl = intent.getStringExtra(Constants.URL)
        if (incomingUrl != null) {
            webView.loadUrl(incomingUrl)
            toolbarTitle.text = intent.getStringExtra(Constants.TITLE)
            toolbarTitle.visibility = View.VISIBLE
            homeIcon.visibility = View.GONE
        } else {
            webView.loadUrl(Constants.BASE_URL)
        }
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                webView.visibility = View.GONE
                errorLayout.visibility = View.GONE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                val hideElementsScript = """
                    (function() {
                        var header = document.querySelector('header');
                        if (header) header.style.display = 'none';
                        var breadcrumbNav = document.querySelector('nav[aria-label="breadcrumb"]');
                        if (breadcrumbNav) breadcrumbNav.style.display = 'none';
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
                webView.visibility = View.GONE
                errorLayout.visibility = View.VISIBLE // Show error layout
            }
        }

        webView.webChromeClient = WebChromeClient()
    }

    private fun toggleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START, true)
        } else {
            drawerLayout.openDrawer(GravityCompat.START, true)
        }
    }

    private fun handleChildMenuItemClick(childMenuItem: ChildMenuItem) {
        webView.loadUrl(childMenuItem.url)
        toggleDrawer()

        homeIcon.visibility = View.GONE
        toolbarTitle.text = childMenuItem.title
        toolbarTitle.visibility = View.VISIBLE
    }

    private fun resetToolbarToHome() {
        homeIcon.visibility = View.VISIBLE
        toolbarTitle.visibility = View.GONE
    }

    private fun animateLogoAndLoadUrl(menuLogo: ImageView) {
        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            menuLogo,
            PropertyValuesHolder.ofFloat(Constants.SCALE_X, Constants.VALUE_ONETWO),
            PropertyValuesHolder.ofFloat(Constants.SCALE_Y, Constants.VALUE_ONETWO)
        ).setDuration(Constants.DURATION)

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            menuLogo,
            PropertyValuesHolder.ofFloat(Constants.SCALE_X, Constants.VALUE_ONE),
            PropertyValuesHolder.ofFloat(Constants.SCALE_Y, Constants.VALUE_ONE)
        ).setDuration(Constants.DURATION)

        val animationSet = AnimatorSet()
        animationSet.play(scaleUp).before(scaleDown)
        animationSet.start()
    }
}
