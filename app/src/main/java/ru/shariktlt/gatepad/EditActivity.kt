package ru.shariktlt.gatepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ru.shariktlt.gatepad.databinding.ActivityEditBinding
import ru.shariktlt.gatepad.databinding.ActivitySetupBinding

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private lateinit var relayService: RelayService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        relayService = RelayService.getInstance(applicationContext)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val relayId = intent.getLongExtra("relayId", 0)
        if (relayId == 0L) {
            Log.e(TAG, "Got relayId = zero")
            finishAffinity()
            return
        }
        val relay = relayService.getFromCacheById(relayId)

        with(binding) {
            origTitleTextView.text = relay.titleOrigin
            editUserTitleText.setText(relay.titleUser)
            editTextNumber.setText(relay.order.toString())

            editCancelBtn.setOnClickListener {
                setResult(RESULT_CANCELED, intent)
                finish()
            }

            editSaveBtn.setOnClickListener {
                relay.titleUser = editUserTitleText.text.toString()
                relay.order = editTextNumber.text.toString().toLong()
                setResult(RESULT_OK, intent)
                finish()
            }
        }

    }

    companion object {
        val TAG = (EditActivity::class.qualifiedName)!!
    }
}