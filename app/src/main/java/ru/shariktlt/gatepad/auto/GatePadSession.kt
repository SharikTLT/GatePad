package ru.shariktlt.gatepad.auto

import android.content.Intent
import android.util.Log
import androidx.car.app.Screen
import androidx.car.app.Session

class GatePadSession : Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        Log.i(TAG, "onCreateScreen")
        return MainScreen(carContext)
    }

    override fun onNewIntent(intent: Intent) {
        Log.i(TAG, "onNewIntent")
        super.onNewIntent(intent)
    }

    companion object {
        private val TAG = (GatePadSession::class.qualifiedName)!!
    }
}