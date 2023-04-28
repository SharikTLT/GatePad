package ru.shariktlt.gatepad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ru.shariktlt.gatepad.auto.GatePadService
import ru.shariktlt.gatepad.auto.GatePadSession

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        setContentView(R.layout.activity_main)
    }

    companion object {
        private val TAG = (MainActivity::class.qualifiedName)!!
    }
}