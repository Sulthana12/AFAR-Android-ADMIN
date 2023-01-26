package com.wings.mile.firebase

import androidx.databinding.ktx.BuildConfig
import com.wings.mile.firebase.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class BaseApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            //Timber.plant(Timber.DebugTree())
        }
    }
}