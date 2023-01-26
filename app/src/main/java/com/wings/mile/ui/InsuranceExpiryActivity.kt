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
import com.wings.mile.databinding.ActivityLicenseExpiryNavigationBinding
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.model.license_expiryItem
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MyViewModelFactory


class InsuranceExpiryActivity : BaseActivity(),LicenseExpiryAdapter.OnItemClicked {

    private lateinit var binding: ActivityLicenseExpiryNavigationBinding

    private var viewModel: MainViewModel? = null

    private lateinit var allAdapter: LicenseExpiryAdapter
    // This property is only valid between onCreateView and
    // onDestroyView.

    private var listValuePopUp: Int = 1
    private val retrofitService = RetrofitService.getInstance()
    private lateinit var tempList: MutableList<license_expiryItem>
    var listValue: ArrayList<license_expiryItem>? = null
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
        binding.expiry.actionbarAccount.toolbarTitle.text = "Insurance Expiry Details"
        binding.expiry.filterIcon.setOnClickListener(object : View.OnClickListener {
            var option: String? = null
            override fun onClick(view: View) {
                onShowPopupWindow(binding.expiry.filterIcon)
                binding.expiry.searchEdittext.setText("")
            }
        })
        searchview()
        viewModel!!.getdriverinsuranceexpirydetails(0).observe(this, Observer {
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
                val intent= Intent(this, InsuranceExpiryActivity::class.java)
                startActivity(intent)
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no) { dialog: DialogInterface, which: Int ->
                // Continue with delete operation
                dialog.dismiss()
            }
            .show()
    }
    private fun initializeRecyclerView(driverdetails: java.util.ArrayList<license_expiryItem>) {

        if (driverdetails.size == 0) {
            binding.expiry.textViewNoRecordFound2.visibility = View.VISIBLE
            binding.expiry.recyclerView.visibility = View.GONE
        } else {
            binding.expiry.textViewNoRecordFound2.visibility = View.GONE
            binding.expiry.recyclerView.visibility = View.VISIBLE
            binding.expiry.recyclerView.isNestedScrollingEnabled = false
            binding.expiry.recyclerView.layoutManager = LinearLayoutManager(this)
            allAdapter = LicenseExpiryAdapter(
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

    private fun onShowPopupWindow(view: View?) {

        // inflate the layout of the popup window
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.filter_popup_window, null)
        val textAll: CheckBox = popupView.findViewById(R.id.txt_view_all) as CheckBox
        val textApproved: CheckBox = popupView.findViewById(R.id.textview_pending) as CheckBox
        val approved: CheckBox = popupView.findViewById(R.id.txt_approval) as CheckBox
        val reject: CheckBox = popupView.findViewById(R.id.txt_reject) as CheckBox
        reject.visibility=View.GONE
        textApproved.text="Expiration-In Progress"
        approved.text="Expired"

        // create the popup window
        val width: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val height: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)
        when (listValuePopUp) {
            1 -> {
                textAll.isChecked = true
                textApproved.isChecked = false
                approved.isChecked = false

            }
            2 -> {
                textAll.isChecked = false
                textApproved.isChecked = true
                approved.isChecked = false

            }
            3 -> {
                textAll.isChecked = false
                textApproved.isChecked = false
                approved.isChecked = true

            }

        }

        popupWindow.showAsDropDown(view, 30, 30, Gravity.LEFT)
        // dismiss the popup window when touched
        popupView.setOnTouchListener { _, _ ->
            popupWindow.dismiss()
            true
        }

        textAll.setOnClickListener {
            listValuePopUp = 1
            updateRecycleViewStatus("")
            popupWindow.dismiss()
        }

        textApproved.setOnClickListener {
            listValuePopUp = 2
            updateRecycleViewStatus("G")
            popupWindow.dismiss()
        }

        approved.setOnClickListener {
            listValuePopUp = 3
            updateRecycleViewStatus("X")
            popupWindow.dismiss()
        }
//        reject.setOnClickListener {
//            listValuePopUp = 3
//            updateRecycleViewStatus("R")
//            popupWindow.dismiss()
//        }
    }

    private fun updateRecycleViewStatus(searchText: String) {
        listValue!!.clear()
        if (searchText.isEmpty()) {
            listValue!!.addAll(tempList)
        } else {
            for (ij in 0 until tempList.size) {
                val submittedDetails = tempList[ij]
                if (submittedDetails.flag == searchText) {
                    listValue!!.add(submittedDetails)
                }
            }
            allAdapter!!.notifyDataSetChanged()
        }
        initializeRecyclerView(listValue!!)
        allAdapter!!.notifyDataSetChanged()
    }

    override fun onDriverdetails(driversItem: String) {
        call(driversItem)
    }
}