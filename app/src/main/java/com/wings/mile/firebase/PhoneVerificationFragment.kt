package com.wings.mile.firebase

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.wings.mile.R
import com.wings.mile.activity.ForgotPasswordActivity
import com.wings.mile.activity.SignupActivity
import com.wings.mile.activity.Verifyphone
import com.wings.mile.databinding.FragmentOtpVerificationBinding

import com.wings.mile.databinding.FragmentPhoneVerificationBinding
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory
import javax.inject.Inject


class PhoneVerificationFragment : BaseFragment<FragmentPhoneVerificationBinding, AuthActivityViewModel>() {

    companion object {
        fun newInstance(): PhoneVerificationFragment {
            val args = Bundle()
            val fragment = PhoneVerificationFragment()
            fragment.arguments = args
            return fragment
        }
    }
    lateinit var viewModels: MainViewModel

    private val retrofitService = RetrofitService.getInstance()
    override fun getViewModelClass(): Class<AuthActivityViewModel> = AuthActivityViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_phone_verification

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding.toolbarLayout.toolbarTitle.text = "Login with OTP"
        binding.buttonVerifyPhone.isEnabled = true

        viewModels =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )

        viewModel.selectedPhoneNumber.observe(
            requireActivity(),
            Observer<String?> { value ->
                binding.textInputEditTextPhone.setText(value ?: "")
            })

        binding.buttonVerifyPhone.setOnClickListener {
            activity?.hideKeyboard()
            if (viewModel.checkIfPhoneIsValid(binding.textInputEditTextPhone.text.toString())) {
                viewModel.sendOtpToPhone(binding.textInputEditTextPhone.text.toString(),requireActivity())
            } else {
                binding.textInputLayoutPhone.error = "Invalid Phone: Please enter phone number with country code"
            }
        }

//        binding.buttonVerifyPhone.setOnClickListener {
//
//            val number = binding.textInputEditTextPhone.text.toString()
//            val password = binding.passwordEdit.text.toString()
//
//            val message = validation(number, password);
//
//            if (message.isNullOrBlank()) {
//                binding!!.LoginAvi.show()
//                binding.login.isEnabled = false
//                viewModels.sendLoginRequest(
//                    binding.textInputEditTextPhone.text.toString(),
//                    binding.passwordEdit.text.toString()
//                ).observe(requireActivity(), Observer {
//                    if (it != null) {
//                        binding!!.LoginAvi.hide()
//                        activity?.hideKeyboard()
//                        if (viewModel.checkIfPhoneIsValid(binding.textInputEditTextPhone.text.toString())) {
//                            viewModel.sendOtpToPhone(binding.textInputEditTextPhone.text.toString(),requireActivity())
//                        } else {
//                            binding.textInputLayoutPhone.error = "Invalid Phone: Please enter phone number with country code"
//                        }
////                        val bundle = Bundle()
////                        bundle.putString("LoginRes", Gson().toJson(it.get(0)))
////                        bundle.putString("phone", binding.textInputEditTextPhone.text.toString())
////                        val intent = Intent(requireActivity(), Verifyphone::class.java)
////                        intent.putExtra("Arguments", bundle)
////                        binding.login.isEnabled = true
////
////                        startActivity(intent)
//
//                    } else {
//                        binding.login.isEnabled = true
//                        binding!!.LoginAvi.hide()
//
//                        Toast.makeText(
//                            requireActivity(),
//                            "Entered Email-ID / Password is invalid",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//
//
//                })
//            } else {
//                binding!!.LoginAvi.hide()
//                Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
//            }
//        }
//
//
//        binding.signup.setOnClickListener {
//            it.let {
//
//                val intent = Intent(requireActivity(), SignupActivity::class.java)
//                startActivity(intent)
//
//            }
//
//
//        }
//
//
//        binding.forgetpassword.setOnClickListener {
//            it.let {
//
//                val intent = Intent(requireActivity(), ForgotPasswordActivity::class.java)
//                startActivity(intent)
//
//            }
//
//
//        }

    }

    fun validation(number: String, password: String): String? {
        if (number.isNullOrEmpty()) {
            binding.textInputEditTextPhone.setError("Phone number should not be empty")
            return "Phone number should not be empty"
        } else if (number.length < 10) {
            binding.textInputEditTextPhone.setError("Phone number not valid must contains 10 Number")
            return "Phone number not valid must contains 10 Number"
        } else {
            return null
        }


    }


}
