package ru.shariktlt.gatepad.api

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.*
import ru.shariktlt.gatepad.IStoreManager
import ru.shariktlt.gatepad.StorageManagerInitializer
import ru.shariktlt.gatepad.api.GateApiClient.Companion.getInstance
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer


class GateApiClient(val uid: String, val storeManager: IStoreManager) {

    init {
        Log.i(TAG, "Initialized with UID = $uid")
    }

    private val client = OkHttpClient()
    private val objectMapper =
        jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun signIn(
        phone: String,
        pass: String,
        success: Consumer<SignInResponse>,
        fail: Consumer<IOException>
    ) {
        val postBody = FormBody.Builder()
            .add("account[phone]", phone)
            .add("account[password]", pass)
            .add("customer_device[uid]", uid)
            .build()


        val request = Request.Builder()
            .url(pikUrl("/api/customers/sign_in"))
            .method("POST", postBody)
            .headers(buildHeaders())
            .build()
        val newCall = client.newCall(request)
        makeAsyncRequest(newCall, fail, success, object : TypeReference<SignInResponse>() {})
    }

    fun getRelays(
        success: Consumer<List<PersonalItercomRelays>>,
        fail: Consumer<IOException>
    ) {

        val request = Request.Builder()
            .url(rubetekUrl("/api/alfred/v1/personal/intercoms"))
            .headers(buildHeaders())
            .build()
        val newCall = client.newCall(request)
        makeAsyncRequest(
            newCall,
            fail,
            success,
            object : TypeReference<List<PersonalItercomRelays>>() {})
    }

    fun unlock(
        relayId: Long, success: Consumer<UnlockRelayResponse>,
        fail: Consumer<IOException>
    ) {

        val request = Request.Builder()
            .url(rubetekUrl("/api/alfred/v1/personal/relays/$relayId/unlock"))
            .method("POST", FormBody.Builder().build())
            .headers(buildHeaders())
            .build()
        val newCall = client.newCall(request)
        makeAsyncRequest(
            newCall,
            fail,
            success,
            object : TypeReference<UnlockRelayResponse>() {})
    }

    private fun buildHeaders(): Headers {
        val b = Headers.Builder()
        b.add("API-VERSION", "2")
        b.add("device-client-app", "alfred")
        b.add("device-client-version", "2021.10.2")
        b.add("device-client-os", "Android")
        b.add("device-client-uid", uid)

        val jwt = storeManager.getJWT()
        if (!jwt.isEmpty()) {
            b.add("Authorization", jwt)
        }
        return b.build()
    }

    private fun <T> makeAsyncRequest(
        newCall: Call,
        fail: Consumer<IOException>,
        success: Consumer<T>,
        type: TypeReference<T>
    ) {
        Log.i(TAG, "Prepare request ${newCall.request()}")
        newCall.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "onFailure ${newCall.request()}", e)
                fail.accept(e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i(TAG, "resp ${newCall.request()} with response: ${response}")
                val jwt = response.headers.get("Authorization")
                if (jwt != null) {
                    Log.i(TAG, "Found jwt")
                    storeManager.setJWT(jwt)
                }
                val parsedBody = objectMapper.readValue(response.body!!.string(), type)
                success.accept(parsedBody)
            }
        })
    }

    companion object {
        private val TAG = (GateApiClient::class.qualifiedName)!!
        private val BASE_PIK_URL = "https://intercom.pik-comfort.ru"
        private val BASE_RUBETEK_URL = "https://iot.rubetek.com"

        fun pikUrl(url: String) = "$BASE_PIK_URL$url"
        fun rubetekUrl(url: String) = "$BASE_RUBETEK_URL$url"

        val instance = AtomicReference<GateApiClient>()

        fun getInstance(context: Context): GateApiClient {
            if (instance.get() == null) {
                synchronized(instance) {
                    if (instance.get() == null) {
                        val storeManager = IStoreManager.getInstance(context)
                        instance.set(GateApiClient(storeManager.getUid(), storeManager))
                    }
                }
            }
            return instance.get()
        }
    }
}

class GateApiClientInitializer : Initializer<GateApiClient> {
    override fun create(context: Context): GateApiClient {
        return getInstance(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(StorageManagerInitializer::class.java)
    }
}