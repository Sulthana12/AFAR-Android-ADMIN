package com.wings.mile.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.adapter.LicenseExpiryAdapter
import com.wings.mile.adapter.UserAccountAdapter
import com.wings.mile.databinding.ActivityLicenseExpiryNavigationBinding
import com.wings.mile.model.User_Accountitem
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainViewModel

import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MyViewModelFactory

class UserAccountsActivity : BaseActivity(),UserAccountAdapter.OnItemClicked  {

    private lateinit var binding: ActivityLicenseExpiryNavigationBinding

    private var viewModel: MainViewModel? = null

    private lateinit var allAdapter: UserAccountAdapter
    // This property is only valid between onCreateView and
    // onDestroyView.

    private var listValuePopUp: Int = 1
    private val retrofitService = RetrofitService.getInstance()
    private lateinit var tempList: MutableList<User_Accountitem>
    var listValue: ArrayList<User_Accountitem>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license_expiry_navigation)
        binding = ActivityLicenseExpiryNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tempList = ArrayList()
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )
        binding.expiry.actionbarAccount.toolbarTitle.text = "Driver Account Details"
        binding.expiry.filterIcon.visibility=View.GONE
        searchview()
        viewModel!!.getdriveraccountdetails(0).observe(this, Observer {
            if (it != null) {
                binding!!.expiry.LoginAvi.hide()
                initializeRecyclerView(it)
                listValue=it
                tempList.clear()
                tempList.addAll(listValue!!)
            } else {
                binding!!.expiry.LoginAvi.hide()
                Log.e("data", "");
                binding.expiry.textViewNoRecordFound2.visibility = View.VISIBLE
                binding.expiry.recyclerView.visibility = View.GONE
                //Toast.makeText(this,"Invalid  credentials",Toast.LENGTH_LONG).show()
            }

        })
    }

    /*Exit Application alert functionality*/
    private fun showdialog() {
        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage("You are about to terminate the services of this driver. Do you wish to continue?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes) { dialog: DialogInterface?, which: Int ->
                val intent= Intent(this, UserAccountsActivity::class.java)
                startActivity(intent)
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no) { dialog: DialogInterface, which: Int ->
                // Continue with delete operation
                dialog.dismiss()
            }
            .show()
    }
    private fun initializeRecyclerView(driverdetails: java.util.ArrayList<User_Accountitem>) {

        if (driverdetails.size == 0) {
            binding.expiry.textViewNoRecordFound2.visibility = View.VISIBLE
            binding.expiry.recyclerView.visibility = View.GONE
        } else {
            binding.expiry.textViewNoRecordFound2.visibility = View.GONE
            binding.expiry.recyclerView.visibility = View.VISIBLE
            binding.expiry.recyclerView.isNestedScrollingEnabled = false
            binding.expiry.recyclerView.layoutManager = LinearLayoutManager(this)
            allAdapter = UserAccountAdapter(
                this,
                binding.expiry.textViewNoRecordFound2,
                driverdetails,
                this,
                1
            )
            binding.expiry.recyclerView.adapter = allAdapter
            allAdapter.notifyDataSetChanged()
        }
    }
    private fun searchview() {
        binding.expiry.searchEdittext.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                println("s" + s)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                println("s")
            }

            override fun afterTextChanged(s: Editable) {

                try {
                    if (allAdapter != null) {

                        if (s.toString().isEmpty()) {
                            // Utility.hideKeyboard(dataBinding.reglayout.searchText)

                            binding.expiry.searchEdittext.isClickable = true
                            allAdapter.filter.filter(s.toString())
                        } else {
                            binding.expiry.searchEdittext.isFocusable = true
                            allAdapter.filter.filter(s.toString())
                        }
                    }

                } catch (e: Exception) {
                    println("code below exception...")
                }
            }
        })
    }



    override fun onDriverdetails(driversItem: String) {
        call(driversItem)
    }
}