package hu.radosdev.szilagyiapp.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import hu.radosdev.szilagyiapp.MainActivity
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.repositories.MenuRepository
import hu.radosdev.szilagyiapp.util.ErrorActivity
import hu.radosdev.szilagyiapp.util.NetworkUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var menuRepository: MenuRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({
            // Check for internet connection first
            if (NetworkUtil.isNetworkAvailable(this)) {
                // Proceed to fetch menu from Firebase
                CoroutineScope(Dispatchers.Main).launch {
                    val menu = menuRepository.fetchMenu()
                    if (menu != null) {
                        // Navigate to MainActivity if everything is fine
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    } else {
                        // Backend failure, navigate to error screen with dynamic message
                        val intent = Intent(this@SplashActivity, ErrorActivity::class.java)
                        intent.putExtra("error_message", "Backend failure. Please try again later.")
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                // No internet connection, navigate to error screen with appropriate message
                val intent = Intent(this, ErrorActivity::class.java)
                intent.putExtra("error_message", "No internet connection. Please check and try again.")
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}