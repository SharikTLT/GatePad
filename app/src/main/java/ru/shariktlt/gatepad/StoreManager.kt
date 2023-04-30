package ru.shariktlt.gatepad

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.startup.Initializer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import ru.shariktlt.gatepad.IStoreManager.Companion.getInstance
import java.io.IOException
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference


val Context.apiDataStore: DataStore<Preferences> by preferencesDataStore(name = "apiData")

interface IStoreManager {
    fun getJWT(): String
    fun setJWT(newJwt: String)
    fun setOrigin(newOrigin: String)
    fun setShortcut(newShortcut: String)
    fun getOrigin(): String
    fun getShortcut(): String
    fun getUid(): String

    companion object {
        val inst = AtomicReference<IStoreManager>()

        fun getInstance(context: Context): IStoreManager {
            if (inst.get() == null) {
                synchronized(inst) {
                    if (inst.get() == null) {
                        inst.set(StoreManager(context))
                    }
                }
            }
            return inst.get()
        }
    }
}

class StoreManager(context: Context) : IStoreManager {
    val store: DataStore<Preferences>

    var jwt: AtomicReference<String> = AtomicReference()
    var orig: AtomicReference<String> = AtomicReference()
    var shortcut: AtomicReference<String> = AtomicReference()
    var uid: AtomicReference<String> = AtomicReference()
    private var isReady = CompletableFuture<Boolean>()

    init {
        Log.i(TAG, "Initialize storage")
        store = context.apiDataStore
        val initFlow = store.data
            .catch {
                Log.e(TAG, "Exception during initialization", it)
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                }

            }
            .map {
                jwt.set(it[JWT_KEY] ?: "")
                orig.set(it[RELAYS_ORIG] ?: "")
                shortcut.set(it[RELAYS_SHORTCUT] ?: "")
                uid.set(it[UID] ?: UUID.randomUUID().toString())
                runBlocking {
                    store.edit {
                        it[UID] = uid.get()
                    }
                }
                Log.i(TAG, "Mapped init storage")
                it
            }

        Log.i(TAG, "Before collect")
        runBlocking {
            initFlow.first {
                isReady.complete(true)
                Log.i(TAG, "Init is ready")
                true
            }
        }
        Log.i(TAG, "After collect")
    }


    override fun getJWT(): String {
        waitReady()
        return jwt.get()
    }

    override fun setJWT(newJwt: String) {
        runBlocking {
            store.edit {
                it[JWT_KEY] = newJwt
            }
        }
    }

    override fun setOrigin(newOrigin: String) {
        runBlocking {
            store.edit {
                it[RELAYS_ORIG] = newOrigin
            }
        }
    }

    override fun setShortcut(newShortcut: String) {
        runBlocking {
            store.edit {
                it[RELAYS_SHORTCUT] = newShortcut
            }
        }
    }

    override fun getOrigin(): String {
        waitReady()
        return orig.get()
    }

    override fun getShortcut(): String {
        waitReady()
        return shortcut.get()
    }

    override fun getUid(): String {
        waitReady()
        return uid.get();
    }

    private fun waitReady() {
        if (!isReady.isDone) {
            try {
                Log.i(TAG, "Wait for ready")
                isReady.get(10, TimeUnit.SECONDS)
            } finally {
                Log.i(TAG, "Waiting is over")
            }
        }
    }

    companion object {
        private val JWT_KEY = stringPreferencesKey("pikJwt")
        private val RELAYS_ORIG = stringPreferencesKey("original")
        private val RELAYS_SHORTCUT = stringPreferencesKey("shortcut")
        private val UID = stringPreferencesKey("uid")

        private val TAG = (StoreManager::class.qualifiedName)!!
    }
}

class StorageManagerInitializer : Initializer<IStoreManager> {

    override fun create(context: Context): IStoreManager {
        return getInstance(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

}