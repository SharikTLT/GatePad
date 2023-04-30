package ru.shariktlt.gatepad

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.shariktlt.gatepad.databinding.ActivitySetupBinding

class SetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupBinding
    private lateinit var adapter: RelayDataAdapter
    private lateinit var relayService: RelayService
    private lateinit var recyclerView: RecyclerView

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                runOnUiThread {
                    adapter.data = relayService.saveCache()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RelayDataAdapter(this)
        relayService = RelayService.getInstance(applicationContext)
        recyclerView = binding.recyclerView
        val linearManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearManager
        recyclerView.adapter = adapter

        relayService.loadRelays(onRelayDataLoaded())


        binding.forceReload.setOnClickListener {
            Log.i(TAG, "User tapped the Supabutton")

            relayService.forceReload(onRelayDataLoaded())
        }
        binding.saveBtn.setOnClickListener {
            adapter.data = relayService.saveCache()
        }


        Log.i(TAG, "View onCreate")

    }

    private fun onRelayDataLoaded(): (t: List<RelayData>) -> Unit =
        {
            runOnUiThread {
                adapter.data = it
            }
        }

    companion object {
        private val TAG = (SetupActivity::class.qualifiedName)!!
    }
}