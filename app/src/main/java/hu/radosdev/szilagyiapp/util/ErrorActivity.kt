package hu.radosdev.szilagyiapp.util

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.splash.SplashActivity

class ErrorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)

        val errorMessage = intent.getStringExtra(Constants.ERROR_ARG) ?: Constants.DEFAULT_ERROR_MESSAGE
        val errorTextView: TextView = findViewById(R.id.error_message)
        errorTextView.text = errorMessage

        val retryButton: Button = findViewById(R.id.button_retry)
        retryButton.setOnClickListener {
            if (NetworkUtil.isNetworkAvailable(this)) {
                val intent = Intent(this, SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                Toast.makeText(this, Constants.NO_INTERNET_CONNECTION_DEFAULT_STRING, Toast.LENGTH_LONG).show()
            }
        }
    }
}