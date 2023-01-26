package com.wings.mile.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.awesomedialog.*
import com.google.firebase.installations.FirebaseInstallations
import com.google.gson.Gson
import com.wings.mile.DashboardActivity
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.ViewDrivers.DetailActivity
import com.wings.mile.databinding.AuthSignInBinding
import com.wings.mile.databinding.ContentMainBinding
import com.wings.mile.firebase.AuthActivity
import com.wings.mile.listItem
import com.wings.mile.listener.clickListener
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory

class LoginActivity : BaseActivity(), clickListener {
    private val TAG = "MainActivity"
    private lateinit var binding: AuthSignInBinding

    lateinit var viewModel: MainViewModel

    private val retrofitService = RetrofitService.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = AuthSignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // call()
        binding.login.isEnabled = true
          setNewFcm()
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )

        binding.login.setOnClickListener {
// on below line we are creating two
            // variables for phone and message
            val phoneNumber = "9677546224"
            //val message = "Farcaps otp 287654"

            // on the below line we are creating a try and catch block
//            try {
//
//                // on below line we are initializing sms manager.
//                //as after android 10 the getDefault function no longer works
//                //so we have to check that if our android version is greater
//                //than or equal toandroid version 6.0 i.e SDK 23
//                val smsManager: SmsManager
//                if (Build.VERSION.SDK_INT>=23) {
//                    //if SDK is greater that or equal to 23 then
//                    //this is how we will initialize the SmsManager
//                    smsManager = this.getSystemService(SmsManager::class.java)
//                }
//                else{
//                    //if user's SDK is less than 23 then
//                    //SmsManager will be initialized like this
//                    smsManager = SmsManager.getDefault()
//                }
//
//                // on below line we are sending text message.
//                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
//
//                // on below line we are displaying a toast message for message send,
//                Toast.makeText(applicationContext, "Message Sent", Toast.LENGTH_LONG).show()
//
//            } catch (e: Exception) {
//
//                // on catch block we are displaying toast message for error.
//                Toast.makeText(applicationContext, "Please enter all the data.."+e.message.toString(), Toast.LENGTH_LONG)
//                    .show()
//            }
//        }
            val number = binding.numberEdit.text.toString()
            val password = binding.passwordEdit.text.toString()

            val message = validation(number, password);
            binding!!.LoginAvi.visibility = View.VISIBLE

            if (message.isNullOrBlank()) {
                binding!!.LoginAvi.show()
                binding.login.isEnabled = false

                viewModel.sendLoginRequest(
                    binding.numberEdit.text.toString(),
                    binding.passwordEdit.text.toString()
                ).observe(this) {
                    it.let { resource ->
                        when (resource.status) {
                            com.wings.mile.Utils.Status.LOADING -> {
                            }
                            com.wings.mile.Utils.Status.SUCCESS -> {
                                binding!!.LoginAvi.hide()
                                Pref_storage.setDetail(
                                    this,
                                    "LoginRes",
                                    Gson().toJson(it.data!!.get(0))
                                )
                                Pref_storage.setDetail(
                                    this,
                                    "userpassword",
                                    binding.passwordEdit.text.toString()
                                )

                                val bundle = Bundle()

                                bundle.putString("phone", binding.numberEdit.text.toString())
                                val intent = Intent(this, OtpActivity::class.java)
                                intent.putExtra("Arguments", bundle)
                                binding.login.isEnabled = true

                                startActivity(intent)
                            }
                            com.wings.mile.Utils.Status.ERROR -> {
                                binding.login.isEnabled = true
                                binding!!.LoginAvi.hide()

                                Toast.makeText(
                                    this,
                                    "Entered Email-ID / Password is invalid",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }

            } else {
                binding!!.LoginAvi.hide()
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

        }

        binding.signup.setOnClickListener {
            it.let {

                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)

            }


        }


        binding.forgetpassword.setOnClickListener {
            it.let {

                val intent = Intent(this, ForgotPasswordActivity::class.java)
                startActivity(intent)

            }


        }



    }

    fun validation(number: String, password: String): String? {
         if (binding.numberEdit.text.isNullOrEmpty()) {
             binding.numberEdit.setError("Email-ID should not be empty")
            return "Email-ID should not be empty"
        } else if (password.isNullOrEmpty()) {
            return "Password number should not be empty"
        } else if (password.length < 8) {
            binding.passwordEdit.setError("Password must contains minimum 8 characters")
            return "Password must contains minimum 8 characters"
        } else {
            return null
        }


    }


    override fun onItem(res: listItem) {

        val bundle = bundleOf(
            "firstName" to res.firstName,
            "lastName" to res.lastName,
            "jobtitle" to res.jobtitle,
            "email" to res.email,

            )
        val intent: Intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("Res", bundle)
        startActivity(intent)
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this!!.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun setNewFcm() {
        FirebaseInstallations.getInstance().getToken(true)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@addOnCompleteListener
                }
                if (task.result != null) {
                    val token: String = task.result.token
                    Pref_storage.setDetail(this,"fcmtoken",token)
                    Log.e("token-->",""+token);
                }
            }
    }
    fun call() {

        if (!allPermissionsGrantphonesms()) {
//            launchHomeScreen()
        } else {

            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION_CAll_SMS, 120)
        }
    }
}


