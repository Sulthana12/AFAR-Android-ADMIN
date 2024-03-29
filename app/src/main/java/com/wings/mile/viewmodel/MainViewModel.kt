package com.wings.mile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.wings.mile.ItemList
import com.wings.mile.Utils.Resource
import com.wings.mile.Utils.Utility
import com.wings.mile.model.*
import kotlinx.coroutines.Dispatchers


class MainViewModel constructor(val repository: MainRepository, application: Application) : AndroidViewModel(application) {

    var songList = MutableLiveData<ItemList>()
    val errorMessage = MutableLiveData<String>()
    var genderlist = gender()
    var vehiclelist = vehicle()
    var countrylist = getCountry()
    var statelist = getstate()
    var districtlist = getDistrictDetails()
    var detailslist = getdetails()

    fun sendLoginRequest(number: String, password: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getLoginRequest(number, password = password)))

//            if (Utility.isConnected(getApplication())) {
//            } else {
//                emit(Resource.error(data = null, message = "Network slow or disconnected"))
//            }
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }


    }
    fun updateprofile(userid: Int) = liveData(Dispatchers.IO) {
        try {
            val response = repository.getupdateprofile(userid)
            emit(response)
        } catch (e: Exception) {
            emit(null)
        }

    }
    fun sendSignupRequestOrProfileUpdate(request: SignUpRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.sendSignUpRequest(request)))

//            if (Utility.isConnected(getApplication())) {
//            } else {
//                emit(Resource.error(data = null, message = "Network slow or disconnected"))
//            }
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }


    }

    fun fetchGenderDetails() = liveData(Dispatchers.IO) {
        val response = repository.fetchGender()
        genderlist.addAll(response)
        emit(response)

    }

    fun fetchVehicleDetails() = liveData(Dispatchers.IO) {
        val response = repository.fetchVehicle()
        vehiclelist.addAll(response)
        emit(response)

    }

    fun fetchStateDetails() = liveData(Dispatchers.IO) {
        val response = repository.fetchState()
        statelist.addAll(response)
        emit(response)

    }


    fun fetchCountryDetails() = liveData(Dispatchers.IO) {
        val response = repository.fetchCountry()
        countrylist.addAll(response)
        emit(response)

    }

    fun fetchDistrictDetails(id: String, id2: String) = liveData(Dispatchers.IO) {
        try {
            val response = repository.fetchDistrict(id, id2)
            districtlist.addAll(response)
            emit(response)
        } catch (e: Exception) {
            emit(null)
        }

    }

    fun postDriverDetails(savedriver: savedriver) = liveData(Dispatchers.IO) {
        try {
            val response = repository.saveDriverDetails(savedriver)
            emit(response)

        } catch (e: Exception) {
            emit(null)
        }
    }


    fun getdriverdetails(phone:String) = liveData(Dispatchers.IO) {

        try {
            val response = repository.getUserdetails(phone)
            emit(response)

        } catch (e: Exception) {
            emit(null)
        }
    }
    fun getdriverlicenseexpirydetails(userid: Int) = liveData(Dispatchers.IO) {

        try {
            val response = repository.getdriverlicenseexpirydetails(userid)
            emit(response)

        } catch (e: Exception) {
            emit(null)
        }
    }
    fun getdriverinsuranceexpirydetails(userid: Int) = liveData(Dispatchers.IO) {

        try {
            val response = repository.getdriverinsuranceexpirydetails(userid)
            emit(response)

        } catch (e: Exception) {
            emit(null)
        }
    }

    fun getdriveraccountdetails(userid: Int) = liveData(Dispatchers.IO) {

        try {
            val response = repository.getdriveraccountdetails(userid)
            emit(response)

        } catch (e: Exception) {
            emit(null)
        }
    }
    fun validate(phone: String) = liveData(Dispatchers.IO) {
        try {
            val response = repository.validateuser(phone)
            emit(response)
        } catch (e: Exception) {
            emit(null)
        }
    }

    fun updateuser(userid: Int) = liveData(Dispatchers.IO) {
        try {
            val response = repository.updateuser(userid)
            emit(response)
        } catch (e: Exception) {
            emit(null)
        }
    }
}

