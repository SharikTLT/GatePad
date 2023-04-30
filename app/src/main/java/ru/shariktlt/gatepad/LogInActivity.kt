package ru.shariktlt.gatepad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.shariktlt.gatepad.api.GateApiClient
import ru.shariktlt.gatepad.databinding.ActivityLogInBinding

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding

    private lateinit var api: GateApiClient
    private lateinit var phoneText: EditText
    private lateinit var passText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        api = GateApiClient.getInstance(applicationContext)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneText = binding.editTextPhone
        passText = binding.editTextTextPassword

        binding.logInBtn.setOnClickListener {
            api.signIn(
                phoneText.text.toString(),
                passText.text.toString(),
                {
                    this.runOnUiThread {
                        if (it.error ?: false) {
                            Log.e(TAG,"Error got ${it}")
                            Toast.makeText(
                                this,
                                "${it.description}",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Log.i(TAG,"Success response ${it}")
                            val intent = Intent(this, SetupActivity::class.java)
                            startActivity(intent)
                        }
                    }

                },
                {
                    this.runOnUiThread{
                        Toast.makeText(this, "Ошибка обращения к API", Toast.LENGTH_SHORT)
                    }
                }
            )
        }

    }


    companion object {
        val TAG = (LogInActivity::class.qualifiedName)!!
    }
}