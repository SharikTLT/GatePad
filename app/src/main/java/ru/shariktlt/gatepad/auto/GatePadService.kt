package ru.shariktlt.gatepad.auto

import android.util.Log
import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

class GatePadService : CarAppService() {
    override fun createHostValidator(): HostValidator {
        Log.i(TAG, "createHostValidator")
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        Log.i(TAG, "onCreateSession")
        return GatePadSession()
    }

    companion object {
        private val TAG = (GatePadService::class.qualifiedName)!!
    }
}