package ru.shariktlt.gatepad.auto

import android.graphics.drawable.Icon
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.core.graphics.drawable.IconCompat
import ru.shariktlt.gatepad.R
import ru.shariktlt.gatepad.RelayData
import ru.shariktlt.gatepad.RelayService

class MainScreen(carContext: CarContext) : Screen(carContext) {
    private val relayService: RelayService

    private val pads = ArrayList<RelayData>()

    init {
        relayService = RelayService(carContext)
        relayService.loadRelays(reloadPads())
        relayService.addListener(reloadPads())
    }

    private fun reloadPads(): (t: List<RelayData>) -> Unit =
        {
            pads.clear()
            Log.i(TAG, "reloaded pads ${it}")
            pads.addAll(
                it.filter({ it.isActive })
                    .sortedWith(compareBy({ !it.isActive }, { it.order * -1 }))

            )
        }


    override fun onGetTemplate(): Template {
        if (pads.isEmpty()) {
            val row =
                Row.Builder().setTitle("Требуется настройка. См. приложение на телефоне").build()
            val pane = Pane.Builder().addRow(row).build()
            return PaneTemplate.Builder(pane)
                .setHeaderAction(Action.APP_ICON)
                .build()
        }

        val listBuilder = ItemList.Builder()

        pads.forEach {
            val iconId = if (it.icon == -1) R.drawable.boom_gate_up_icon else it.icon
            val icon = CarIcon.Builder(IconCompat.createWithResource(carContext, iconId)).build()

            listBuilder.addItem(
                GridItem.Builder()
                    .setTitle(it.titleUser)
                    .setImage(icon)
                    .setOnClickListener {
                        Log.i(TAG, "onClick ${it.titleUser}")
                        relayService.unlock(it, {
                            CarToast.makeText(
                                carContext,
                                "Открыт ${it.titleUser}",
                                CarToast.LENGTH_SHORT
                            ).show()
                        }, {
                            CarToast.makeText(
                                carContext,
                                "Ошибка ${it.titleUser}",
                                CarToast.LENGTH_SHORT
                            ).show()
                        })

                    }
                    .build()
            )
        }



        return GridTemplate.Builder()
            .setTitle("Шлагбаумы")
            .setHeaderAction(Action.APP_ICON)
            .setSingleList(listBuilder.build())
            .build()


    }

    companion object {
        private val TAG = (MainScreen::class.qualifiedName)!!
    }
}