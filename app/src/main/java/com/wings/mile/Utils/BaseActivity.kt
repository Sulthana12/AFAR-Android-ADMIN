package com.wings.mile.Utils

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import com.wings.mile.activity.LoginActivity


open class BaseActivity : AppCompatActivity(), LogoutListener {
    val REQUEST_PHONE_CALL = "android.permission.CALL_PHONE"
    val REQUEST_SMS_CALL = "android.permission.SEND_SMS"
    protected val REQUIRED_PERMISSION_CAll =
        arrayOf(REQUEST_PHONE_CALL)
    protected val REQUIRED_PERMISSION_CAll_SMS =
        arrayOf(REQUEST_SMS_CALL)
    override fun onCreate(savedInstanceState: Bundle?) {
        //setTheme(App.getApplicationTheme());
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        //Set Listener to receive events
        App.registerSessionListener(this)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        //reset session when user interact
        App.resetSession()
    }

    override fun onSessionLogout() {
       // Toast.makeText(this,"session logout",Toast.LENGTH_LONG).show()
        Log.e("session","sessionlogout")
        // Do You Task on session out
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        )
        startActivity(intent)
    }
    public fun call(phone: String) {

        if (!allPermissionsGrantphone()) {
            val num = "tel:" + phone
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(num))
            startActivity(intent)
        } else {

            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION_CAll, 120)
        }
    }
    protected fun allPermissionsGrantphone(): Boolean {
        for (permission in REQUIRED_PERMISSION_CAll) {
            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
        }
        return false
    }

    protected fun allPermissionsGrantphonesms(): Boolean {
        for (permission in REQUIRED_PERMISSION_CAll_SMS) {
            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
        }
        return false
    }
}