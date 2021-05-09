package com.example.buzzer

class citizendata(
    val cid: String?,
    val etfirstName: String?,
    val etlastName: String,
    val etEmail: String,
    val etPno: String,
    val etAdd: String,
    val etCity: String,
    val etState: String,
    val etPassword: String,
    val etBloodGroup : String
){
    constructor():this("","","","","","","","","","")
}