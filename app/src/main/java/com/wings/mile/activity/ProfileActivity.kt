package com.wings.mile.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.devhoony.lottieproegressdialog.LottieProgressDialog
import com.example.awesomedialog.*
import com.google.gson.Gson
import com.wings.mile.DashboardActivity
import com.wings.mile.NavigationActivity
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.databinding.ForgerpasswordBinding
import com.wings.mile.databinding.ProfileBinding
import com.wings.mile.model.SignUpRequest
import com.wings.mile.model.loginResponseItem
import com.wings.mile.service.RetrofitService
import com.wings.mile.service.RetrofitService.Companion.gson
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory

import java.util.regex.Pattern

class ProfileActivity : BaseActivity() {

    private lateinit var binding: ProfileBinding
    lateinit var viewModel: MainViewModel
    private val retrofitService = RetrofitService.getInstance()
    var mediaPlayer: MediaPlayer? = null
    var nameCheck:Boolean=false
    var emailCheck:Boolean=false
    var numCheck:Boolean=false

    lateinit var request:SignUpRequest
    lateinit var loginResponse: loginResponseItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        binding = ProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarLayout.toolbarTitle.text = "Profile"

        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )
        val obj = intent

        //val bundle = intent.extras
        // bundle = intent.getBundleExtra("Arguments")


        updateprofile()


        binding.save.setOnClickListener {

            val number = binding.mobileEdittext.text.toString()
            val name = binding.nameEdittext.text.toString()
            val email = binding.emailEdittext.text.toString()

            val token=Pref_storage.getDetail(this,"fcmtoken")
            binding!!.LoginAvi.show()
            val signup = SignUpRequest(email,name,
                "",number, "", loginResponse.user_id, user_type_flg =loginResponse.user_type_flg, en_flg = "u",
            notification_token = token)
            Log.e("signup",""+Gson().toJson(signup));
            viewModel.sendSignupRequestOrProfileUpdate(signup).observe(this) {
                it.let { resource ->
                    when (resource.status) {
                        com.wings.mile.Utils.Status.LOADING -> {
                        }
                        com.wings.mile.Utils.Status.SUCCESS -> {
                            binding!!.LoginAvi.hide()
                            AwesomeDialog.build(this)
                                .title("Congratulations", null, R.color.labelrecyclerview)
                                .body("Profile Updated Successfully", null, R.color.labelrecyclerview)
                                .icon(R.drawable.user)
                                .onPositive(
                                    "Ok",
                                    R.drawable.login_border_theme

                                ) {
                                    val intent = Intent(this, NavigationActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                        }
                        com.wings.mile.Utils.Status.ERROR -> {
                            binding!!.LoginAvi.hide()
                            Toast.makeText(this, "Profile updated Error", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

//            viewModel.sendSignupRequestOrProfileUpdate(
//                signup
//            ).observe(this, Observer {
//                if (it != null) {
//                    binding!!.LoginAvi.hide()
//                    AwesomeDialog.build(this)
//                        .title("Congratulations", null, R.color.labelrecyclerview)
//                        .body("Account Created Successfully", null, R.color.labelrecyclerview)
//                        .icon(R.drawable.user)
//                        .onPositive(
//                            "Ok",
//                            R.drawable.login_border_theme
//
//                        ) {
//                            val intent = Intent(this, DashboardActivity::class.java)
//                            startActivity(intent)
//                            finish()
//                        }
//
//
//                } else {
//                    binding!!.LoginAvi.hide()
//                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
//                }
//
//
//            })


        }

        binding.nameEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val name=s.toString()
                if(name!=loginResponse.name){
                    request.first_name=name
                     nameCheck=true
                    enableButton()
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        binding.emailEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                var email=s.toString()
                if(email!=loginResponse.email_id){
                    request.email_id=email
                    emailCheck=true
                    enableButton()
                }


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        binding.mobileEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var mobile=s.toString()
                if(mobile!=loginResponse.phone_num){
                    request.phone_num=mobile
                    numCheck=true
                    enableButton()
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })


    }

    private fun setViewModel() {

    }

    fun enableButton(){
        binding.save.isEnabled=(nameCheck || numCheck || emailCheck)

    }


    fun LottieProgressDialog.Hide(){
        this.cancel()
    }

    fun LottieProgressDialog.Show(){
        this.show()
    }


    fun Dialog(): LottieProgressDialog {
        return LottieProgressDialog(
            context = this,
            isCancel = true,
            dialogWidth = null,
            dialogHeight = null,
            animationViewWidth = null,
            animationViewHeight = null,
            fileName = LottieProgressDialog.SAMPLE_1,
            title = null,
            titleVisible = null
        )
    }

    fun updateprofile(){
        loginResponse  = Gson().fromJson(Pref_storage.getDetail(this,"LoginRes"), com.wings.mile.model.loginResponseItem::class.java)
        request=SignUpRequest(loginResponse.email_id,loginResponse.name,"",loginResponse.phone_num,"",loginResponse.user_id, user_type_flg = loginResponse.user_type_flg, en_flg = "A",
            notification_token =Pref_storage.getDetail(this,"fcmtoken") )
        viewModel.updateprofile(
            loginResponse.user_id
        ).observe(this, Observer {
            if (it != null) {
                Pref_storage.setDetail(this,"LoginRes", Gson().toJson(it.get(0)))
                loginResponse  = Gson().fromJson(Pref_storage.getDetail(this,"LoginRes"), com.wings.mile.model.loginResponseItem::class.java)

                request.user_type_flg=loginResponse.user_type_flg
                request.user_id=loginResponse.user_id
                request.first_name=loginResponse.name
                request.email_id=loginResponse.email_id
                request.phone_num=loginResponse.phone_num
                binding.nameEdittext.setText(loginResponse.name)
                binding.emailEdittext.setText(loginResponse.email_id)
                binding.mobileEdittext.setText(loginResponse.phone_num)
            } else {
                Toast.makeText(
                    this,
                    "Entered Email-ID / Password is invalid",
                    Toast.LENGTH_LONG
                ).show()
            }


        })
    }

}