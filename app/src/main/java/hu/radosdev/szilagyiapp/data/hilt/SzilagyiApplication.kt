package hu.radosdev.szilagyiapp.data.hilt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SzilagyiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(ActivityTracker)
    }
}