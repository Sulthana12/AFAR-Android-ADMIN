package com.wings.mile

import `in`.shrinathbhosale.preffy.Preffy
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.wings.mile.Adddrivers.Driverdetails
import com.wings.mile.Adddrivers.Driverdetails1
import com.wings.mile.Adddrivers.Driverdetails2
import com.wings.mile.Utils.AppConstants.DATE_FORMAT_PATTERN_14
import com.wings.mile.Utils.AppConstants.DATE_FORMAT_PATTERN_4
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.Utils.Utility
import com.wings.mile.Utils.Utility.Companion.getFolderSizeLabel
import com.wings.mile.ViewDrivers.DetailActivity
import com.wings.mile.ViewDrivers.DetailsActivity1
import com.wings.mile.ViewDrivers.DetailsActivity2
import com.wings.mile.activity.*
import com.wings.mile.adapter.AlldriversAdapter
import com.wings.mile.databinding.ActivityDashboardBinding
import com.wings.mile.databinding.HomescreenBinding
import com.wings.mile.dialog.CustomSuccessDialogFragment
import com.wings.mile.dialog.OptionDialogFragment
import com.wings.mile.model.*
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*
import java.util.regex.Pattern
import kotlin.concurrent.fixedRateTimer

class DashboardViewActivity : BaseActivity(), OptionDialogFragment.OnItemSelect,
    CustomSuccessDialogFragment.OnItemSelectDialog {
    private val TAG = "MainActivity"
    val CAMERA_PERMISSION = "android.permission.CAMERA"
    val WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE"
    val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
    val ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION"
    val READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES"
   // val REQUEST_PHONE_CALL = "android.permission.CALL_PHONE"
    var Res: String? = ""
    var bundle: Bundle? = null
    lateinit var viewModel: MainViewModel
    private lateinit var commodityFragment: DetailsActivity1
    private lateinit var Driver: DetailsActivity2
    private val retrofitService = RetrofitService.getInstance()

    private var someActivityResultLauncher: ActivityResultLauncher<Intent>? = null

    var photoimageView: ImageView? = null
    var textview: AppCompatTextView? = null
    var bm: Bitmap? = null
    private var stringBase64ImageProfile: String? = ""
    private var stringBase64VehicleImage: String? = ""
    private var stringBase64AadharImage: String? = ""
    private var stringBase64PanImage: String? = ""
    private var stringBase64InsuranceImage: String? = ""
    private var stringBase64LicenseImage: String? = ""
    var value: Int = 0
    var genderlist = gender()
    private lateinit var byteArrayImage: ByteArray
    lateinit var pickedDate: LocalDate
    var eighteenYearsAgo = LocalDate.now() - Period.ofYears(18)
    private lateinit var allAdapter: AlldriversAdapter
    var preffy: Preffy? = null
    protected val REQUIRED_PERMISSION =
        arrayOf(CAMERA_PERMISSION, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
//    protected val REQUIRED_PERMISSION_CAll =
//        arrayOf(REQUEST_PHONE_CALL)
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDashboardBinding
    lateinit var navController:NavController
    lateinit var genderItem: genderItem
    lateinit var savedriver: getdriver
    lateinit var savedriver1: savedriver
    lateinit var loginResponse: loginResponseItem
    var imagedata:String?=""
    var imagelocation:String?=""
    var imagelocationname:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )
        setContentView(binding.root)
        loginResponse = Gson().fromJson(Pref_storage.getDetail(this,"LoginRes"), com.wings.mile.model.loginResponseItem::class.java)

//        setSupportActionBar(binding.toolbarLayout.toolTitle)
//        binding.toolbarLayout.icon.visibility = View.VISIBLE
//        binding.toolbarLayout.logouticon.visibility = View.VISIBLE
        try {
            val bundle = intent.extras
            // bundle = intent.getBundleExtra("Arguments")
            Pref_storage.setDetail(this,"Pantext","")
            Pref_storage.setDetail(this,"Vehicletext","")
            Pref_storage.setDetail(this,"Insurancetext","")
            Pref_storage.setDetail(this,"adhartext","")
            Pref_storage.setDetail(this,"Licensetext","")
            Pref_storage.setDetail(this,"Licensenumber","")
            Pref_storage.setDetail(this,"adharnumber","")
            Pref_storage.setDetail(this,"Insurancenumber","")
            Pref_storage.setDetail(this,"Vehiclenumber","")
            Pref_storage.setDetail(this,"Pannumber","")
            Pref_storage.setDetail(this,"Vehiclename","")
            Pref_storage.setDetail(this,"Statename","")
            Pref_storage.setDetail(this,"Districtname","")
            Pref_storage.setDetail(this,"driverpdf","")
            Pref_storage.setDetail(this,"idproof","")
            Pref_storage.setDetail(this,"pdfbase64","")
            Pref_storage.setDetail(this,"getdriverdetails","")
            Pref_storage.setDetail(this,"Vehicleid","")
            Pref_storage.setDetail(this,"Districtid","")
            Pref_storage.setDetail(this,"Insuranceexpirydate","")
            Pref_storage.setDetail(this,"Licenseexpirydate","")
            Pref_storage.setDetail(this,"Driverdetails","")
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        binding.toolbarLayout.logouticon.setOnClickListener {
//            showExitAlertDialog()
//        }
//        binding.toolbarLayout.icon.setOnClickListener {
//            it.let {
//
//                val intent = Intent(this, ProfileActivity::class.java)
//                startActivity(intent)
//
//            }
//
//
//        }
//        navController = findNavController(R.id.nav_host_fragment_content_dashboard)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        viewModel.fetchCountryDetails().observe(this, Observer {

            if (!it.isNullOrEmpty()) {
                //preffy?.putString("Country",Gson().toJson(it))
            }

        })

        viewModel.fetchGenderDetails().observe(this, Observer {
            updateGenderSpinner()

        })
        viewModel.fetchVehicleDetails().observe(this, Observer {


        })
        viewModel.fetchStateDetails().observe(this, Observer {


        })
        binding.commodityunit.birthLabelEdittext.setInputType(InputType.TYPE_NULL)

        binding.commodityunit.birthLabelEdittext.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    var conversion: String = ""
                    if (dayOfMonth.toInt() <= 9)
                        conversion = "0" + dayOfMonth
                    else
                        conversion = "" + dayOfMonth
                    pickedDate = LocalDate.of(year, month + 1, day)
                    if (pickedDate >= eighteenYearsAgo) {
                        // Picked a date less than 18 years ago
                        binding.commodityunit.birthLabelEdittext.setError("Minimum age must be 18 years. Please re-check")
                    } else {
                        binding.commodityunit.birthLabelEdittext.setError(null)

                        binding.commodityunit.birthLabelEdittext.setText(conversion + "/" + (monthOfYear + 1) + "/" + year)
                    }

                },
                year,
                month,
                day
            )

            dpd.show()
            val positiveColor = ContextCompat.getColor(
                this,
                com.wings.mile.R.color.teal_200
            )
            val negativeColor = ContextCompat.getColor(
                this,
                com.wings.mile.R.color.teal_200
            )
            dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
            dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)

        }
        binding.commodityunit.photoEdittexts.setOnClickListener {
           pickPhotoImage( binding.commodityunit.photoEdittexts)

        }
        val bundle = intent.extras
        val request = bundle?.get("getdetails")
        savedriver = Gson().fromJson(request.toString(), getdriver::class.java)
        binding.commodityunit.firstnameEdittext.setText(savedriver.first_Name)
        binding.commodityunit.lastnameEdittext.setText(savedriver.last_Name)
        binding.commodityunit.genderSpinner.setText(savedriver.gender)
        binding.commodityunit.emailEdittext.setText(savedriver.email_id)
        binding.commodityunit.addressEdittext.setText(savedriver.address)
        binding.commodityunit.mobilenumberEdittext.setText(savedriver.phone_num)
        binding.commodityunit.pincodeEdittext.setText(savedriver.pincode)
        binding.commodityunit.birthLabelEdittext.setText(savedriver.date_of_birth!!)
        binding.commodityunit.birthLabelEdittext.setInputType(InputType.TYPE_NULL)
        binding.commodityunit.calllayout.visibility=View.VISIBLE
        try {
            // (activity as DashboardActivity).iconfeedback()
            val genderValue = Gson().toJson(getgender())
            //genderList = Gson().fromJson(genderValue.toString(), gender::class.java)
            updateGenderSpinner()
        }catch (e:Exception){
            e.printStackTrace()
        }
        if(savedriver.usr_img_file_location!!.isNotEmpty() && getBase64()==null){
            Glide.with( binding.commodityunit.photoEdittexts).load(savedriver.usr_img_file_location+savedriver.usr_img_file_name)
                .into( binding.commodityunit.photoEdittexts)
        }else{
            if(getBase64().equals("")){
                Glide.with( binding.commodityunit.photoEdittexts).load(savedriver.usr_img_file_location+savedriver.usr_img_file_name)
                    .into( binding.commodityunit.photoEdittexts)
            }else{
                val decodedString: ByteArray = Base64.decode((getBase64()), Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                binding.commodityunit.photoEdittexts.setImageBitmap(decodedByte)
            }
        }
        binding.commodityunit.call.setOnClickListener {
            try {
               call( binding.commodityunit.mobilenumberEdittext.text.toString())

            } catch (e: Exception) {
                e.printStackTrace()

            }
        }


        binding.commodityunit.next.setOnClickListener {

            val result = validation(
                binding.commodityunit.firstnameEdittext.text.toString(),
                binding.commodityunit.lastnameEdittext.text.toString(),
                binding.commodityunit.mobilenumberEdittext.text.toString(),
                binding.commodityunit.addressEdittext.text.toString(),
                binding.commodityunit.emailEdittext.text.toString(),
                binding.commodityunit.birthLabelEdittext.text.toString(),
                binding.commodityunit.genderSpinner.text.toString()
            )


            if (result.isNullOrEmpty()) {


                if(getBase64()!!.isEmpty()){

                    imagelocation=savedriver.usr_img_file_location
                    imagelocationname=savedriver.usr_img_file_name
                }else {
                    imagelocation=""
                    imagelocationname=""
                }

                savedriver1 = savedriver(
                    first_name = binding.commodityunit.firstnameEdittext.text.toString(),
                    last_name = binding.commodityunit.lastnameEdittext.text.toString(),
                    gender = binding.commodityunit.genderSpinner.text.toString(),
                    phone_number = binding.commodityunit.mobilenumberEdittext.text.toString(),
                    user_address = binding.commodityunit.addressEdittext.text.toString(),
                    email_id = binding.commodityunit.emailEdittext.text.toString(),
                    date_of_birth = getdate(binding.commodityunit.birthLabelEdittext.text.toString()),
                    image_data = getBase64(),
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
                    pincode = binding.commodityunit.pincodeEdittext.text.toString(),
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

//                val bundle = Bundle()
////                bundle.putString("Vehicle", vehicleValue.toString())
////                bundle.putString("Country", countryValue.toString())
////                bundle.putString("State", StateValue.toString())
////                bundle.putString("State", StateValue.toString())
//                bundle.putString("SaveRequest", Gson().toJson(savedriver1))
//                bundle.putString("getRequest", Gson().toJson(savedriver))
//                findNavController().navigate(R.id.action_FifthFragment_to_SixthFragment,bundle)

                    attachFragment()
            }


        }











}

    override fun onSupportNavigateUp(): Boolean {
        navController= findNavController(R.id.nav_host_fragment_content_dashboard)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun pickPhotoImages(imageView: ImageView, imagename: AppCompatTextView, i: Int) {
        photoimageView = imageView
        textview = imagename
        value = i
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,

                    )
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else if (checkSelfPermission(Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,

                    )
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                OptionDialogFragment(this, false, 2).show(
                    supportFragmentManager,
                    "OptionDialogFragment"
                )
            }
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,

                    )
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else if (checkSelfPermission(Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,

                    )
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                OptionDialogFragment(this, false, 2).show(
                    supportFragmentManager,
                    "OptionDialogFragment"
                )
            }
        } else {
            //system OS is < Marshmallow
            OptionDialogFragment(this, false, 2).show(
                supportFragmentManager,
                "OptionDialogFragment"
            )
        }

    }

    fun pickPhotoImage(imageView: ImageView) {
        photoimageView = imageView
        value = 0
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,

                    )
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else if (checkSelfPermission(Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,

                    )
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                OptionDialogFragment(this, false, 2).show(
                    supportFragmentManager,
                    "OptionDialogFragment"
                )
            }
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,

                    )
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else if (checkSelfPermission(Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,

                    Manifest.permission.CAMERA,
                )
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                OptionDialogFragment(this, false, 2).show(
                    supportFragmentManager,
                    "OptionDialogFragment"
                )
            }
        } else {
            //system OS is < Marshmallow
            OptionDialogFragment(this, false, 2).show(
                supportFragmentManager,
                "OptionDialogFragment"
            )
        }
    }
    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000

        //Permission code
        private val REQUEST_CODE_CAMERA = 1002
        private val PERMISSION_CODE = 1001
    }


    //handle requested permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    OptionDialogFragment(this, false, 2).show(
                        supportFragmentManager,
                        "OptionDialogFragment"
                    )
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {

            bm = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, data?.data)
            val tempUri = data?.data

//            if (getFolderSizeLabel(this, tempUri) > 20) {
//                CustomSuccessDialogFragment(this, R.string.img_size_alert, R.drawable.ic_warning).show(supportFragmentManager, "CustomSuccessDialogFragment")
//                stringBase64ImageProfile = null
//            } else {
                when (value) {

                    0 -> {

                        photoimageView!!.setImageURI(data?.data)
                        stringBase64ImageProfile = encodeTobase64(bm!!)


                    }
                    1 -> {

                        textview!!.text = getRealPathFromURI(data?.data)
                        Pref_storage.setDetail(this,"adhartext",textview!!.text.toString())
                        textview!!.visibility = View.VISIBLE
                        stringBase64AadharImage = encodeTobase64(bm!!)

                    }
                    2 -> {

                        textview!!.text = getRealPathFromURI(data?.data)
                        Pref_storage.setDetail(this,"Licensetext",textview!!.text.toString())

                        textview!!.visibility = View.VISIBLE
                        stringBase64LicenseImage = encodeTobase64(bm!!)

                    }
                    3 -> {

                        textview!!.text = getRealPathFromURI(data?.data)
                        Pref_storage.setDetail(this,"Insurancetext",textview!!.text.toString())

                        textview!!.visibility = View.VISIBLE
                        stringBase64InsuranceImage = encodeTobase64(bm!!)

                    }
                    4 -> {

                        textview!!.text = getRealPathFromURI(data?.data)
                        Pref_storage.setDetail(this,"Pantext",textview!!.text.toString())

                        textview!!.visibility = View.VISIBLE
                        stringBase64PanImage = encodeTobase64(bm!!)

                    }
                    5 -> {
                        textview!!.text = getRealPathFromURI(data?.data)
                        Pref_storage.setDetail(this,"Vehicletext",textview!!.text.toString())

                        textview!!.visibility = View.VISIBLE
                        stringBase64VehicleImage = encodeTobase64(bm!!)

                    }
               // }
            }


        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
            // else handle other activity results
            val mFileTemp: File
            try {

                val photo = data!!.extras!!["data"] as Bitmap?
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                var tempUri: Uri? = null
                tempUri = Utility.getImageUri(this, photo)
//                if (getFolderSizeLabel(this, tempUri) > 20) {
//                    CustomSuccessDialogFragment(this, R.string.img_size_alert, R.drawable.ic_warning).show(supportFragmentManager, "CustomSuccessDialogFragment")
//                    stringBase64ImageProfile = null
//                } else {
                    if (value != 0) {
                        tempUri = Utility.getImageUri(this, photo)
                    }
                    byteArrayImage = getByteArrayImage(data)

                    when (value) {

                        0 -> {

                            photoimageView!!.setImageBitmap(photo)
                            stringBase64ImageProfile =
                                Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
                            Pref_storage.setDetail(this,"profileimage",stringBase64ImageProfile)


                        }
                        1 -> {

                            if (tempUri != null) {
                                textview!!.text = getRealPathFromURI(tempUri)
                                Pref_storage.setDetail(this,"adhartext",textview!!.text.toString())

                            } else {
                                Log.e("check", "check data---> ")
                            }
                            stringBase64AadharImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)

                        }
                        2 -> {

                            textview!!.text = getRealPathFromURI(tempUri!!)

                            stringBase64LicenseImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
                            Pref_storage.setDetail(this,"Licensetext",textview!!.text.toString())


                        }
                        3 -> {

                            textview!!.text = getRealPathFromURI(tempUri!!)
                            stringBase64InsuranceImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
                            Pref_storage.setDetail(this,"Insurancetext",textview!!.text.toString())

                        }
                        4 -> {

                            textview!!.text = getRealPathFromURI(tempUri!!)
                            stringBase64PanImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
                            Pref_storage.setDetail(this,"Pantext",textview!!.text.toString())

                        }
                        5 -> {

                            textview!!.text = getRealPathFromURI(tempUri!!)
                            stringBase64VehicleImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
                            Pref_storage.setDetail(this,"Vehicletext",textview!!.text.toString())


                        }
                    //}
                }

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }
    }


    private fun getByteArrayImage(data: Intent?): ByteArray {
        val stream = ByteArrayOutputStream()
        val bmp = data!!.extras!!["data"] as Bitmap?
        bmp!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        bmp.recycle()
        return byteArray
    }

    fun encodeTobase64(image: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        val imageEncoded = Base64.encodeToString(b, Base64.DEFAULT)
        Log.e("LOOK", imageEncoded)
        return imageEncoded
    }

    fun getBase64(): String? {

        return stringBase64ImageProfile
    }


    override fun onGalleryButtonClicked() {
        pickImageFromGallery()
    }

    override fun onCameraButtonClicked() {
        takePictureCamera()

    }

    override fun onOkButtonClicked() {
    }


    private fun getRealPathFromURI(uri: Uri?): String {
        var path = ""
        if (baseContext.contentResolver != null) {
            val cursor = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    protected fun allPermissionsGrant(): Boolean {
        for (permission in REQUIRED_PERMISSION) {
            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

//    protected fun allPermissionsGrantphone(): Boolean {
//        for (permission in REQUIRED_PERMISSION_CAll) {
//            if (ContextCompat.checkSelfPermission(
//                    baseContext,
//                    permission!!
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return true
//            }
//        }
//        return false
//    }

    private fun takePictureCamera() {
        startCamera()
    }

    override fun onStart() {
        super.onStart()
        setLauncherResult()
    }

    private fun startCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (someActivityResultLauncher != null) {
            someActivityResultLauncher!!.launch(cameraIntent)
        }
        //startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA)
    }

    private fun setLauncherResult() {
        someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                // Here, no request code
                val data = result.data
                if (data != null) {

                    val photo = data.extras!!["data"] as Bitmap?
                    var tempUri: Uri? = null
                    if (value != 0) {
                        tempUri = Utility.getImageUri(this, photo)
                    }
                    byteArrayImage = getByteArrayImage(data)

                    when (value) {

                        0 -> {

                            photoimageView!!.setImageBitmap(photo)
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            stringBase64ImageProfile =
                                Base64.encodeToString(byteArrayImage, Base64.DEFAULT)

                        }
                        1 -> {

                            textview!!.visibility = View.VISIBLE
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            textview!!.text = fileImage.name
                            stringBase64AadharImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
                            Pref_storage.setDetail(this,"adhartext",textview!!.text.toString())

                        }
                        2 -> {

                            textview!!.visibility = View.VISIBLE
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            textview!!.text = fileImage.name
                            stringBase64LicenseImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
                            Pref_storage.setDetail(this,"Licensetext",textview!!.text.toString())

                        }
                        3 -> {

                            textview!!.visibility = View.VISIBLE
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            textview!!.text = fileImage.name
                            stringBase64InsuranceImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
                            Pref_storage.setDetail(this,"Insurancetext",textview!!.text.toString())


                        }
                        4 -> {

                            textview!!.visibility = View.VISIBLE
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            textview!!.text = fileImage.name
                            stringBase64PanImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
                            Pref_storage.setDetail(this,"Pantext",textview!!.text.toString())


                        }
                        5 -> {

                            textview!!.visibility = View.VISIBLE
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            textview!!.text = fileImage.name
                            stringBase64VehicleImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
                            Pref_storage.setDetail(this,"Vehicletext",textview!!.text.toString())


                        }
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this!!.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

//    fun call(phone: String) {
//
//        if (!allPermissionsGrantphone()) {
//            val num = "tel:" + phone
//            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(num))
//            startActivity(intent)
//        } else {
//
//            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION_CAll, 120)
//        }
//    }

    fun getpanbase64(): String? {

        return stringBase64PanImage
    }

    fun getinsurancebase64(): String? {

        return stringBase64InsuranceImage
    }

    fun getaadharbase64(): String? {

        return stringBase64AadharImage
    }

    fun getlicense(): String? {

        return stringBase64LicenseImage
    }

    fun getvehicles(): String? {

        return stringBase64VehicleImage
    }

    fun getgender(): gender {

        return viewModel.genderlist
    }

    fun icon() {
        binding.actionbarAccount.icon.visibility = View.GONE
        binding.actionbarAccount.logouticon.visibility = View.GONE
    }

    fun icons() {
        binding.actionbarAccount.icon.visibility = View.VISIBLE
        binding.actionbarAccount.logouticon.visibility = View.VISIBLE
        binding.actionbarAccount.logouticon.setImageResource(R.drawable.logout)
        binding.actionbarAccount.logouticon.setOnClickListener {
            showExitAlertDialog()
        }
    }

    fun profileicon(): ImageView {
        return binding.actionbarAccount.icon
    }

    fun iconfeedback() {
        binding.actionbarAccount.icon.visibility = View.GONE
        binding.actionbarAccount.logouticon.visibility = View.VISIBLE
        binding.actionbarAccount.logouticon.setImageResource(R.drawable.chat)
        binding.actionbarAccount.logouticon.setOnClickListener {

            val intent = Intent(this, FeedbackActivity::class.java)
            startActivity(intent)
        }
    }

    fun datapass(): Bundle {
        val bundle = Bundle()
        bundle.putString("Gender", Gson().toJson(viewModel.genderlist))
        bundle.putString("State", Gson().toJson(viewModel.statelist))
        bundle.putString("Vehicle", Gson().toJson(viewModel.vehiclelist))
        bundle.putString("Country", Gson().toJson(viewModel.countrylist))
        return bundle
    }


    private fun replaceFragment(fragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.nav_host_fragment, fragment)
        ft.addToBackStack("Commodity")
        ft.commit()
    }

    /*Exit Application alert functionality*/
    private fun showExitAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage("Do You Want to Logout?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes) { dialog: DialogInterface?, which: Int ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
                startActivity(intent)

            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no) { dialog: DialogInterface, which: Int ->
                // Continue with delete operation
                dialog.dismiss()
            }
            .show()
    }

    fun getvehicle(): vehicle {

        return viewModel.vehiclelist
    }

    fun getstate(): getstate {

        return viewModel.statelist
    }


    fun getcountry(): getCountry {

        return viewModel.countrylist
    }
    fun logout(){
        val intent = Intent(this, ProfileActivity::class.java)

        Log.e("res",""+Res.toString())

        startActivity(intent)
    }


    private fun showAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("Signout")
            .setMessage(getString(R.string.signout)) // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes) { dialog: DialogInterface?, which: Int ->
                dialog!!.dismiss()
                val i = Intent(this, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                finish()

            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no) { dialog: DialogInterface, which: Int ->
                // Continue with delete operation
                val i = Intent(this, NavigationActivity::class.java)
                startActivity(i)
               dialog.dismiss()
            }
            .show()
    }
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }


     fun attachFragment() {
        commodityFragment = DetailsActivity1()
        val bundle = Bundle()
         bundle.putString("SaveRequest", Gson().toJson(savedriver1))
               bundle.putString("getRequest", Gson().toJson(savedriver))
        commodityFragment.arguments = bundle
        replaceFragment(commodityFragment)
    }
     fun attachFragments() {
        Driver = DetailsActivity2()
        val bundle = Bundle()
//        bundle.putString("districtName", loginUserResponse().lgDirLst[0].districtName)
//        bundle.putInt("market_id", market_id)
//        bundle.putInt("cmdt_grp_id", cmdt_grp_id)
//        bundle.putString("market_name", market_name)
//        bundle.putString("cmdt_grp_name", cmdt_grp_name)
//        bundle.putBoolean("isFromHomeScreen", true)
        Driver.arguments = bundle
        replaceFragment(Driver)
    }
    fun updateGenderSpinner() {
        var adapter = ArrayAdapter(this, R.layout.itempopup, R.id.item, viewModel.genderlist)
        binding.commodityunit.genderSpinner.setAdapter(adapter)

        binding.commodityunit.genderSpinner.onItemClickListener =
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
                    genderItem = viewModel.genderlist.get(position)
                }


            }
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
            binding.commodityunit.firstnameEdittext.setError("firstname should not be empty")

            return "firstname number should not be empty"
        } else if (lastname.isNullOrEmpty()) {
            binding.commodityunit.lastnameEdittext.setError("lastname should not be empty")

            return "lastname should not be empty"
        } else if (!isValidEmailId(email)) {
            binding.commodityunit.emailEdittext.setError("Invalid Email Address")
            return "Invalid Email Address"
        } else if (number.isNullOrEmpty()) {
            binding.commodityunit.mobilenumberEdittext.setError("Phone number should not be empty")
            return "Phone number should not be empty"
        } else if (number.length < 10) {
            binding.commodityunit.mobilenumberEdittext.setError("Phone number not valid must contains 10 Number")
            return "Phone number not valid must contains 10 Number"
        } else if (addres.isNullOrEmpty()) {
            binding.commodityunit.addressEdittext.setError("Address should not be empty")

            return "Address should not be empty"
        } else if (binding.commodityunit.pincodeEdittext.text.toString().isNullOrEmpty()) {
            binding.commodityunit.pincodeEdittext.error = "Pincode should not be empty"

            return "Pincode should not be empty"
        }else if (gender.isNullOrEmpty()) {
            binding.commodityunit.genderSpinner.setError("Gender should not be empty")

            return "Gender should not be empty"
        }else if (dateofBirth.isNullOrEmpty()) {
            binding.commodityunit.birthLabelEdittext.setError("Date of birth should not be empty")

            return "Date of birth should not be empty"
        }
//        else if (pickedDate >= eighteenYearsAgo) {
//            // Picked a date less than 18 years ago
//            binding.commodityunit.birthLabelEdittext.setError("Minimum age must be 18 years. Please re-check")
//
//            return "Minimum age must be 18 years. Please re-check"
//        }
         else {
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
}