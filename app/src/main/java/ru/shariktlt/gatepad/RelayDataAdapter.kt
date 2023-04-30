package ru.shariktlt.gatepad

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.shariktlt.gatepad.databinding.ItemRelayBinding

class RelayDataAdapter(val setupActivity: SetupActivity) :
    RecyclerView.Adapter<RelayDataAdapter.RelayDataViewHolder>() {

    var data: List<RelayData> = emptyList()
        set(newValue) {
            field = orderData(newValue)
            Log.i(TAG, "new data setted")
            notifyDataSetChanged()
        }

    class RelayDataViewHolder(val binding: ItemRelayBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelayDataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRelayBinding.inflate(inflater, parent, false)

        return RelayDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RelayDataViewHolder, position: Int) {
        val relay = data[position]
        val context = holder.itemView.context

        with(holder.binding) {
            titleOrigView.text = relay.titleOrigin
            titleUserView.text = relay.titleUser
            isActive.setOnCheckedChangeListener(null)
            isActive.isChecked = relay.isActive
            isActive.setOnCheckedChangeListener { button, isChecked ->
                val targetRelay: RelayData = button.tag as RelayData
                targetRelay.isActive = isChecked
                holder.itemView.post {
                    notifyItemChanged(position)
                }
                //Log.i(TAG, "Change relay:\n\t${holder}\n\t${holder.binding}\n${targetRelay}")
            }
            isActive.tag = relay

            orderText.setText(relay.order.toString())

            editBtn.setOnClickListener(null)
            editBtn.setOnClickListener {
                val intent = Intent(context, EditActivity::class.java)
                intent.putExtra("relayId", relay.id)
                intent.putExtra("relayPosition", position)
                setupActivity.resultLauncher.launch(intent)
            }
            editBtn.tag = relay

        }
    }


    fun orderData(items: List<RelayData>): List<RelayData> {
        return items.sortedWith(compareBy({ !it.isActive }, { it.order * -1 }))
    }

    companion object {
        private val TAG = (RelayDataAdapter::class.qualifiedName)!!
    }
}