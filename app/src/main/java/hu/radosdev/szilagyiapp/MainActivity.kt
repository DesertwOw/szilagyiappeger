package hu.radosdev.szilagyiapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.messaging.FirebaseMessaging
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize InAppMessageManager
        inAppMessageManager = InAppMessageManager()
        inAppMessageManager.initialize(findViewById(R.id.in_app_notification_layout))

        // Firebase token retrieval
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get the token
            val token = task.result
            Log.d("MainActivity", "FCM Token: $token")
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        val menuIcon = findViewById<ImageView>(R.id.menu_icon)
        val homeIcon = findViewById<ImageView>(R.id.menu_home)
        val menuLogo = findViewById<ImageView>(R.id.menu_logo)

        // Set up WebView and ProgressBar
        webView = findViewById(R.id.webview)
        progressBar = findViewById(R.id.progress_bar)

        // Menu icon click listener
        menuIcon.setOnClickListener { toggleDrawer() }

        // Home icon click listener
        homeIcon.setOnClickListener {
            webView.loadUrl(Constants.BASE_URL)
        }

        // Set up RecyclerView for menu items
        val recyclerView = findViewById<RecyclerView>(R.id.menu_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        menuAdapter = MenuAdapter(mutableListOf(), ::handleChildMenuItemClick, this)
        recyclerView.adapter = menuAdapter

        setupWebView()

        // Load menu items and ensure "Támogatók" is included
        menuViewModel.loadMenuItems()
        menuViewModel.menuItems.observe(this) { items: List<MainMenuItem> ->
            val updatedItems = items.toMutableList()
            // Add "Támogatók" item to the list
            updatedItems.add(MainMenuItem(title = "Támogatók", childs = null))
            menuAdapter.updateMenu(updatedItems)
        }

        // Menu logo click listener to animate and load URL
        menuLogo.setOnClickListener {
            animateLogoAndLoadUrl(menuLogo)
        }
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

                // Hide specific elements after the page has loaded
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
                    progressBar.visibility = View.GONE
                    webView.visibility = View.VISIBLE
                }
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                progressBar.visibility = View.GONE
            }
        }

        webView.webChromeClient = WebChromeClient()

        // Load the initial URL
        webView.loadUrl(Constants.BASE_URL)
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
    }

    private fun animateLogoAndLoadUrl(menuLogo: ImageView) {
        // Create an animation to scale the logo
        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            menuLogo,
            PropertyValuesHolder.ofFloat("scaleX", 1.5f), // Scale up in X direction
            PropertyValuesHolder.ofFloat("scaleY", 1.5f)  // Scale up in Y direction
        ).setDuration(300) // Duration of the animation

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            menuLogo,
            PropertyValuesHolder.ofFloat("scaleX", 1f), // Scale back to original
            PropertyValuesHolder.ofFloat("scaleY", 1f)  // Scale back to original
        ).setDuration(300) // Duration of the animation

        // Set up an AnimatorSet to play animations sequentially
        val animationSet = AnimatorSet()
        animationSet.play(scaleUp).before(scaleDown)

        // Add a listener to load the URL after the animation
        animationSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                webView.loadUrl("https://www.szilagyi-eger.hu")
            }
        })

        // Start the animation
        animationSet.start()
    }
}
