package hu.radosdev.szilagyiapp.splash

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import hu.radosdev.szilagyiapp.MainActivity
import hu.radosdev.szilagyiapp.R
import hu.radosdev.szilagyiapp.data.repositories.MenuRepository
import hu.radosdev.szilagyiapp.util.Constants
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

    private lateinit var sharedPreferences: SharedPreferences

    // Register for notification permission result
    private val notificationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupNotifications()
            navigateToNextScreen()
        } else {
            showPermissionDeniedDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Load the splash layout

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)

        if (isFirstLaunch) {
            checkAndRequestNotificationPermission()
            sharedPreferences.edit().putBoolean("is_first_launch", false).apply()
        } else {
            navigateToNextScreen()
        }
    }

    private fun navigateToNextScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (NetworkUtil.isNetworkAvailable(this)) {
                CoroutineScope(Dispatchers.Main).launch {
                    val menu = menuRepository.fetchMenu()
                    if (menu != null) {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    } else {
                        val intent = Intent(this@SplashActivity, ErrorActivity::class.java)
                        intent.putExtra(Constants.ERROR_ARG, Constants.URL_JSON_LOAD_ERROR)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                val intent = Intent(this, ErrorActivity::class.java)
                intent.putExtra(Constants.ERROR_ARG, Constants.NO_INTERNET_CONNECTION_DEFAULT_STRING)
                startActivity(intent)
                finish()
            }
        }, Constants.SPLASH_DELAY)
    }

    private fun setupNotifications() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionRequest.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                setupNotifications()
                navigateToNextScreen()
            }
        } else {
            setupNotifications()
            navigateToNextScreen()
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Enable Notifications")
            .setMessage("To receive important updates, please enable notifications in the settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}
