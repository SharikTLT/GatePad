package ru.shariktlt.gatepad.auto

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.*

class MainScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {

        val listBuilder = ItemList.Builder()
        for(i in 1..6){
            listBuilder.addItem(
                GridItem.Builder()
                    .setTitle("Item $i")
                    .setImage(CarIcon.APP_ICON)
                    .setOnClickListener {
                        Log.i(TAG, "onClick $i")
                        CarToast.makeText(carContext, "Нажали на $i", CarToast.LENGTH_SHORT).show()
                    }
                    .build()
            )
        }

        return GridTemplate.Builder()
            .setTitle("Devices")
            .setHeaderAction(Action.APP_ICON)
            .setSingleList(listBuilder.build())
            .build()


    }

    companion object {
        private val TAG = (MainScreen::class.qualifiedName)!!
    }
}