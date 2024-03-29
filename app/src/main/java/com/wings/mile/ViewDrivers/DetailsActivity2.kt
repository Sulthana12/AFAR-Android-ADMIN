package com.wings.mile.ViewDrivers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.awesomedialog.*
import com.google.gson.Gson
import com.wings.mile.DashboardActivity
import com.wings.mile.NavigationActivity
import com.wings.mile.R
import com.wings.mile.Utils.Pref_storage
import com.wings.mile.databinding.ActivityMain3Binding
import com.wings.mile.model.savedriver
import com.wings.mile.preview.Popuppdf
import com.wings.mile.service.RetrofitService
import com.wings.mile.service.RetrofitService1
import com.wings.mile.viewmodel.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


class DetailsActivity2 : Fragment() {

    private lateinit var dataBinding: ActivityMain3Binding

    lateinit var viewModel: MainViewModel

    private val retrofitService = RetrofitService.getInstance()

    lateinit var myContext: Context

    lateinit var savedriver1: savedriver

    private var someActivityResultLauncher: ActivityResultLauncher<Intent>? = null

    private var pdfFilePath: String? = null
    private var message: String? = null

    private var myFilePath: File? = null;
    private var stringBase64ImageProfile: String? = ""
    private var observableVal: MutableLiveData<Boolean> = MutableLiveData(false);
    private val retrofitService1 = RetrofitService1.getInstance()
    lateinit var viewModel1: MainViewModel1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = ActivityMain3Binding.inflate(inflater, container, false)
        initializeView()
        setViewModel()
        return dataBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLauncherResult()

    }

    override fun onStart() {
        super.onStart()
    }

    private fun setViewModel() {
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(MainRepository(retrofitService))).get(
                MainViewModel::class.java
            )
        viewModel1 =
            ViewModelProvider(this, MyViewModelFactory1(MainRepository1(retrofitService1))).get(
                MainViewModel1::class.java
            )
    }

    override fun onAttach(context: Context) {
        myContext = context
        super.onAttach(context)

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        someActivityResultLauncher!!.unregister()
    }

    private fun initializeView() {
        dataBinding.lifecycleOwner = this
        try {
            (activity as DashboardActivity).icon()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val bundle = this.arguments
//        val request = bundle?.get("DriverDetails")
//        val res = bundle?.get("LoginRes")


        try {
            savedriver1 = Gson().fromJson(
                Pref_storage.getDetail(requireActivity(), "Driverdetails"),
                savedriver::class.java
            )
            if(savedriver1.en_flag.equals("A")){
                dataBinding.bottomLayout.visibility=View.GONE
                dataBinding.commentsConstraints.visibility=View.GONE
            }

            if(Pref_storage.getDetail(requireActivity(),"idproof").toString().equals("")) {
                dataBinding.proofSpinner.setText(savedriver1.id_Proof_Name)
            }else {
                dataBinding!!.proofSpinner.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "idproof"
                        )
                    )

            }
             if (Pref_storage.getDetail(requireActivity(), "driverpdf").toString().equals("")) {
                 dataBinding.pdffileTxt.text = savedriver1.doc_file_name

            }else {

                    dataBinding!!.pdffileTxt.setText(
                        Pref_storage.getDetail(
                            requireActivity(),
                            "driverpdf"
                        )
                    )

             }
            if(savedriver1.comments!="") {
                dataBinding!!.commentstext.visibility=View.VISIBLE
                dataBinding!!.commentstext.text = "Comments : " + savedriver1.comments
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


        dataBinding.finish.setOnClickListener {
            Log.e("postdata", "" + stringBase64ImageProfile.toString())
            Log.e("postdata", "" + savedriver1.toString())
            if (dataBinding.editTextComments.text.isNotEmpty()) {
                if (savedriver1.doc_file_location!!.isNotEmpty() && stringBase64ImageProfile!!.toString()
                        .isEmpty()
                ) {
                    savedriver1.doc_file_location = savedriver1.doc_file_location
                    savedriver1.doc_file_name = savedriver1.doc_file_name
                    savedriver1.en_flag = "A"
                    savedriver1.id_Proof_Name=dataBinding.proofSpinner.text.toString()
                    savedriver1.comments=dataBinding.editTextComments.text.toString()

                } else {
                    savedriver1.doc_data = stringBase64ImageProfile
                    savedriver1.doc_file_name = ""
                    savedriver1.doc_file_location = ""
                    savedriver1.en_flag = "A"
                    savedriver1.id_Proof_Name=dataBinding.proofSpinner.text.toString()
                    savedriver1.comments=dataBinding.editTextComments.text.toString()
                }
                Log.e("postdata", "" + savedriver1.toString())

                dataBinding!!.LoginAvi.show()
                viewModel.postDriverDetails(savedriver1).observe(requireActivity(), Observer {
                    if (it != null) {
                        dataBinding!!.LoginAvi.hide()

                        AwesomeDialog.build(requireActivity())
                            .title("Congratulations", null, R.color.labelrecyclerview)
                            .body(
                                "Driver Details Added Successfully",
                                null,
                                R.color.labelrecyclerview
                            )
                            .icon(R.drawable.user)
                            .onPositive(
                                "Ok",
                                R.drawable.login_border_theme

                            ) {
                                val intent = Intent(context, NavigationActivity::class.java)
                                startActivity(intent)
                                sendsms()
                            }

                    } else {
                        dataBinding!!.LoginAvi.hide()
                        Toast.makeText(
                            context,
                            "Email-ID/Phone Number already exits",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            } else {
                Toast.makeText(
                    context,
                    "Comments Mandatory",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        dataBinding.reject.setOnClickListener {
            Log.e("postdata", "" + stringBase64ImageProfile.toString())
            Log.e("postdata", "" + savedriver1.toString())
            if (dataBinding.editTextComments.text.isNotEmpty()) {
                if (savedriver1.doc_file_location!!.isNotEmpty() && stringBase64ImageProfile!!.toString()
                        .isEmpty()
                ) {
                    savedriver1.doc_file_location = savedriver1.doc_file_location
                    savedriver1.doc_file_name = savedriver1.doc_file_name
                    savedriver1.en_flag = "R"
                    savedriver1.id_Proof_Name=dataBinding.proofSpinner.text.toString()
                    savedriver1.comments=dataBinding.editTextComments.text.toString()

                } else {

                    savedriver1.doc_data = stringBase64ImageProfile
                    savedriver1.doc_file_name = ""
                    savedriver1.doc_file_location = ""
                    savedriver1.en_flag = "R"
                    savedriver1.id_Proof_Name=dataBinding.proofSpinner.text.toString()
                    savedriver1.comments=dataBinding.editTextComments.text.toString()

                }
                Log.e("postdata", "" + savedriver1.toString())

                dataBinding!!.LoginAvi.show()
                viewModel.postDriverDetails(savedriver1).observe(requireActivity(), Observer {
                    if (it != null) {
                        dataBinding!!.LoginAvi.hide()

                        AwesomeDialog.build(requireActivity())
                            .title("Alert!!!", null, R.color.labelrecyclerview)
                            .body("Driver Details Rejected", null, R.color.labelrecyclerview)
                            .icon(R.drawable.user)
                            .onPositive(
                                "Ok",
                                R.drawable.login_border_theme

                            ) {
                                val intent = Intent(context, NavigationActivity::class.java)
                                startActivity(intent)
                                sendsmsrejected()

                            }

                    } else {
                        dataBinding!!.LoginAvi.hide()
                        Toast.makeText(
                            context,
                            "Email-ID/Phone Number already exits",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            } else {
                Toast.makeText(
                    context,
                    "Comments Mandatory",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        dataBinding.uploadfile.setOnClickListener {
            //(activity as MainActivity).pickPhotoImage(dataBinding.photoEdittexts)
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            if (someActivityResultLauncher != null) {
                someActivityResultLauncher!!.launch(intent)
            }

        }

        dataBinding.pdffileTxt.setOnClickListener {
            Popuppdf(
                requireActivity(),
                savedriver1.doc_file_location + savedriver1.doc_file_name
            ).showFullImageView()

//            val bundle = Bundle()
//            bundle.putString("pdffile", savedriver1.doc_file_location + savedriver1.doc_file_name)
//            val intent = Intent(requireActivity(), PDFActivity::class.java)
//           intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//            intent.putExtra("Arguments", bundle)
//            startActivity(intent)

//            if (pdfFilePath != null) {
//
//                val target = Intent(Intent.ACTION_VIEW)
//                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
//
//                    val pdfUri = Uri.fromFile(myFilePath)
//
//                    target.setDataAndType(pdfUri, "application/pdf")
//                    target.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    val intent = Intent.createChooser(target, getString(R.string.open_file))
//                    try {
//                        startActivity(intent)
//                    } catch (e: ActivityNotFoundException) {
//
//                    }
//                } else {
//
//                    val contentUri = FileProvider.getUriForFile(
//                        Objects.requireNonNull(requireActivity().applicationContext),
//                        "com.wings.mile.provider", myFilePath!!
//                    )
//                    target.setDataAndType(contentUri, "application/pdf")
//                    target.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    target.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    val intent = Intent.createChooser(target, "Open File")
//                    startActivity(intent)
//
//                }
//            }
        }
        updateproofSpinner()

    }

    fun updateproofSpinner() {
        val list: MutableList<String> = ArrayList()
        list.add("Vehicle Documents")
        list.add("Driver Documents")
        var adapter = ArrayAdapter(requireContext(), R.layout.itempopup, R.id.item, list)
        dataBinding.proofSpinner.setAdapter(adapter)

        dataBinding.proofSpinner.onItemClickListener = object : AdapterView.OnItemClickListener,
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
                Pref_storage.setDetail(requireActivity(),"idproof",dataBinding!!.proofSpinner.text.toString())
            }


        }
    }

    @SuppressLint("Range", "Recycle")
    private fun setLauncherResult() {
        someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {

                try {

                    // Here, no request code
                    val data = result.data
                    if (data != null) {

                        var displayName: String? = "";

                        // Get the Uri of the selected file
                        val uri: Uri = result.data!!.data!!
                        val inputStream = requireActivity()!!.contentResolver.openInputStream(uri!!)
                        val pdfInBytes = ByteArray(inputStream!!.available())
                        Log.e("bbbbbbbbb",""+pdfInBytes.size)
                        Log.e("bbbbbbbbb",""+pdfInBytes.size / 1024)
//                        val fileSizeInKB: Int = pdfInBytes.size / 1024
//                        if (fileSizeInKB > 20 || fileSizeInKB < 12) {
//                            showAlertDialog()
//                            stringBase64ImageProfile = null
//                        } else {
                        val uriString: String = uri.toString()
                        myFilePath = File(uriString)
                            val file_size: Int = java.lang.String.valueOf(myFilePath!!.length() / 1024).toInt()
                            Log.e("bbbbbbbbb",""+file_size)

                            pdfFilePath = myFilePath!!.absolutePath
                        getPDFPath(uri)
                        if (uriString.startsWith("content://")) {
                            var cursor: Cursor? = null
                            try {
                                cursor = requireActivity().contentResolver
                                    .query(uri, null, null, null, null)
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayName =
                                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                                    dataBinding.pdffileTxt.text = displayName
                                    dataBinding.pdffileTxt.visibility = View.VISIBLE
                                    dataBinding.pdffileTxt.setTextColor(Color.BLACK)
                                }
                            } finally {
                                cursor!!.close()
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = myFilePath!!.name

                            dataBinding.pdffileTxt.text = displayName
                            dataBinding.pdffileTxt.visibility = View.VISIBLE
                            dataBinding.pdffileTxt.setTextColor(Color.BLACK)

                        }

                    }
                    //}
                } catch (e: Exception) {
                    Log.e("check", "issue Message -----> " + e.localizedMessage);
                    e.printStackTrace()
                }

            }
        }
    }

    fun getPDFPath(uri: Uri?): String? {
        var absolutePath = ""
        try {
            val inputStream = requireActivity()!!.contentResolver.openInputStream(uri!!)
            val pdfInBytes = ByteArray(inputStream!!.available())
            Log.e("bbbbbbbbb",""+pdfInBytes.size)
            Log.e("bbbbbbbbb",""+pdfInBytes.size / 1024)

            inputStream.read(pdfInBytes)
            stringBase64ImageProfile = Base64.encodeToString(pdfInBytes, Base64.DEFAULT)
            Log.e("postdata", "" + stringBase64ImageProfile.toString())

            var offset = 0
            var numRead = 0
            while (offset < pdfInBytes.size && inputStream.read(
                    pdfInBytes,
                    offset,
                    pdfInBytes.size - offset
                ).also {
                    numRead = it
                } >= 0
            ) {
                offset += numRead
            }
            var mPath = ""
            mPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                requireActivity()!!.getExternalFilesDir(Environment.DIRECTORY_DCIM)
                    .toString() + "/" + Calendar.getInstance().time + ".pdf"
            } else {
                Environment.getExternalStorageDirectory()
                    .toString() + "/" + Calendar.getInstance().time + ".pdf"
            }
            val pdfFile = File(mPath)
            val op: OutputStream = FileOutputStream(pdfFile)
            op.write(pdfInBytes)
            absolutePath = pdfFile.path
        } catch (ae: java.lang.Exception) {
            ae.printStackTrace()
        }
        return absolutePath
    }
        /*Exit Application alert functionality*/
        private fun showAlertDialog() {
            AlertDialog.Builder(requireActivity())
                .setTitle("Alert")
                .setMessage(getString(R.string.img_size_alert)) // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes) { dialog: DialogInterface?, which: Int ->
                    dialog!!.dismiss()

                } // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no) { dialog: DialogInterface, which: Int ->
                    // Continue with delete operation
                    dialog.dismiss()
                }
                .show()
        }

    fun sendsms() {
        message = "https://www.smsgatewayhub.com/api/mt/SendSMS?APIKey=7n425U10ZEyLf0BqP3Zzrw&" +
                "senderid=AFARPT&channel=2&DCS=0&flashsms=0&number=91" + savedriver1.phone_number +
                "&text=Your application has been verified and approved by the AFAR Cab team.Begin your journey by logging in with your registered mobile number" + "&route=1&dlttemplateid=1007654192644988416"


        viewModel1!!.updateuser(message!!).observe(requireActivity()) {
            it.let { resource ->
                when (resource!!.status) {
                    com.wings.mile.Utils.Status.LOADING -> {
                    }
                    com.wings.mile.Utils.Status.SUCCESS -> {
                        Log.e("data", "" + it!!.data.toString())


                    }
                    com.wings.mile.Utils.Status.ERROR -> {
                        if (it!!.data == null) {

                        }

                        // binding!!.LoginAvi.hide()

                    }
                }
            }
        }
    }
        fun sendsmsrejected() {
            message="https://www.smsgatewayhub.com/api/mt/SendSMS?APIKey=7n425U10ZEyLf0BqP3Zzrw&" +
                    "senderid=AFARPT&channel=2&DCS=0&flashsms=0&number=91"+savedriver1.phone_number+
                    "&text=Your application with AFAR Cabs has been put on hold for the below-mentioned reason  "+dataBinding.editTextComments.text.toString() +". Kindly login and update your profile with the necessary details.&route=1&dlttemplateid=1007694432375728006"


            viewModel1!!.updateuser(message!!).observe(requireActivity()) {
                it.let { resource ->
                    when (resource!!.status) {
                        com.wings.mile.Utils.Status.LOADING -> {
                        }
                        com.wings.mile.Utils.Status.SUCCESS -> {
                            Log.e("data", "" + it!!.data.toString())


                        }
                        com.wings.mile.Utils.Status.ERROR -> {
                            if (it!!.data == null) {

                            }

                            // binding!!.LoginAvi.hide()

                        }
                    }
                }
            }
    }
}
