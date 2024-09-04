package com.neatplex.nightell.data.dto

data class OtpVerifyRequest (
    val email: String,
    val otp: String
)