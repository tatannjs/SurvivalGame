package fr.core.projet

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import fr.core.projet.game.Game

/**
 * Gestionnaire personnalisé des capteurs pour contrôler la bille du jeu.
 *
 * Cette classe utilise l'accéléromètre de l'appareil pour détecter les mouvements
 * et les traduire en déplacements de la bille dans le jeu. Elle s'occupe de
 * l'enregistrement et du désenregistrement des écouteurs de capteurs lors des
 * changements de cycle de vie de l'activité.
 *
 * @property game L'instance du jeu à contrôler
 * @property sensorManager Le gestionnaire système des capteurs
 * @property accelerometer Le capteur d'accéléromètre utilisé
 * @property speedMultiplier Facteur multiplicateur pour ajuster la sensibilité des contrôles
 * @constructor Crée un gestionnaire de capteurs associé à une instance de jeu
 * @param context Le contexte Android utilisé pour accéder aux services système
 */
class CustomSensorManager(context: Context, private val game: Game) : SensorEventListener {

    /** Gestionnaire système des capteurs */
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    /** Capteur d'accéléromètre de l'appareil */
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    /** Multiplicateur de vitesse pour le contrôle de la bille */
    private val speedMultiplier = 1.5f

    /**
     * Enregistre l'écouteur pour l'accéléromètre lorsque l'activité reprend.
     * À appeler dans la méthode onResume() de l'activité ou du fragment.
     */
    fun onResume() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    /**
     * Désenregistre l'écouteur pour l'accéléromètre lorsque l'activité est mise en pause.
     * À appeler dans la méthode onPause() de l'activité ou du fragment.
     */
    fun onPause() {
        sensorManager.unregisterListener(this)
    }

    /**
     * Appelé lorsque les valeurs du capteur changent.
     * Convertit les valeurs de l'accéléromètre en mouvement pour la bille.
     *
     * @param event L'événement du capteur contenant les nouvelles valeurs
     */
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = -event.values[0] * speedMultiplier
            val y = event.values[1] * speedMultiplier

            val bille = game.getBille()
            bille.setVx(x)
            bille.setVy(y)
        }
    }

    /**
     * Appelé lorsque la précision du capteur change.
     * Non utilisé dans cette implémentation.
     *
     * @param sensor Le capteur dont la précision a changé
     * @param accuracy Le nouveau niveau de précision
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Ne rien faire
    }
}