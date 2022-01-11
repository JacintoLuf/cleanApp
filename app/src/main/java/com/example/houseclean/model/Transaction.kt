package com.example.houseclean.model

import java.io.Serializable
import java.util.*

data class Transaction(
    val transactionID: String? = null,
    val clientID: String? = null,
    var cleanerID: String? = null,
    var clientName: String? = null,
    var house: House? = null,
    var startDate: Date? = null,
    var limitDate: Date? = null,
    //cleaned but not completed
    var status: String? = null,
    //completed after read qr code
    var completed: Boolean? = false
) : Serializable
{

}