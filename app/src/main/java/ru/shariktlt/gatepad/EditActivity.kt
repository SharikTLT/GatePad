package ru.shariktlt.gatepad

import android.graphics.Color
import android.graphics.drawable.Icon
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.IconCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.shariktlt.gatepad.databinding.ActivityEditBinding
import ru.shariktlt.gatepad.databinding.ItemIconBinding
import java.util.concurrent.atomic.AtomicInteger

class EditActivity : AppCompatActivity() {
    private lateinit var iconSelectAdapter: IconSelectAdapter
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
                relay.icon = iconSelectAdapter.selectedIcon.get()
                setResult(RESULT_OK, intent)
                finish()
            }

            val iconArr = getResources().obtainTypedArray(R.array.icons)

            var iconSelectList = ArrayList<IconSelect>()
            iconSelectList.add(IconSelect(R.mipmap.ic_boom_gate, relay.icon == -1))

            for (i in 0..iconArr.length() - 1) {
                val resId = iconArr.getResourceId(i, -1)
                iconSelectList.add(IconSelect(resId, relay.icon == resId))
            }

            iconSelectAdapter = IconSelectAdapter(iconSelectList, relay.icon)
            iconRecyclerView.adapter = iconSelectAdapter
            iconRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        }

    }

    companion object {
        val TAG = (EditActivity::class.qualifiedName)!!
    }
}

class IconSelectAdapter(val data: List<IconSelect>, val initialIcon: Int) :
    RecyclerView.Adapter<IconSelectAdapter.IconSelectViewHolder>() {
    val selectedIcon = AtomicInteger()

    init {
        selectedIcon.set(initialIcon)
    }

    class IconSelectViewHolder(val binding: ItemIconBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconSelectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemIconBinding.inflate(inflater, parent, false)

        return IconSelectAdapter.IconSelectViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size
    override fun onBindViewHolder(holder: IconSelectViewHolder, position: Int) {
        val icon = data[position]
        val context = holder.itemView.context

        with(holder.binding) {

            val iconCompat = IconCompat.createWithResource(context, icon.id)
            imageView.setImageIcon(iconCompat.toIcon(context))
            radioButton.setOnCheckedChangeListener(null)
            radioButton.isChecked = icon.selected
            radioButton.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    selectNew(icon.id)
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun selectNew(id: Int) {
        selectedIcon.set(id)
        data.forEach {
            if (it.id != id && it.selected) {
                it.selected = false
            }
            if (it.id == id) {
                it.selected = true
            }
        }
    }

}