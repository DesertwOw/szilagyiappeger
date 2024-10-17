package hu.radosdev.szilagyiapp.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
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

    companion object {
        private var activeInstance: SplashActivity? = null

        fun finishIfActive() {
            activeInstance?.finish()
        }
    }

    @Inject
    lateinit var menuRepository: MenuRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isLoadingScreen = intent.getBooleanExtra(Constants.IS_LOADING_SCREEN, false)

        setContentView(R.layout.activity_splash)

        if (!isLoadingScreen) {
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
    }

    override fun onStart() {
        super.onStart()
        activeInstance = this
    }

    override fun onStop() {
        super.onStop()
        if (activeInstance === this) {
            activeInstance = null
        }
    }
}
