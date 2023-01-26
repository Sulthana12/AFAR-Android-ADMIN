package com.wings.mile.ui.home


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wings.mile.DashboardActivity
import com.wings.mile.DashboardViewActivity
import com.wings.mile.R
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.adapter.AlldriversAdapter
import com.wings.mile.databinding.FragmentFirstBinding
import com.wings.mile.model.getdriver
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory


class HomeFragment : Fragment(), AlldriversAdapter.OnItemClicked {
    private var viewModel: MainViewModel? = null
    private var _binding: FragmentFirstBinding? = null
    private lateinit var allAdapter: AlldriversAdapter
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var listValuePopUp: Int = 1
    private val retrofitService = RetrofitService.getInstance()
    private lateinit var tempList: MutableList<getdriver>
    var listValue: ArrayList<getdriver>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setViewModel()
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.LoginAvi.show()
        Pref_storage.setDetail(requireActivity(),"Pantext","")
        Pref_storage.setDetail(requireActivity(),"Vehicletext","")
        Pref_storage.setDetail(requireActivity(),"Insurancetext","")
        Pref_storage.setDetail(requireActivity(),"adhartext","")
        Pref_storage.setDetail(requireActivity(),"Licensetext","")
        Pref_storage.setDetail(requireActivity(),"Licensenumber","")
        Pref_storage.setDetail(requireActivity(),"adharnumber","")
        Pref_storage.setDetail(requireActivity(),"Insurancenumber","")
        Pref_storage.setDetail(requireActivity(),"Vehiclenumber","")
        Pref_storage.setDetail(requireActivity(),"Pannumber","")
        Pref_storage.setDetail(requireActivity(),"Vehiclename","")
        Pref_storage.setDetail(requireActivity(),"Statename","")
        Pref_storage.setDetail(requireActivity(),"Districtname","")
        Pref_storage.setDetail(requireActivity(),"driverpdf","")
        Pref_storage.setDetail(requireActivity(),"idproof","")
        Pref_storage.setDetail(requireActivity(),"pdfbase64","")
       // Log.e("data", ""+(activity as DashboardActivity).getBase64());
        tempList = ArrayList()
        binding.adddriver.setOnClickListener {
            //(activity as DashboardActivity).datapass()
            val intent = Intent(activity, DashboardActivity::class.java)

            startActivity(intent)
           // findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment,(activity as NavigationActivity).datapass())
        }
        binding.filterIcon.setOnClickListener(object : View.OnClickListener {
            var option: String? = null
            override fun onClick(view: View) {
                onShowPopupWindow(binding.filterIcon)
                binding.searchEdittext.setText("")
            }
        })
        searchview()
        viewModel!!.getdriverdetails("").observe(requireActivity(), Observer {
            if (it != null) {
                binding!!.LoginAvi.hide()
                initializeRecyclerView(it)
                listValue=it
                tempList.clear()
                tempList.addAll(listValue!!)
            } else {
                binding!!.LoginAvi.hide()
                Log.e("data", "");
                binding.textViewNoRecordFound2.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                //Toast.makeText(this,"Invalid  credentials",Toast.LENGTH_LONG).show()
            }

        })
        try {
           // (activity as DashboardActivity).icons()
            //(activity as DashboardActivity).logout()

        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setViewModel() {
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )
    }
    private fun initializeRecyclerView(driverdetails: java.util.ArrayList<getdriver>) {

        if (driverdetails.size == 0) {
            binding.textViewNoRecordFound2.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.textViewNoRecordFound2.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.recyclerView.isNestedScrollingEnabled = false
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            allAdapter = AlldriversAdapter(
                context!!,
                binding.textViewNoRecordFound2,
                driverdetails,
                this,
                1
            )
            binding.recyclerView.adapter = allAdapter
            allAdapter.notifyDataSetChanged()
        }
    }
    private fun searchview() {
        binding.searchEdittext.addTextChangedListener(object : TextWatcher {
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

                            binding.searchEdittext.isClickable = true
                            allAdapter.filter.filter(s.toString())
                        } else {
                            binding.searchEdittext.isFocusable = true
                            allAdapter.filter.filter(s.toString())
                        }
                    }

                } catch (e: Exception) {
                    println("code below exception...")
                }
            }
        })
    }

    override fun onDriverdetailsclick(driversItem: getdriver) {
//        val bundle = Bundle()
//        bundle.putString("getdetails", Gson().toJson(driversItem))
//        findNavController().navigate(R.id.action_FirstFragment_to_FifthFragment,bundle)
        val intent = Intent(activity, DashboardViewActivity::class.java)
        intent.putExtra("getdetails",  Gson().toJson(driversItem))
        startActivity(intent)
    }
    private fun onShowPopupWindow(view: View?) {

        // inflate the layout of the popup window
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.filter_popup_window, null)
        val textAll: CheckBox = popupView.findViewById(R.id.txt_view_all) as CheckBox
        val textApproved: CheckBox = popupView.findViewById(R.id.textview_pending) as CheckBox
        val approved: CheckBox = popupView.findViewById(R.id.txt_approval) as CheckBox
        val reject: CheckBox = popupView.findViewById(R.id.txt_reject) as CheckBox

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
                reject.isChecked = false
            }
            2 -> {
                textAll.isChecked = false
                textApproved.isChecked = true
                approved.isChecked = false
                reject.isChecked = false
            }
            3 -> {
                textAll.isChecked = false
                textApproved.isChecked = false
                approved.isChecked = true
                reject.isChecked = false
            }
            else -> {
                textAll.isChecked = false
                textApproved.isChecked = false
                approved.isChecked = false
                reject.isChecked = true
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
            updateRecycleViewStatus("P")
            popupWindow.dismiss()
        }

        approved.setOnClickListener {
            listValuePopUp = 3
            updateRecycleViewStatus("A")
            popupWindow.dismiss()
        }
        reject.setOnClickListener {
            listValuePopUp = 3
            updateRecycleViewStatus("R")
            popupWindow.dismiss()
        }
    }

    private fun updateRecycleViewStatus(searchText: String) {
        listValue!!.clear()
        if (searchText.isEmpty()) {
            listValue!!.addAll(tempList)
        } else {
            for (ij in 0 until tempList.size) {
                val submittedDetails = tempList[ij]
                if (submittedDetails.en_flag == searchText) {
                    listValue!!.add(submittedDetails)
                }
            }
            allAdapter!!.notifyDataSetChanged()
        }
        initializeRecyclerView(listValue!!)
        allAdapter!!.notifyDataSetChanged()
    }
}