package com.example.houseclean


data class User(
    val UID: String? = null,
    var name: String? = null,
    val email: String? = null,
    var usrPic: String? = null,
    var houses: MutableList<House>? = mutableListOf<House>(),
    var transactions: MutableList<Transaction>? = mutableListOf<Transaction>())
{

}
