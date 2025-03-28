package fr.core.projet

import android.content.SharedPreferences
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import fr.core.projet.game.Game

class MainActivity : AppCompatActivity(), Game.ScoreListener {

    private lateinit var game: Game
    private lateinit var sensorManager: CustomSensorManager
    private lateinit var scoreTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var pauseButton: AppCompatImageButton
    private lateinit var soundPool: SoundPool
    private var wasPaused = false
    private val handler = Handler(Looper.getMainLooper())
    private var soundIdCollision = 0
    private var soundIdScore = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        initializeUI()
        initializeGame()
        initializeSounds()

        game.start()
    }

    private fun initializeUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        scoreTextView = findViewById(R.id.scoreTextView)
        pauseButton = findViewById(R.id.pauseButton)
        pauseButton.setOnClickListener {
            game.pause()
            showPauseDialog()
        }
    }

    private fun initializeGame() {
        wasPaused = false
        game = findViewById(R.id.surfaceView)
        sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)

        val selectedSkin = intent.getIntExtra(MenuActivity.EXTRA_SELECTED_SKIN, Color.RED)
        game.setPlayerSkin(selectedSkin);
        game.setScoreListener(this)

        sensorManager = CustomSensorManager(this, game)
    }

    private fun initializeSounds() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            soundPool = SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            @Suppress("DEPRECATION")
            soundPool = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
        }
        soundIdCollision = soundPool.load(this, R.raw.sword, 1)
        soundIdScore = soundPool.load(this, R.raw.woosh, 1)
    }

    private fun showPauseDialog() {
        if (supportFragmentManager.findFragmentByTag("PauseDialog") == null) {
            val pauseDialog = PauseDialogFragment {
                game.resume()
            }
            pauseDialog.show(supportFragmentManager, "PauseDialog")
        }
    }

    override fun onScoreUpdated(score: Int) {
        runOnUiThread {
            val oldScore = scoreTextView.text.toString().replace("Score: ", "").toInt()
            updateScoreDisplay(score)
            if (score > 0 && score % 100 == 0 && score > oldScore) {
                soundPool.play(soundIdScore, 1f, 1f, 0, 0, 1f)
                Toast.makeText(this, "Nouvelle vague !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateScoreDisplay(newScore: Int) {
        val currentScore = scoreTextView.text.toString().replace("Score: ", "").toInt()
        if (newScore > currentScore) {
            // Animation pour augmentation de score
            val scaleUp = android.animation.AnimatorSet().apply {
                playTogether(
                    android.animation.ObjectAnimator.ofFloat(scoreTextView, "scaleX", 1f, 1.2f, 1f),
                    android.animation.ObjectAnimator.ofFloat(scoreTextView, "scaleY", 1f, 1.2f, 1f)
                )
                duration = 300
            }
            scaleUp.start()
        }
        scoreTextView.text = "Score: $newScore"
    }

    override fun onGameOver(finalScore: Int) {
        runOnUiThread {
            val bestScore = sharedPreferences.getInt("bestScore", 0)
            val gamesPlayed = sharedPreferences.getInt("gamesPlayed", 0) + 1
            val isNewBestScore = finalScore > bestScore

            val editor = sharedPreferences.edit()
            if (isNewBestScore) {
                editor.putInt("bestScore", finalScore)
            }
            editor.putInt("gamesPlayed", gamesPlayed)
            editor.putInt("lastScore", finalScore)
            editor.apply()

            // Effet de vibration amélioré
            val vibrator = getSystemService(VIBRATOR_SERVICE) as android.os.Vibrator
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Motif de vibration plus distinctif pour la fin de partie
                vibrator.vibrate(android.os.VibrationEffect.createWaveform(
                    longArrayOf(0, 100, 100, 100, 100, 200),
                    intArrayOf(0, 150, 0, 150, 0, 200),
                    -1
                ))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }

            soundPool.play(soundIdCollision, 1f, 1f, 0, 0, 1f)

            // Utilisation du nouveau DialogFragment
            val gameOverDialog = GameOverDialogFragment(
                finalScore = finalScore,
                bestScore = bestScore,
                gamesPlayed = gamesPlayed,
                isNewBestScore = isNewBestScore
            ) {
                // Action pour le bouton "Réessayer"
                game.start()
            }

            gameOverDialog.isCancelable = false
            gameOverDialog.show(supportFragmentManager, "GameOverDialog")
        }
    }

    override fun onPause() {
        sensorManager.onPause()
        game.pause()
        wasPaused = true
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.onResume()

        if (wasPaused && !isFinishing) {
            handler.postDelayed({
                showPauseDialog()
                wasPaused = false
            }, 300)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        game.pause()
        showPauseDialog()
        return true
    }

    override fun onBackPressed() {
        // Si un dialogue de fin de partie est affiché, ne rien faire
        if (supportFragmentManager.findFragmentByTag("GameOverDialog") != null) {
            return
        }

        // Si le jeu est en cours, mettre en pause
        if (game.isRunning()) {
            game.pause()
            showPauseDialog()
        }
        // Si déjà en pause, reprendre le jeu
        else if (supportFragmentManager.findFragmentByTag("PauseDialog") != null) {
            supportFragmentManager.findFragmentByTag("PauseDialog")?.let {
                (it as PauseDialogFragment).dismiss()
                game.resume()
            }
        }
        // Sinon comportement normal (ne devrait pas arriver)
        else {
            super.onBackPressed()
        }
    }
}