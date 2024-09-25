package hu.radosdev.szilagyiapp.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import hu.radosdev.szilagyiapp.MainActivity
import hu.radosdev.szilagyiapp.R

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var splashViewModel: SplashViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /*

        splashViewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        splashViewModel.isDataLoaded.observe(this, Observer { isLoaded ->
            if(isLoaded){
                // Amint megérkezik az adat, megyunk a main activity-ra
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })

        */
        Handler(Looper.getMainLooper()).postDelayed({
            // Navigate to MainActivity after 2 seconds
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 10000)

    }
}