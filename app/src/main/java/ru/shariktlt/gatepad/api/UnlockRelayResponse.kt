package ru.shariktlt.gatepad.api

data class UnlockRelayResponse(
    val command: String,
    val target: TargetInfo
)

data class TargetInfo(
    val device: DeviceInfo,
    val address: AddressInfo
)

data class DeviceInfo(
    val vendor_id: String?,
    val product_id: String?,
    val manufacturer_id: String?
)

data class AddressInfo(
    val ip: String?
)
