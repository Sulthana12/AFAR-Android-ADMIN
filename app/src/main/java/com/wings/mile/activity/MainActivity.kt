package com.wings.mile.activity

import `in`.shrinathbhosale.preffy.Preffy
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.wings.mile.Adddrivers.Driverdetails
import com.wings.mile.Adddrivers.Driverdetails1
import com.wings.mile.Adddrivers.Driverdetails2
import com.wings.mile.R
import com.wings.mile.Utils.BaseActivity
import com.wings.mile.Utils.DatePicker
import com.wings.mile.Utils.Utility
import com.wings.mile.Utils.Utility.Companion.getImageUri
import com.wings.mile.ViewDrivers.DetailActivity
import com.wings.mile.ViewDrivers.DetailsActivity2
import com.wings.mile.adapter.AlldriversAdapter
import com.wings.mile.databinding.HomescreenBinding
import com.wings.mile.dialog.CustomSuccessDialogFragment
import com.wings.mile.dialog.OptionDialogFragment
import com.wings.mile.service.RetrofitService
import com.wings.mile.viewmodel.MainRepository
import com.wings.mile.viewmodel.MainViewModel
import com.wings.mile.viewmodel.MyViewModelFactory
import com.google.gson.Gson
import com.wings.mile.FirstFragment
import com.wings.mile.ViewDrivers.DetailsActivity1
import com.wings.mile.model.*
import java.io.ByteArrayOutputStream
import java.io.File


class MainActivity : BaseActivity(), OptionDialogFragment.OnItemSelect,
    CustomSuccessDialogFragment.OnItemSelectDialog, AlldriversAdapter.OnItemClicked {
    private val TAG = "MainActivity"
    val CAMERA_PERMISSION = "android.permission.CAMERA"
    val WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE"
    val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
    val ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION"
    val READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES"
    //override val REQUEST_PHONE_CALL = "android.permission.CALL_PHONE"
    private lateinit var binding: HomescreenBinding
    var Res: String? = ""
    var bundle: Bundle? = null
    lateinit var viewModel: MainViewModel

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

    private lateinit var allAdapter: AlldriversAdapter
    var preffy: Preffy? = null
    protected val REQUIRED_PERMISSION =
        arrayOf(CAMERA_PERMISSION, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
//    protected val REQUIRED_PERMISSION_CAll =
//        arrayOf(REQUEST_PHONE_CALL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescreen)
        binding = HomescreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preffy = Preffy.getInstance(this)
        binding.toolbarLayout.toolbarTitle.text = "Home"
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )

        try {
            val bundle = intent.extras
            // bundle = intent.getBundleExtra("Arguments")

            Res = bundle?.getString("LoginRes")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.toolbarLayout.icon.visibility = View.VISIBLE
        binding.toolbarLayout.logouticon.visibility = View.VISIBLE
       // binding.adddriver.visibility = View.VISIBLE
       // binding.LoginAvi.visibility = View.VISIBLE
       // binding!!.LoginAvi.show()


        binding.toolbarLayout.logouticon.setOnClickListener {
            showExitAlertDialog()
        }


        viewModel.fetchCountryDetails().observe(this, Observer {

            if (!it.isNullOrEmpty()) {
                //preffy?.putString("Country",Gson().toJson(it))
            }

        })

        viewModel.fetchGenderDetails().observe(this, Observer {

        })
        viewModel.fetchVehicleDetails().observe(this, Observer {


        })
        viewModel.fetchStateDetails().observe(this, Observer {


        })

//        binding.adddriver.setOnClickListener {
//            val driverdetails = Driverdetails()
//            val bundle = Bundle()
//            bundle.putString("Gender", Gson().toJson(viewModel.genderlist))
//            bundle.putString("State", Gson().toJson(viewModel.statelist))
//            bundle.putString("Vehicle", Gson().toJson(viewModel.vehiclelist))
//            bundle.putString("Country", Gson().toJson(viewModel.countrylist))
//            bundle.putString("LoginRes", Res)
//            driverdetails.arguments = bundle
//            replaceFragment(driverdetails)
//
//        }
        val driverdetails = FirstFragment()

        replaceFragment(driverdetails)

        supportFragmentManager.addOnBackStackChangedListener(getListener())

    }

    override fun onStart() {
        super.onStart()
        setLauncherResult()
    }
    fun datapass(){
        val driverdetails = Driverdetails()
        val bundle = Bundle()
        bundle.putString("Gender", Gson().toJson(viewModel.genderlist))
        bundle.putString("State", Gson().toJson(viewModel.statelist))
        bundle.putString("Vehicle", Gson().toJson(viewModel.vehiclelist))
        bundle.putString("Country", Gson().toJson(viewModel.countrylist))
        bundle.putString("LoginRes", Res)
        driverdetails.arguments = bundle
    }

    /*Exit Application alert functionality*/
    private fun showExitAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage("Do You Want to Logout?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes) { dialog: DialogInterface?, which: Int ->
                finish()
                FirebaseAuth.getInstance().signOut()
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no) { dialog: DialogInterface, which: Int ->
                // Continue with delete operation
                dialog.dismiss()
            }
            .show()
    }

    override fun onBackPressed() {
        val fm: FragmentManager = supportFragmentManager
        if (fm.backStackEntryCount > 0) {
            Log.i("MainActivity", "popping backstack")
            fm.popBackStack()
        } else {
            showExitAlertDialog()
        }
        /*
        if (supportFragmentManager.backStackEntryCount >= 1) {
            popStack()
        } else {

        }*/
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.frameContainer, fragment)
            .addToBackStack(null)
            .commit()
    }


    fun replaceFragment1(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .commit()
    }

    fun replaceFragmentWithArgumnets(saveRes: String, fragment: Fragment) {
        val bundle = Bundle()
        bundle.putString("SaveRes", saveRes)
        fragment.arguments = bundle
        replaceFragment(fragment)
    }


    fun showDialog() {
        val mDatePickerDialogFragment =
            DatePicker()
        mDatePickerDialogFragment.show(supportFragmentManager, "DATE PICK")
    }


    fun popStack() {
        val fm: FragmentManager = supportFragmentManager
        if (fm.backStackEntryCount > 0) {
            Log.i("MainActivity", "popping backstack")
            fm.popBackStack()
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super")
            super.onBackPressed()
        }
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


    private fun getListener(): FragmentManager.OnBackStackChangedListener {
        return FragmentManager.OnBackStackChangedListener {
            val manager = supportFragmentManager
            val ft: FragmentTransaction = manager.beginTransaction()
            val currFrag = manager.findFragmentById(R.id.frameContainer)
            if (currFrag is FirstFragment) {
                binding.toolbarLayout.icon.visibility = View.GONE
                binding.toolbarLayout.logouticon.visibility = View.GONE
                binding.toolbarLayout.toolbarTitle.text = getString(R.string.profile)
                binding.adddriver.visibility = View.GONE

            } else if (currFrag is Driverdetails) {
                binding.toolbarLayout.icon.visibility = View.GONE
                binding.toolbarLayout.logouticon.visibility = View.GONE
                binding.toolbarLayout.toolbarTitle.text = getString(R.string.header)
                binding.adddriver.visibility = View.GONE

            } else if (currFrag is Driverdetails1) {
                binding.toolbarLayout.icon.visibility = View.GONE
                binding.toolbarLayout.logouticon.visibility = View.GONE
                binding.adddriver.visibility = View.GONE

                binding.toolbarLayout.toolbarTitle.text = getString(R.string.header)
            } else if (currFrag is Driverdetails2) {
                binding.toolbarLayout.icon.visibility = View.GONE
                binding.toolbarLayout.logouticon.visibility = View.GONE
                binding.adddriver.visibility = View.GONE

                binding.toolbarLayout.toolbarTitle.text = getString(R.string.header)
            } else if (currFrag is DetailActivity) {
                binding.toolbarLayout.icon.visibility = View.GONE
                binding.toolbarLayout.logouticon.visibility = View.VISIBLE
                binding.toolbarLayout.logouticon.setImageResource(R.drawable.chat)
                binding.adddriver.visibility = View.GONE

                binding.toolbarLayout.logouticon.setOnClickListener {

                    val intent = Intent(this, FeedbackActivity::class.java)
                    startActivity(intent)
                }
                binding.toolbarLayout.toolbarTitle.text = getString(R.string.header)
            } else if (currFrag is DetailsActivity1) {
                binding.toolbarLayout.icon.visibility = View.GONE
                binding.toolbarLayout.logouticon.visibility = View.GONE
                binding.adddriver.visibility = View.GONE

                binding.toolbarLayout.toolbarTitle.text = getString(R.string.header)
            } else if (currFrag is DetailsActivity2) {
                binding.toolbarLayout.icon.visibility = View.GONE
                binding.toolbarLayout.logouticon.visibility = View.GONE
                binding.adddriver.visibility = View.GONE

                binding.toolbarLayout.toolbarTitle.text = getString(R.string.header)
            } else {
                binding.toolbarLayout.icon.visibility = View.VISIBLE
                binding.toolbarLayout.logouticon.visibility = View.VISIBLE
                binding.adddriver.visibility = View.VISIBLE
                binding.toolbarLayout.toolbarTitle.text = getString(R.string.home)
                binding.toolbarLayout.logouticon.setImageResource(R.drawable.logout)
                binding.toolbarLayout.logouticon.setOnClickListener {
                    showExitAlertDialog()
                }
            }
        }
    }

    fun pickPhotoImages(imageView: ImageView, imagename: AppCompatTextView, i: Int) {
        photoimageView = imageView
        textview = imagename
        value = i
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            when (value) {

                0 -> {

                    photoimageView!!.setImageURI(data?.data)
                    stringBase64ImageProfile = encodeTobase64(bm!!)

                }
                1 -> {

                    textview!!.text = getRealPathFromURI(data?.data)
                    textview!!.visibility = View.VISIBLE
                    stringBase64AadharImage = encodeTobase64(bm!!)

                }
                2 -> {

                    textview!!.text = getRealPathFromURI(data?.data)
                    textview!!.visibility = View.VISIBLE
                    stringBase64LicenseImage = encodeTobase64(bm!!)

                }
                3 -> {

                    textview!!.text = getRealPathFromURI(data?.data)
                    textview!!.visibility = View.VISIBLE
                    stringBase64InsuranceImage = encodeTobase64(bm!!)

                }
                4 -> {

                    textview!!.text = getRealPathFromURI(data?.data)
                    textview!!.visibility = View.VISIBLE
                    stringBase64PanImage = encodeTobase64(bm!!)

                }
                5 -> {

                    textview!!.text = getRealPathFromURI(data?.data)
                    textview!!.visibility = View.VISIBLE
                    stringBase64VehicleImage = encodeTobase64(bm!!)

                }
            }


        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
            // else handle other activity results
            val mFileTemp: File
            try {

                val photo = data!!.extras!!["data"] as Bitmap?
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                var tempUri: Uri? = null
                if (value != 0) {
                    tempUri = getImageUri(this, photo)
                }
                byteArrayImage = getByteArrayImage(data)

                when (value) {

                    0 -> {

                        photoimageView!!.setImageBitmap(photo)
                        stringBase64ImageProfile =
                            Base64.encodeToString(byteArrayImage, Base64.DEFAULT)

                    }
                    1 -> {

                        if (tempUri != null) {
                            textview!!.text = getRealPathFromURI(tempUri)
                        } else {
                            Log.e("check", "check data---> ")
                        }
                        stringBase64AadharImage = encodeTobase64(photo!!)

                    }
                    2 -> {

                        textview!!.text = getRealPathFromURI(tempUri!!)
                        stringBase64LicenseImage = encodeTobase64(photo!!)

                    }
                    3 -> {

                        textview!!.text = getRealPathFromURI(tempUri!!)
                        stringBase64InsuranceImage = encodeTobase64(photo!!)

                    }
                    4 -> {

                        textview!!.text = getRealPathFromURI(tempUri!!)
                        stringBase64PanImage = encodeTobase64(photo!!)

                    }
                    5 -> {

                        textview!!.text = getRealPathFromURI(tempUri!!)
                        stringBase64VehicleImage = encodeTobase64(photo!!)

                    }
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



    override fun onDriverdetailsclick(getdriverdetails: getdriver) {
        val bundle = Bundle()
        bundle.putString("getdetails", Gson().toJson(getdriverdetails))
        val detailFragment = DetailActivity()
        detailFragment.arguments = bundle
        binding.toolbarLayout.icon.visibility = View.GONE
        binding.toolbarLayout.toolbarTitle.text = "Pilot's Details"
        replaceFragment(detailFragment)

    }


    override fun onGalleryButtonClicked() {
        pickImageFromGallery()
    }

    override fun onCameraButtonClicked() {
        takePictureCamera()

    }

    override fun onOkButtonClicked() {
        TODO("Not yet implemented")
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
                        tempUri = getImageUri(this, photo)
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
                            stringBase64AadharImage = encodeTobase64(photo)

                        }
                        2 -> {

                            textview!!.visibility = View.VISIBLE
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            textview!!.text = fileImage.name
                            stringBase64LicenseImage = encodeTobase64(photo)

                        }
                        3 -> {

                            textview!!.visibility = View.VISIBLE
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            textview!!.text = fileImage.name
                            stringBase64InsuranceImage = encodeTobase64(photo!!)

                        }
                        4 -> {

                            textview!!.visibility = View.VISIBLE
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            textview!!.text = fileImage.name
                            stringBase64PanImage = encodeTobase64(photo!!)

                        }
                        5 -> {

                            textview!!.visibility = View.VISIBLE
                            val fileImage: File = Utility().savebitmap(photo!!, value, this)
                            Log.e("check", "image Path ===> " + fileImage.absolutePath)
                            textview!!.text = fileImage.name
                            stringBase64VehicleImage = encodeTobase64(photo!!)

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
    fun getvehicle(): vehicle {

        return viewModel.vehiclelist
    }

    fun getstate(): getstate {

        return viewModel.statelist
    }


    fun getcountry(): getCountry {

        return viewModel.countrylist
    }
    fun getloginresponse(): String {

        return Res!!
    }
}