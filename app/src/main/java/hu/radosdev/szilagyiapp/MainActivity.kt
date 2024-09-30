package hu.radosdev.szilagyiapp

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import hu.radosdev.szilagyiapp.menu.MenuAdapter
import hu.radosdev.szilagyiapp.menu.MenuViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private val menuViewModel: MenuViewModel by viewModels()
    lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        // Observe the LiveData from the ViewModel and update RecyclerView with the fetched menu items
        menuViewModel.loadMenuItems() // Trigger fetching menu items
        menuViewModel.menuItems.observe(this, Observer { items ->
            adapter.updateMenu(items) // Update adapter with new menu items
        })

        webView = findViewById(R.id.webview)

        webView.loadUrl("https://www.szilagyi-eger.hu/")

        webView.settings.javaScriptEnabled = true

        webView.webViewClient = WebViewClient()
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
