package com.wings.mile.activity

import android.R.attr.logo
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.devhoony.lottieproegressdialog.LottieProgressDialog
import com.wings.mile.DashboardActivity
import com.wings.mile.MapsActivity
import com.wings.mile.R
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.databinding.ActivitySplashScreenBinding


class SplashScreenActivity : AppCompatActivity() {

    private val handler = Handler()
    lateinit var dataBinding: ActivitySplashScreenBinding

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

        val display = windowManager.defaultDisplay
        val size = Point()
        display.  getSize(size)
        val width = size.x
        val height = size.y
        Pref_storage.setDetail(this, "width", width.toString())
        Pref_storage.setDetail(this, "height", height.toString())


        handler.postDelayed({ // Checking for first time launch - before calling setContentView()
            startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
            finish()
        }, 1500)
        val myanim: Animation = AnimationUtils.loadAnimation(this, R.anim.transition)
        dataBinding.img.startAnimation(myanim)

    }


}