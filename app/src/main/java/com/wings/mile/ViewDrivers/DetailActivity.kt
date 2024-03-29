package com.wings.mile.ViewDrivers

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.wings.mile.R

import com.wings.mile.databinding.ActivityMainBinding
import com.wings.mile.databinding.DetaillayoutBinding
import com.wings.mile.model.*
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory
import com.google.gson.Gson
import com.wings.mile.DashboardActivity
import com.wings.mile.NavigationActivity
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.Utils.Pref_storage
import java.io.ByteArrayOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*
import java.util.jar.Manifest
import java.util.regex.Pattern


class DetailActivity : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var dataBinding: ActivityMainBinding
    lateinit var viewModel: MainViewModel

    lateinit var genderList: gender
    lateinit var genderItem: genderItem
    lateinit var savedriver: getdriver
    lateinit var savedriver1: savedriver
    lateinit var loginResponse: loginResponseItem
    private val blockCharacterSet = "₹@.~#^:;?'|$%&*!/_,()-+0123456789"
    val DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val DATE_FORMAT_PATTERN_1 = "yyyy-MM-dd'T'HH:mm:ss" //2020-11-27T12:18:41
    val DATE_FORMAT_PATTERN_2 = "dd/MM/yyyy hh:mm a"
    val DATE_FORMAT_PATTERN_3 = "yyyy-MM-dd'T'HH:mm:ss.SSS" //2021-01-04T19:23:00.297
    val DATE_FORMAT_PATTERN_4 = "dd/MM/yy"
    val DATE_FORMAT_PATTERN_14 = "MM/dd/yyyy"

    val REGEX_PATTERN_1 = "\\d{2}-\\d{2}-\\d{4}"
    lateinit var pickedDate: LocalDate
    var eighteenYearsAgo = LocalDate.now() - Period.ofYears(18)
    private val REQUIRED_PERMISSIONS = arrayOf(
        "android.permission.CAMERA",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE"
    )
    private val retrofitService = RetrofitService.getInstance()
    lateinit var myContext: Context

    var imagedata:String?=""
    var imagelocation:String?=""
    var imagelocationname:String?=""
    private val filters =
        InputFilter { source, start, end, dest, dstart, dend ->
            if (source != null && blockCharacterSet.contains("" + source)) {
                ""
            } else null
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = ActivityMainBinding.inflate(inflater, container, false)
        initializeView()
        setViewModel()
        return dataBinding.root
    }

    private fun setViewModel() {
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )

       // updateGenderSpinner()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedImageUri = data!!.data

        val photo = data.extras!!["data"] as Bitmap?

        val cameraPhoto = getResizedBitmap(photo, 140)
        dataBinding.photoEdittexts.setImageBitmap(cameraPhoto)

    }

    override fun onAttach(context: Context) {
        myContext = context
        super.onAttach(context)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeView() {
        dataBinding.lifecycleOwner = this

        dataBinding.firstnameEdittext.filters=arrayOf<InputFilter>(filters )
        dataBinding.lastnameEdittext.filters=arrayOf<InputFilter>(filters )
        val bundle = this.arguments
        val genderValue =  (activity as NavigationActivity).getgender()
//        val vehicleValue = bundle?.get("Vehicle")
//        val countryValue = bundle?.get("Country")
//        val StateValue = bundle?.get("State")
       // genderList = Gson().fromJson(genderValue.toString(), gender::class.java)
        val request = bundle?.get("getdetails")
        savedriver = Gson().fromJson(request.toString(), getdriver::class.java)
        dataBinding.firstnameEdittext.setText(savedriver.first_Name)
        dataBinding.lastnameEdittext.setText(savedriver.last_Name)
        dataBinding.genderSpinner.setText(savedriver.gender)
        dataBinding.emailEdittext.setText(savedriver.email_id)
        dataBinding.addressEdittext.setText(savedriver.address)
        dataBinding.mobilenumberEdittext.setText(savedriver.phone_num)
        dataBinding.pincodeEdittext.setText(savedriver.pincode)
        dataBinding.birthLabelEdittext.setText(savedriver.date_of_birth!!)
        dataBinding.birthLabelEdittext.setInputType(InputType.TYPE_NULL)
        dataBinding.calllayout.visibility=View.VISIBLE

        try {
           // (activity as DashboardActivity).iconfeedback()
            val genderValue = Gson().toJson( (activity as NavigationActivity).getgender())
            genderList = Gson().fromJson(genderValue.toString(), gender::class.java)
            updateGenderSpinner()
        }catch (e:Exception){
            e.printStackTrace()
        }
        if(savedriver.usr_img_file_location!!.isNotEmpty() && (activity as DashboardActivity).getBase64()==null){
            Glide.with(dataBinding.photoEdittexts).load(savedriver.usr_img_file_location+savedriver.usr_img_file_name)
                .into(dataBinding.photoEdittexts)
        }else{
            if((activity as NavigationActivity).getBase64().equals("")){
                Glide.with(dataBinding.photoEdittexts).load(savedriver.usr_img_file_location+savedriver.usr_img_file_name)
                    .into(dataBinding.photoEdittexts)
            }else{
                val decodedString: ByteArray = Base64.decode(((activity as NavigationActivity).getBase64()), Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                dataBinding.photoEdittexts.setImageBitmap(decodedByte)
            }
        }
        dataBinding.call.setOnClickListener {
            try {
                (activity as BaseActivity).call(dataBinding.mobilenumberEdittext.text.toString())

            } catch (e: Exception) {
                e.printStackTrace()

            }
        }


    dataBinding.next.setOnClickListener {

        val result = validation(
            dataBinding.firstnameEdittext.text.toString(),
            dataBinding.lastnameEdittext.text.toString(),
            dataBinding.mobilenumberEdittext.text.toString(),
            dataBinding.addressEdittext.text.toString(),
            dataBinding.emailEdittext.text.toString(),
            dataBinding.birthLabelEdittext.text.toString(),
            dataBinding.genderSpinner.text.toString()
        )


            if (result.isNullOrEmpty()) {

                imagedata= (activity as NavigationActivity).getBase64()
                if(imagedata!!.isEmpty()){

                    imagelocation=savedriver.usr_img_file_location
                    imagelocationname=savedriver.usr_img_file_name
                }else {
                    imagelocation=""
                    imagelocationname=""
                }

                savedriver1 = savedriver(
                    first_name = dataBinding.firstnameEdittext.text.toString(),
                    last_name = dataBinding.lastnameEdittext.text.toString(),
                    gender = dataBinding.genderSpinner.text.toString(),
                    phone_number = dataBinding.mobilenumberEdittext.text.toString(),
                    user_address = dataBinding.addressEdittext.text.toString(),
                    email_id = dataBinding.emailEdittext.text.toString(),
                    date_of_birth = getdate(dataBinding.birthLabelEdittext.text.toString()),
                    image_data = imagedata,
                    usr_img_file_location = imagelocation,
                    usr_img_file_name = imagelocationname,
                    user_password = "Taxi@123",
                    user_id = savedriver.user_id,
                    user_type_flg = "D",
                    aadhar_no = "",
                    country = "India",
                    district = savedriver.usr_district,
                    drv_license_no = "",
                    en_flag = "",
                    pincode = dataBinding.pincodeEdittext.text.toString(),
                    state = savedriver.usr_state,
                    vehicle_type_id = savedriver.vehicle_type_id,
                    district_id = savedriver.district_id,
                    doc_file_location = "",
                    doc_file_name = "",
                    doc_data = "",
                    drv_aadhar_no = "",
                    drv_insurance_no = "",
                    license_plate_no = "",
                    aadhar_data = "",
                    aadhar_file_location = "",
                    aadhar_file_name = "",
                    panno_file_location = "",
                    panno_file_name = "",
                    pan_data = "",
                    drv_pan_no = "",
                    license_data = "",
                    licno_file_location = "",
                    licno_file_name = "",
                    insno_file_location = "",
                    insno_file_name = "",
                    insurance_data = "",
                    plateNo_data = "",
                    plateno_file_location = "",
                    plateno_file_name = "",
                    vehicle_name=savedriver.vehicle_type_name,
                    new_userid = savedriver.user_id,
                    id_Proof_Name =savedriver.id_proof_name,
                    comments = savedriver.comments,
                    driving_License_Expiry_Date = savedriver.driving_License_Expiry_Date,
                    vehicle_Insurance_Expiry_Date = savedriver.vehicle_Insurance_Expiry_Date

                )

                val bundle = Bundle()
//                bundle.putString("Vehicle", vehicleValue.toString())
//                bundle.putString("Country", countryValue.toString())
//                bundle.putString("State", StateValue.toString())
//                bundle.putString("State", StateValue.toString())
                bundle.putString("SaveRequest", Gson().toJson(savedriver1))
                bundle.putString("getRequest", Gson().toJson(savedriver))
                findNavController().navigate(R.id.action_FifthFragment_to_SixthFragment,bundle)


            }


        }


        dataBinding.birthLabelEdittext.setOnClickListener {
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
                    pickedDate = LocalDate.of(year, month + 1, day)
                    if (pickedDate >= eighteenYearsAgo) {
                        // Picked a date less than 18 years ago
                        dataBinding.birthLabelEdittext.setError("Minimum age must be 18 years. Please re-check")
                    } else {
                        dataBinding.birthLabelEdittext.setError(null)

                        dataBinding.birthLabelEdittext.setText(conversion + "/" + (monthOfYear + 1) + "/" + year)
                    }

                },
                year,
                month,
                day
            )

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


        dataBinding.photoEdittexts.setOnClickListener {
            (activity as NavigationActivity).pickPhotoImage(dataBinding.photoEdittexts)

        }


    }


    fun updateGenderSpinner() {
        var adapter = ArrayAdapter(requireContext(), R.layout.itempopup, R.id.item, genderList)
        dataBinding.genderSpinner.setAdapter(adapter)

        dataBinding.genderSpinner.onItemClickListener =
            object : AdapterView.OnItemClickListener,
                AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {


                }

                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    genderItem = genderList.get(position)
                }


            }
    }


    protected fun getSelectPhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, 102)
    }

    protected fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }


    /**
     * reduces the size of the image
     * @param image
     * @param maxSize
     * @return
     */
    private fun getResizedBitmap(image: Bitmap?, maxSize: Int): Bitmap? {
        var width = image?.width
        var height = image?.height
        val bitmapRatio = height?.toFloat()?.div(width!!.toFloat())
        if (bitmapRatio != null) {
            if (bitmapRatio > 1) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }
        }
        return Bitmap.createScaledBitmap(image!!, width!!, height!!, true)
    }

    private fun getByteArrayImage(data: Intent?): ByteArray {
        val stream = ByteArrayOutputStream()
        val bmp = data!!.extras!!["data"] as Bitmap?
        bmp!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        bmp.recycle()
        return byteArray
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        TODO("Not yet implemented")
    }

    private fun isValidEmailId(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validation(
        firstname: String,
        lastname: String,
        number: String,
        addres: String,
        email: String,
        dateofBirth: String,
        gender: String
    ): String? {
        if (firstname.isNullOrEmpty()) {
            dataBinding.firstnameEdittext.setError("firstname should not be empty")

            return "firstname number should not be empty"
        } else if (lastname.isNullOrEmpty()) {
            dataBinding.lastnameEdittext.setError("lastname should not be empty")

            return "lastname should not be empty"
        } else if (!isValidEmailId(email)) {
            dataBinding.emailEdittext.setError("Invlaid Email Address")
            return "Invalid Email Address"
        } else if (number.isNullOrEmpty()) {
            dataBinding.mobilenumberEdittext.setError("Phone number should not be empty")
            return "Phone number should not be empty"
        } else if (number.length < 10) {
            dataBinding.mobilenumberEdittext.setError("Phone number not valid must contains 10 Number")
            return "Phone number not valid must contains 10 Number"
        } else if (addres.isNullOrEmpty()) {
            dataBinding.addressEdittext.setError("Address should not be empty")

            return "Address should not be empty"
        } else if (gender.isNullOrEmpty()) {
            dataBinding.genderSpinner.setError("Gender should not be empty")

            return "Gender should not be empty"
        }else if (dateofBirth.isNullOrEmpty()) {
            dataBinding.birthLabelEdittext.setError("Date of birth should not be empty")

            return "Date of birth should not be empty"
        }
//        else if (pickedDate >= eighteenYearsAgo) {
//            // Picked a date less than 18 years ago
//            dataBinding.birthLabelEdittext.setError("Minimum age must be 18 years. Please re-check")
//
//            return "Minimum age must be 18 years. Please re-check"
//        }

        else {
            return null
        }


    }


    fun dateStringToUTC(value: String): String? {
        var date1: Date? = null
        try {
            date1 = SimpleDateFormat(DATE_FORMAT_PATTERN_4).parse(value)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        //Date c = Calendar.getInstance().getTime();
        val df = SimpleDateFormat(DATE_FORMAT_PATTERN_4, Locale.getDefault())
        val formattedDate = df.format(date1)
        val outputformat = SimpleDateFormat(DATE_FORMAT_PATTERN_14)
        var date: Date? = null
        try {
            date = df.parse(formattedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return outputformat.format(date)
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

