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
import com.wings.mile.databinding.FragmentGalleryBinding
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.model.license_expiryItem
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MyViewModelFactory


class AboutusActivity : BaseActivity() {

    private lateinit var binding: FragmentGalleryBinding

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
        setContentView(R.layout.fragment_gallery)
        binding = FragmentGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tempList = ArrayList()
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )
      //  binding.expiry.actionbarAccount.toolbarTitle.text = "About us"


    }
}