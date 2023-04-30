package ru.shariktlt.gatepad.api

data class SignInResponse(val account: Account?, val error: Boolean?, val code: String?, val description: String?)

data class Account(
    val id: Long?,
    val appartment_id: Long?,
    val number: String?,
    val first_name: String?,
    val middle_name: String?,
    val last_name: String?,
    val phone: String?,
    val email: String?
)