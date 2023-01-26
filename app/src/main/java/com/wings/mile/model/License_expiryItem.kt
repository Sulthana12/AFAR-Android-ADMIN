package com.wings.mile.model

data class license_expiryItem(
    val email_id: String,
    val expiry_date: String,
    val first_name: String,
    val last_name: String,
    val license_plate_no: String,
    val msg: String,
    val notification_token: String,
    val phone_num: String,
    val user_id: Int,
    val flag: String
)