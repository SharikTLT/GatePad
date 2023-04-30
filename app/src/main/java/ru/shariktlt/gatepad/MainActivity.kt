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
        storeManager = IStoreManager.getInstance(applicationContext)
        gateApiClient = GateApiClient.getInstance(applicationContext)
        if(storeManager!!.getJWT().isEmpty()){
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }else{
            val intent = Intent(this, SetupActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        private val TAG = (MainActivity::class.qualifiedName)!!
    }
}