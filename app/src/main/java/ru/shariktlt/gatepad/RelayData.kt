package ru.shariktlt.gatepad

data class RelayData(
    val id: Long,
    val titleOrigin: String,
    var titleUser: String,
    var isActive: Boolean,
    var order: Long = 1,
    var icon: Int = -1
)