package ru.shariktlt.gatepad.auto

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.*
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
            listBuilder.addItem(
                GridItem.Builder()
                    .setTitle(it.titleUser)
                    .setImage(CarIcon.APP_ICON)
                    .setOnClickListener {
                        Log.i(TAG, "onClick ${it.titleUser}")
                        CarToast.makeText(
                            carContext,
                            "Нажали на ${it.titleUser}",
                            CarToast.LENGTH_SHORT
                        ).show()
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