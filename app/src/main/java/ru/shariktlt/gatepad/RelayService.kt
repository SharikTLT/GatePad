package ru.shariktlt.gatepad

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ru.shariktlt.gatepad.api.GateApiClient
import ru.shariktlt.gatepad.api.PersonalItercomRelays
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer

typealias RelaysListener = Consumer<List<RelayData>>

class RelayService(val context: Context) {
    private var storageManager: IStoreManager = IStoreManager.getInstance(context)
    private var api: GateApiClient = GateApiClient.getInstance(context)
    private val mapper = jacksonObjectMapper()
    private val cache = ArrayList<RelayData>()
    private val listeners = ArrayList<RelaysListener>()


    fun loadRelays(success: Consumer<List<RelayData>>) {
        val shortcutJson = storageManager.getShortcut()
        if (shortcutJson.isEmpty()) {
            forceReload(success)
        } else {
            val readed =
                mapper.readValue(shortcutJson, object : TypeReference<List<RelayData>>() {})
            cache.clear()
            cache.addAll(readed)
            success.accept(cache)
            triggerListeners()
        }
    }

    fun unlock(relayData: RelayData, success: Runnable, fail: Runnable) {
        api.unlock(relayData.id,
            {
                success.run()
            },
            {
                fail.run()
            })
    }

    fun saveCache(): List<RelayData> {
        storageManager.setShortcut(mapper.writeValueAsString(cache))
        triggerListeners()
        return cache
    }

    private fun triggerListeners() {
        listeners.forEach {
            it.accept(cache)
        }
    }

    fun forceReload(success: Consumer<List<RelayData>>) {
        api.getRelays(
            {
                val relayData = filter(it)
                cache.clear()
                cache.addAll(relayData)
                saveCache()
                storageManager.setOrigin(mapper.writeValueAsString(it))
                success.accept(cache)
            },
            {

            })
    }

    private fun filter(personalItercomRelays: List<PersonalItercomRelays>): List<RelayData> {
        val list = ArrayList<RelayData>()
        personalItercomRelays.forEach {
            if (it.relays.isNotEmpty()) {
                var relayData = RelayData(
                    id = it.relays[0].id,
                    titleOrigin = it.relays[0].name,
                    titleUser = it.name,
                    isActive = true,
                    icon = -1
                )
                list.add(relayData)
            }
            if (it.relays.size > 1) {
                Log.e(TAG, "Found more than one relay in response ${it}")
            }
        }
        return list
    }

    fun getFromCacheById(relayId: Long): RelayData {
        return cache.first { it.id == relayId }
    }

    fun addListener(listener: RelaysListener) {
        listeners.add(listener)
        listener.accept(cache)
    }

    companion object {
        private val instance = AtomicReference<RelayService>()
        fun getInstance(context: Context): RelayService {
            if (instance.get() == null) {
                synchronized(instance) {
                    if (instance.get() == null) {
                        instance.set(RelayService(context))
                    }
                }
            }
            return instance.get()
        }

        private val TAG = (RelayService::class.qualifiedName)!!
    }
}