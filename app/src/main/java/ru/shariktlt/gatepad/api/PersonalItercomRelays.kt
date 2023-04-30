package ru.shariktlt.gatepad.api

data class PersonalItercomRelays(
    val id: Long,
    val name: String,
    val relays: List<Relay>
)

data class Relay(
    val id: Long,
    val name: String
)
