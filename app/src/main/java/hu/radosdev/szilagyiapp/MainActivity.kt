package hu.radosdev.szilagyiapp

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import hu.radosdev.szilagyiapp.databinding.ActivityMainBinding
import hu.radosdev.szilagyiapp.menu.MenuAdapter
import hu.radosdev.szilagyiapp.menu.MenuViewModel
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var webView: WebView
    private val menuViewModel: MenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up the drawer layout and menu icon for toggling
        drawerLayout = findViewById(R.id.drawer_layout)
        val menuIcon = findViewById<ImageView>(R.id.menu_icon)
        menuIcon.setOnClickListener {
            toggleDrawer()
        }

        // Set up RecyclerView for the menu inside the drawer
        val recyclerView = findViewById<RecyclerView>(R.id.menu_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MenuAdapter(emptyList()) // Initially an empty list
        recyclerView.adapter = adapter

        // Observe the menu data using coroutines
        menuViewModel.fetchMenu() // Trigger fetching menu items
        lifecycleScope.launchWhenStarted {
            menuViewModel.menu.collect { menu ->
                menu?.let {
                    adapter.updateMenu(it.mainMenu) // Update adapter with new menu items
                }
            }
        }

        // Set up WebView
        webView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Inject JavaScript to hide the <header> element
                val script = "document.querySelector('header').style.display='none';"
                webView.evaluateJavascript(script, null)
            }
        }
        webView.webChromeClient = WebChromeClient()

        // Load the webpage
        webView.loadUrl("https://www.szilagyi-eger.hu/")
    }

    // Method to toggle the navigation drawer
    private fun toggleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }
}
