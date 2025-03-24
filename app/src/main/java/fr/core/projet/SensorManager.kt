package fr.core.projet

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import fr.core.projet.game.Game

class CustomSensorManager(pContext: MainActivity, private var game: Game): SensorEventListener{

    private val context = pContext
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val bille = game.getBille()
            bille.setVx(-event.values[0])
            bille.setVy(event.values[1])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
       // Nothing to do
    }

    fun onResume(){
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    fun onPause(){
        sensorManager.unregisterListener(this)
    }

}