package com.wings.mile.model

class useraccount: ArrayList<User_Accountitem>()
data class User_Accountitem(
    val email_Id: String,
    val expiry_date: String,
    val first_Name: String,
    val last_Name: String,
    val bank_Name: String,
    val branch_Name: String,
    val ifsc_Code: String,
    val phone_Number: String,
    val user_Id: Int,
    val bank_Img_File_Name: String,
    val bank_Img_File_Location: String,
    val account_Number: String,
    val bank_mobile_num: String
)

