package com.wings.mile.ViewDrivers

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.wings.mile.DashboardViewActivity
import com.wings.mile.R
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.databinding.ActivityMain1Binding
import com.wings.mile.model.*
import com.wings.mile.preview.PopupImage
import com.wings.mile.preview.PopupImageView
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class DetailsActivity1 :Fragment() {

    private lateinit var dataBinding: ActivityMain1Binding
    lateinit var viewModel: MainViewModel

    private val retrofitService = RetrofitService.getInstance()
    lateinit var myContext: Context

    lateinit var vehicle1: vehicle
    lateinit var country: getCountry
    lateinit var state: getstate
    lateinit var district: getDistrictDetails

    lateinit var savedriver1: savedriver
    private lateinit var getdriverdetails: getdriver

    lateinit var vehicleItem: vehicleItem
    lateinit var stateItem: getstateItem
    lateinit var districtItem: getDistrictDetailsItem
    val DATE_FORMAT_PATTERN_4 = "dd/MM/yy"
    val DATE_FORMAT_PATTERN_14 = "MM/dd/yyyy"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = ActivityMain1Binding.inflate(inflater, container, false)

        setViewModel()
        initializeView()
        return dataBinding.root
    }

    private fun setViewModel() {
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )


    }

    private fun fetchDistrictDetails(statevalue: String) {
        viewModel.fetchDistrictDetails(statevalue, "2").observe(requireActivity(), Observer {
            if (it != null) {
                district = it
                updateDistrictSpinner(district)
            } else {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onAttach(context: Context) {
        myContext = context
        super.onAttach(context)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun initializeView() {
        dataBinding.lifecycleOwner = this
        try {
            (activity as DashboardViewActivity).icon()

        val bundle = this.arguments
//        val vehicleValue = viewModel.vehiclelist
//        val countryValue = viewModel.countrylist
//        val StateValue = viewModel.statelist




        val res = bundle?.get("LoginRes")
        val request = bundle?.get("SaveRequest")
        val getrequest = bundle?.get("getRequest")

        if (request != null) {
            savedriver1 = Gson().fromJson(request.toString(), savedriver::class.java)
        }

        if (getrequest != null) {

            getdriverdetails = Gson().fromJson(getrequest.toString(), getdriver::class.java)
        }

        val vehicleValue =Gson().toJson( (activity as DashboardViewActivity).getvehicle())
        val countryValue = Gson().toJson( (activity as DashboardViewActivity).getcountry())
        val StateValue = Gson().toJson( (activity as DashboardViewActivity).getstate())
        vehicle1 = Gson().fromJson(vehicleValue.toString(), vehicle::class.java)
        country = Gson().fromJson(countryValue.toString(), getCountry::class.java)
        state = Gson().fromJson(StateValue.toString(), getstate::class.java)
        dataBinding.imagenameaadhar.visibility = View.VISIBLE
        dataBinding.imagenamepancard.visibility = View.VISIBLE
        dataBinding.imagenameinsurance.visibility = View.VISIBLE
        dataBinding.imagenameplate.visibility = View.VISIBLE
        dataBinding.imagenamelicense.visibility = View.VISIBLE
            dataBinding.insuranceExpirytext.setText(getdriverdetails.vehicle_Insurance_Expiry_Date)
            dataBinding.licenseExpirydate.setText(getdriverdetails.driving_License_Expiry_Date)
        if(Pref_storage.getDetail(requireActivity(),"Vehicletext").toString().equals("")) {
            dataBinding.imagenameplate.text=getdriverdetails.plateno_file_name

        }else{
            dataBinding.imagenameplate.visibility=View.VISIBLE
            dataBinding.imagenameplate.text=Pref_storage.getDetail(requireActivity(),"Vehicletext")
        }
        if(Pref_storage.getDetail(requireActivity(),"Pantext").toString().equals("")) {
            dataBinding.imagenamepancard.text=getdriverdetails.panno_file_name

        }else{
            dataBinding.imagenamepancard.visibility=View.VISIBLE
            dataBinding.imagenamepancard.text=Pref_storage.getDetail(requireActivity(),"Pantext")
        }

        if(Pref_storage.getDetail(requireActivity(),"adhartext").toString().equals("")) {
            dataBinding.imagenameaadhar.text=getdriverdetails.aadhar_file_name

        }else{
            dataBinding.imagenameaadhar.visibility=View.VISIBLE
            dataBinding.imagenameaadhar.text=Pref_storage.getDetail(requireActivity(),"adhartext")

        }
        if(Pref_storage.getDetail(requireActivity(),"Insurancetext").toString().equals("")) {
            dataBinding.imagenameinsurance.text=getdriverdetails.insno_file_name

        }else{
            dataBinding.imagenameinsurance.visibility=View.VISIBLE

            dataBinding.imagenameinsurance.text=Pref_storage.getDetail(requireActivity(),"Insurancetext")
        }
        if(Pref_storage.getDetail(requireActivity(),"Licensetext").toString().equals("")) {
            dataBinding.imagenamelicense.text=getdriverdetails.licno_file_name


        }else{
            dataBinding.imagenamelicense.visibility=View.VISIBLE
            dataBinding.imagenamelicense.text=Pref_storage.getDetail(requireActivity(),"Licensetext")
        }
            if(Pref_storage.getDetail(requireActivity(),"Statename").toString().equals("")) {
                dataBinding.stateSpinner.setText(getdriverdetails.usr_state)


            }else{
                dataBinding.stateSpinner.setText(Pref_storage.getDetail(requireActivity(),"Statename"))

            }
            if(Pref_storage.getDetail(requireActivity(),"Vehiclename").toString().equals("")) {
                dataBinding.vehileSpinner.setText(getdriverdetails.vehicle_type_name)


            }else{
                dataBinding.vehileSpinner.setText(Pref_storage.getDetail(requireActivity(),"Vehiclename"))

            }
            if(Pref_storage.getDetail(requireActivity(),"Districtname").toString().equals("")) {
                dataBinding.citySpinner.setText(getdriverdetails.usr_district)


            }else{
                dataBinding.citySpinner.setText(Pref_storage.getDetail(requireActivity(),"Districtname"))

            }
            if(Pref_storage.getDetail(requireActivity(),"Vehiclenumber").toString().equals("")) {
                dataBinding.platenumber.setText(getdriverdetails.license_plate_no)


            }else{
                dataBinding.platenumber.setText(Pref_storage.getDetail(requireActivity(),"Vehiclenumber"))

            }
            if(Pref_storage.getDetail(requireActivity(),"adharnumber").toString().equals("")) {
                dataBinding.adharEdittext.setText(getdriverdetails.aadhar_no)


            }else{
                dataBinding.adharEdittext.setText(Pref_storage.getDetail(requireActivity(),"adharnumber"))

            }
            if(Pref_storage.getDetail(requireActivity(),"Pannumber").toString().equals("")) {
                dataBinding.panEdittext.setText(getdriverdetails.drv_pan_no)


            }else{
                dataBinding.panEdittext.setText(Pref_storage.getDetail(requireActivity(),"Pannumber"))

            }
            if(Pref_storage.getDetail(requireActivity(),"Licensenumber").toString().equals("")) {
                dataBinding.driverlicenseEdittext.setText(getdriverdetails.license_no)


            }else{
                dataBinding.driverlicenseEdittext.setText(Pref_storage.getDetail(requireActivity(),"Licensenumber"))

            }
            if(Pref_storage.getDetail(requireActivity(),"Insurancenumber").toString().equals("")) {
                dataBinding.insurancenumber.setText(getdriverdetails.insurance_no)


            }else{
                dataBinding.insurancenumber.setText(Pref_storage.getDetail(requireActivity(),"Insurancenumber"))

            }
            dataBinding.platenumber.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // this method does nothing
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // this method does nothing
                }

                override fun afterTextChanged(s: Editable?) {
                    Pref_storage.setDetail(requireActivity(),"Vehiclenumber",s.toString())
                }

            })
            dataBinding.adharEdittext.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // this method does nothing
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // this method does nothing
                }

                override fun afterTextChanged(s: Editable?) {
                    Pref_storage.setDetail(requireActivity(),"adharnumber",s.toString())
                }

            })
            dataBinding.panEdittext.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // this method does nothing
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // this method does nothing
                }

                override fun afterTextChanged(s: Editable?) {
                    Pref_storage.setDetail(requireActivity(),"Pannumber",s.toString())
                }

            })
            dataBinding.driverlicenseEdittext.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // this method does nothing
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // this method does nothing
                }

                override fun afterTextChanged(s: Editable?) {
                    Pref_storage.setDetail(requireActivity(),"Licensenumber",s.toString())
                }

            })
            dataBinding.insurancenumber.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // this method does nothing
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // this method does nothing
                }

                override fun afterTextChanged(s: Editable?) {
                    Pref_storage.setDetail(requireActivity(),"Insurancenumber",s.toString())
                }

            })

        }catch (e:Exception){
            e.printStackTrace()
        }
        updateVehicleSpinner()
        updateStateSpinner()
        fetchDistrictDetails("6")
        dataBinding.next.setOnClickListener {
            val vechicledata = (activity as DashboardViewActivity).getvehicles()
            val licensedata = (activity as DashboardViewActivity).getlicense()
            val insurancedata = (activity as DashboardViewActivity).getinsurancebase64()
            val aadhardata = (activity as DashboardViewActivity).getaadharbase64()
            val pancarddata = (activity as DashboardViewActivity).getpanbase64()
            val result = validation(
                dataBinding.platenumber.text.toString(),
                dataBinding.insurancenumber.text.toString(),
                dataBinding.driverlicenseEdittext.text.toString(),
                dataBinding.adharEdittext.text.toString(),
                dataBinding.panEdittext.text.toString(),
                dataBinding.vehileSpinner.text.toString(),
                dataBinding.stateSpinner.text.toString(),
                dataBinding.citySpinner.text.toString(),
            )

            if (result == null) {
                savedriver1.vehicle_type_id = getdriverdetails.vehicle_type_id!!.toInt()
                savedriver1.drv_license_no = dataBinding.driverlicenseEdittext.text.toString()
                savedriver1.state = dataBinding.stateSpinner.text.toString()
                savedriver1.district = dataBinding.citySpinner.text.toString()
                savedriver1.district_id=getdriverdetails.district_id
                savedriver1.aadhar_no = dataBinding.adharEdittext.text.toString()

                savedriver1.id_Proof_Name = getdriverdetails.id_proof_name
                savedriver1.en_flag = getdriverdetails.en_flag
                savedriver1.license_plate_no=dataBinding.platenumber.text.toString()
                savedriver1.drv_insurance_no=dataBinding.insurancenumber.text.toString()
                savedriver1.vehicle_Insurance_Expiry_Date=getdate(dataBinding.insuranceExpirytext.text.toString())
                savedriver1.driving_License_Expiry_Date=getdate(dataBinding.licenseExpirydate.text.toString())
                savedriver1.drv_aadhar_no=dataBinding.adharEdittext.text.toString()
                savedriver1.drv_pan_no=dataBinding.panEdittext.text.toString()
                savedriver1.doc_file_location=getdriverdetails.doc_file_location
                savedriver1.doc_file_name=getdriverdetails.doc_file_name
                if(getdriverdetails.plateno_file_location!!.isNotEmpty() && vechicledata!!.isEmpty()){
                    savedriver1.plateno_file_location=getdriverdetails.plateno_file_location
                    savedriver1.plateno_file_name=getdriverdetails.plateno_file_name
                }else {
                    savedriver1.plateNo_data = vechicledata
                    savedriver1.plateno_file_name=""
                    savedriver1.plateno_file_location=""
                }
                if(getdriverdetails.panno_file_location!!.isNotEmpty() && pancarddata!!.isEmpty()){
                    savedriver1.panno_file_location=getdriverdetails.panno_file_location
                    savedriver1.panno_file_name=getdriverdetails.panno_file_name
                }else {
                    savedriver1.pan_data=pancarddata
                    savedriver1.panno_file_location=""
                    savedriver1.panno_file_name=""
                }
                if(getdriverdetails.aadhar_file_location!!.isNotEmpty() && aadhardata!!.isEmpty()){
                    savedriver1.aadhar_file_location=getdriverdetails.aadhar_file_location
                    savedriver1.aadhar_file_name=getdriverdetails.aadhar_file_name
                }else {
                    savedriver1.aadhar_data=aadhardata
                    savedriver1.aadhar_file_location=""
                    savedriver1.aadhar_file_name=""
                }
                if(getdriverdetails.licno_file_location!!.isNotEmpty() && licensedata!!.isEmpty()){
                    savedriver1.licno_file_location=getdriverdetails.licno_file_location
                    savedriver1.licno_file_name=getdriverdetails.licno_file_name
                }else {
                    savedriver1.license_data=licensedata
                    savedriver1.licno_file_location=""
                    savedriver1.licno_file_name=""
                }
                if(getdriverdetails.insno_file_location!!.isNotEmpty() && insurancedata!!.isEmpty()){
                    savedriver1.insno_file_location=getdriverdetails.insno_file_location
                    savedriver1.insno_file_name=getdriverdetails.insno_file_name
                }else {
                    savedriver1.insurance_data=insurancedata
                    savedriver1.licno_file_location=""
                    savedriver1.licno_file_name=""
                }

//                val bundle = Bundle()
////                bundle.putString("DriverDetails", Gson().toJson(savedriver1))
////                bundle.putString("LoginRes", res.toString())
//                findNavController().navigate(R.id.action_SixthFragment_to_SeventhFragment,bundle)
                (activity as DashboardViewActivity).attachFragments()
                Pref_storage.setDetail(requireActivity(),"Driverdetails",Gson().toJson(savedriver1))

            }

        }
        dataBinding.licenseExpirydate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(
                requireActivity(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    var conversion: String = ""
                    if (dayOfMonth.toInt() <= 9)
                        conversion = "0" + dayOfMonth
                    else
                        conversion = "" + dayOfMonth
                    dataBinding.licenseExpirydate.setText(conversion + "/" + (monthOfYear + 1) + "/" + year)


                },
                year,
                month,
                day
            )
            dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            dpd.show()
            val positiveColor = ContextCompat.getColor(
                requireActivity(),
                com.wings.mile.R.color.teal_200
            )
            val negativeColor = ContextCompat.getColor(
                requireActivity(),
                com.wings.mile.R.color.teal_200
            )
            dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
            dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)
        }
        dataBinding.insuranceExpirytext.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(
                requireActivity(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    var conversion: String = ""
                    if (dayOfMonth.toInt() <= 9)
                        conversion = "0" + dayOfMonth
                    else
                        conversion = "" + dayOfMonth
                    dataBinding.insuranceExpirytext.setText(conversion + "/" + (monthOfYear + 1) + "/" + year)


                },
                year,
                month,
                day
            )
            dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dpd.show()
            val positiveColor = ContextCompat.getColor(
                requireActivity(),
                com.wings.mile.R.color.teal_200
            )
            val negativeColor = ContextCompat.getColor(
                requireActivity(),
                com.wings.mile.R.color.teal_200
            )
            dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
            dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)

        }


        dataBinding.imgAadharcard.setOnClickListener {
            (activity as DashboardViewActivity).pickPhotoImages(dataBinding.imgAadharcard,dataBinding.imagenameaadhar,1)

        }
        dataBinding.imgDriverLicense.setOnClickListener {
            (activity as DashboardViewActivity).pickPhotoImages(dataBinding.imgDriverLicense,dataBinding.imagenamelicense,2)

        }
        dataBinding.imgInsurance.setOnClickListener {
            (activity as DashboardViewActivity).pickPhotoImages(dataBinding.imgInsurance,dataBinding.imagenameinsurance,3)

        }
        dataBinding.imgPancard.setOnClickListener {
            (activity as DashboardViewActivity).pickPhotoImages(dataBinding.imgPancard,dataBinding.imagenamepancard,4)

        }
        dataBinding.imgLicensePlate.setOnClickListener {
            (activity as DashboardViewActivity).pickPhotoImages(dataBinding.imgLicensePlate,dataBinding.imagenameplate,5)

        }

        dataBinding.imagenameplate.setOnClickListener {
            if (dataBinding.imagenameplate.text.toString().isNotEmpty()) {

                val decodedString: ByteArray = Base64.decode((activity as DashboardViewActivity).getvehicles(), Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                if(decodedByte==null){
                    PopupImage(requireActivity(),getdriverdetails.plateno_file_location+getdriverdetails.plateno_file_name).showFullImageView()

                }else{


                    PopupImageView(requireActivity(),decodedByte,getdriverdetails.plateno_file_location+getdriverdetails.plateno_file_name).showFullImageView()            }
                }
        }
        dataBinding.imagenamelicense.setOnClickListener {
            if (dataBinding.imagenamelicense.text.toString().isNotEmpty()) {

                val decodedString: ByteArray = Base64.decode(
                    (activity as DashboardViewActivity).getlicense(),
                    Base64.DEFAULT
                )
                val decodedByte =
                    BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                if (decodedByte == null) {
                    PopupImage(
                        requireActivity(),
                        getdriverdetails.licno_file_location + getdriverdetails.licno_file_name
                    ).showFullImageView()

                } else {

                    PopupImageView(
                        requireActivity(),
                        decodedByte,
                        getdriverdetails.licno_file_location + getdriverdetails.licno_file_name
                    ).showFullImageView()

                }
            }
        }
        dataBinding.imagenameinsurance.setOnClickListener {
            if (dataBinding.imagenameinsurance.text.toString().isNotEmpty()) {
                val decodedString: ByteArray = Base64.decode((activity as DashboardViewActivity).getinsurancebase64(), Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                if(decodedByte==null){
                    PopupImage(requireActivity(),getdriverdetails.insno_file_location+getdriverdetails.insno_file_name).showFullImageView()

                }else{

                    PopupImageView(requireActivity(),decodedByte,getdriverdetails.insno_file_location+getdriverdetails.insno_file_name).showFullImageView()
            }
            }
        }


        dataBinding.imagenameaadhar.setOnClickListener {
            if (dataBinding.imagenameaadhar.text.toString().isNotEmpty()) {
                val decodedString: ByteArray = Base64.decode(
                    (activity as DashboardViewActivity).getaadharbase64(),
                    Base64.DEFAULT
                )
                val decodedByte =
                    BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                if (decodedByte == null) {
                    PopupImage(
                        requireActivity(),
                        getdriverdetails.aadhar_file_location + getdriverdetails.aadhar_file_name
                    ).showFullImageView()

                } else {

                    PopupImageView(
                        requireActivity(),
                        decodedByte,
                        getdriverdetails.aadhar_file_location + getdriverdetails.aadhar_file_name
                    ).showFullImageView()
                }
            }
        }

        dataBinding.imagenamepancard.setOnClickListener {
            if (dataBinding.imagenamepancard.text.toString().isNotEmpty()) {
                val decodedString: ByteArray = Base64.decode(
                    (activity as DashboardViewActivity).getpanbase64(),
                    Base64.DEFAULT
                )
                val decodedByte =
                    BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                if (decodedByte == null) {
                    PopupImage(
                        requireActivity(),
                        getdriverdetails.panno_file_location + getdriverdetails.panno_file_name
                    ).showFullImageView()

                } else {

                    PopupImageView(
                        requireActivity(),
                        decodedByte,
                        getdriverdetails.panno_file_location + getdriverdetails.panno_file_name
                    ).showFullImageView()
                }
            }
        }

    }


    fun updateVehicleSpinner() {
        val adapter = ArrayAdapter(requireContext(), R.layout.itempopup, R.id.item, vehicle1)
        dataBinding.vehileSpinner.setAdapter(adapter)

        dataBinding.vehileSpinner.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                vehicleItem = vehicle1.get(position)
                Pref_storage.setDetail(requireActivity(),"Vehiclename",vehicleItem.vehicle_type_name)
                Pref_storage.setDetail(requireActivity(),"Vehicleid",vehicleItem.vehicle_type_id.toString())
            }
    }

    fun updateStateSpinner() {
        val adapter = ArrayAdapter(requireContext(), R.layout.itempopup, R.id.item, state)
        dataBinding.stateSpinner.setAdapter(adapter)

        dataBinding.stateSpinner.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                stateItem = state.get(position)
                dataBinding.citySpinner.setText("")
                Pref_storage.setDetail(requireActivity(),"Statename",stateItem.state_name)
                fetchDistrictDetails(stateItem.state_id.toString())
               // if (stateItem.state_id == 6)

            }
    }


    fun updateDistrictSpinner(districts: getDistrictDetails) {
        val adapter = ArrayAdapter(requireContext(), R.layout.itempopup, R.id.item, districts)
        dataBinding.citySpinner.setAdapter(adapter)

        dataBinding.citySpinner.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                districtItem = this.district.get(position)
                Pref_storage.setDetail(requireActivity(),"Districtname",districtItem.district_name)
                Pref_storage.setDetail(requireActivity(),"Districtid",districtItem.district_id.toString())
            }
    }

    fun validation(plate: String,insurance: String,license: String, adhar: String,pan: String,
                   type:String,state:String,city:String): String? {
        if (plate.isNullOrEmpty()) {
            dataBinding.platenumber.setError("Vehicle number should not be empty")

            return "Vehicle number should not be empty"
        } else if (insurance.isNullOrEmpty()) {
            dataBinding.insurancenumber.setError("Insurance number should not be empty")

            return "Insurance number should not be empty"
        } else if (license.isNullOrEmpty()) {
            dataBinding.driverlicenseEdittext.setError("license number should not be empty")

            return "license number should not be empty"
        } else if (adhar.isNullOrEmpty()) {
            dataBinding.adharEdittext.setError("Adhar number should not be empty")

            return "Adhar number should not be empty"
        }  else if (pan.isNullOrEmpty()) {
            dataBinding.panEdittext.setError("Pan number should not be empty")

            return "Pan number should not be empty"
        } else if (type.isNullOrEmpty()) {
            dataBinding.vehileSpinner.setError("Vehicle Type should not be empty")

            return "Vehicle Type should not be empty"
        } else if (state.isNullOrEmpty()) {
            dataBinding.stateSpinner.setError("State should not be empty")

            return "State should not be empty"
        } else if (city.isNullOrEmpty()) {
            dataBinding.citySpinner.setError("City should not be empty")

            return "City should not be empty"
        }else if ( dataBinding.platenumber.text!!.length < 10) {
            dataBinding.platenumber.setError("Vehicle number not valid must contains 10 Number")
            return "Vehicle number not valid must contains 10 Number"
        } else if ( dataBinding.insurancenumber.text!!.length < 20) {
            dataBinding.insurancenumber.setError("Insurance number not valid must contains 20 Number")
            return "Insurance number not valid must contains 20 Number"
        } else if ( dataBinding.driverlicenseEdittext.text!!.length < 16) {
            dataBinding.driverlicenseEdittext.setError("License number not valid must contains 16 Number")
            return "License number not valid must contains 16 Number"
        } else if ( dataBinding.adharEdittext.text!!.length < 12) {
            dataBinding.adharEdittext.setError("Adhar number not valid must contains 12 Number")
            return "Phone number not valid must contains 10 Number"
        }else if ( dataBinding.panEdittext.text!!.length < 10) {
            dataBinding.panEdittext.setError("Pan number not valid must contains 10 Number")
            return "Pan number not valid must contains 10 Number"
        } else {
            return null
        }

    }


    private fun getdate(date: String): String? {
        var date1: Date? = null
        try {
            date1 = SimpleDateFormat(DATE_FORMAT_PATTERN_4).parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (date1 != null) {
            val df = SimpleDateFormat(
                DATE_FORMAT_PATTERN_14,
                Locale.getDefault()
            )
            return df.format(date1)
        }
        return ""
    }
}

