package hu.radosdev.szilagyiapp.util

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.splash.SplashActivity

class ErrorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)

        val errorMessage = intent.getStringExtra("error_message") ?: "An error occurred"
        val errorTextView: TextView = findViewById(R.id.error_message)
        errorTextView.text = errorMessage

        val retryButton: Button = findViewById(R.id.button_retry)
        retryButton.setOnClickListener {
            // Check internet connection again
            if (NetworkUtil.isNetworkAvailable(this)) {
                // Restart SplashActivity if the connection is available
                val intent = Intent(this, SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                // Show a message if still no internet connection
                Toast.makeText(this, "No internet connection. Please check and try again.", Toast.LENGTH_LONG).show()
            }
        }
    }
}