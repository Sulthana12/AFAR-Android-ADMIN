package com.wings.mile.activity

import android.R.attr.logo
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.devhoony.lottieproegressdialog.LottieProgressDialog
import com.wings.mile.MapsActivity
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.databinding.ActivitySplashScreenBinding
import com.wings.mile.databinding.AuthSignInBinding
import com.wings.mile.databinding.DriverLoginBinding
import com.wings.mile.databinding.PhonePermissionBinding


class DriverLoginActivity : BaseActivity() {
    var English: ToggleButton? = null
    var Hindi: ToggleButton? = null
    var rl: RelativeLayout? = null
    private val handler = Handler()
    lateinit var dataBinding: DriverLoginBinding
//    val REQUEST_PHONE_CALL = "android.permission.CALL_PHONE"
//    protected val REQUIRED_PERMISSION_CAll =
//        arrayOf(REQUEST_PHONE_CALL)
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dataBinding = DriverLoginBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        dataBinding.confirm.setOnClickListener(){
            val intent = Intent(this, DriverRegisterActivity::class.java)
            startActivity(intent)
        }

    }



}