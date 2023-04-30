package ru.shariktlt.gatepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ru.shariktlt.gatepad.api.GateApiClient
import ru.shariktlt.gatepad.auto.GatePadService
import ru.shariktlt.gatepad.auto.GatePadSession

class MainActivity : AppCompatActivity() {

    var storeManager: IStoreManager? = null
    var gateApiClient: GateApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        setContentView(R.layout.activity_main)
        storeManager = IStoreManager.getInstance(applicationContext)
        gateApiClient = GateApiClient.getInstance(applicationContext)
        if(storeManager!!.getJWT().isEmpty()){
            val intent = Intent(this, LogInActivity::class.java)
        }
    }

    companion object {
        private val TAG = (MainActivity::class.qualifiedName)!!
    }
}