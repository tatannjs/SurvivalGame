package fr.core.projet

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import fr.core.projet.game.Game

class MainActivity : AppCompatActivity() {

    private lateinit var game: Game
    private lateinit var sensorManager: CustomSensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        game = findViewById(R.id.surfaceView)

        sensorManager = CustomSensorManager(this,game)

        game.start()

    }

    override fun onPause() {
        sensorManager.onPause()
        game.pause()
        showPauseDialog()
        super.onPause()
    }

    override fun onResume() {
        sensorManager.onResume()
        game.resume()
        super.onResume()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        game.pause()
        showPauseDialog()
    }

    private fun showPauseDialog() {
        val pauseDialog = PauseDialogFragment {
            game.resume()
        }
        pauseDialog.show(supportFragmentManager, "PauseDialog")
    }
}