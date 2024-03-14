package com.neatplex.nightell.util

import androidx.navigation.NavController
import com.google.gson.Gson

//convert string to object
fun <A> String.fromJson(type: Class<A>) : A{
    return Gson().fromJson(this,type)
}

//convert object to string
fun <A> A.toJson() : String?{
    return Gson().toJson(this)
}
