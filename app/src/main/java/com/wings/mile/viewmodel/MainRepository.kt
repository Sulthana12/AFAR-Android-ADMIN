package com.wings.mile.viewmodel

import com.wings.mile.model.SignUpRequest
import com.wings.mile.model.savedriver
import com.wings.mile.service.RetrofitService

class MainRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun getAll() = retrofitService.getAllPeople()

    suspend fun getLoginRequest(phonenumber: String, password: String) =
        retrofitService.getLogin(phonenumber, password)

    suspend fun getupdateprofile(userid: Int) = retrofitService.getupdateprofile(userid)


    suspend fun getUserdetails(phonenumber: String) = retrofitService.getdriverdetails(phonenumber)
    suspend fun getdriverlicenseexpirydetails(userid: Int) =
        retrofitService.getdriverlicenseexpirydetails(userid)

    suspend fun getdriverinsuranceexpirydetails(userid: Int) =
        retrofitService.getdriverinsuranceexpirydetails(userid)

    suspend fun getdriveraccountdetails(userid: Int) =
        retrofitService.getdriveraccountdetails(userid)

    suspend fun sendSignUpRequest(request: SignUpRequest) = retrofitService.postSignUp(request)

    suspend fun fetchGender() = retrofitService.getGender("Gender")

    suspend fun fetchVehicle() = retrofitService.getVehicle()

    suspend fun fetchState() = retrofitService.getState()

    suspend fun fetchCountry() = retrofitService.getCountryDetails()

    suspend fun fetchDistrict(stateid: String, countryid: String) =
        retrofitService.getDistrict(stateid, countryid)

    suspend fun saveDriverDetails(request: savedriver) = retrofitService.postDriverDetails(request)

    suspend fun validateuser(phonenumber: String) = retrofitService.validateuser(phonenumber)

    suspend fun updateuser(userid: Int) = retrofitService.updateeuser(userid)
}