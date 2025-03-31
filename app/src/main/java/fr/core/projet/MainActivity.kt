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

/**
 * Activité principale du jeu qui gère l'interface utilisateur, les interactions,
 * les sons et les événements de jeu.
 *
 * Cette classe implémente [Game.ScoreListener] pour recevoir les mises à jour du score
 * et les événements de fin de partie.
 */
class MainActivity : AppCompatActivity(), Game.ScoreListener {

    /** Instance du jeu associée à cette activité */
    private lateinit var game: Game

    /** Gestionnaire de capteurs personnalisé pour contrôler le jeu */
    private lateinit var sensorManager: CustomSensorManager

    /** TextView affichant le score actuel */
    private lateinit var scoreTextView: TextView

    /** Préférences partagées pour stocker les scores et statistiques */
    private lateinit var sharedPreferences: SharedPreferences

    /** Bouton de pause */
    private lateinit var pauseButton: AppCompatImageButton

    /** Pool de sons pour les effets sonores du jeu */
    private lateinit var soundPool: SoundPool

    /** Indique si le jeu était en pause avant la mise en arrière-plan de l'activité */
    private var wasPaused = false

    /** Handler pour exécuter des tâches sur le thread principal */
    private val handler = Handler(Looper.getMainLooper())

    /** ID du son de collision */
    private var soundIdCollision = 0

    /** ID du son joué lors de l'augmentation du score */
    private var soundIdScore = 0

    /**
     * Initialise l'activité et configure tous les éléments nécessaires au jeu.
     *
     * @param savedInstanceState État de l'instance précédemment enregistré
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        initializeUI()
        initializeGame()
        initializeSounds()

        game.start()
    }

    /**
     * Initialise les éléments de l'interface utilisateur et configure les listeners.
     */
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

    /**
     * Initialise le jeu et configure les paramètres initiaux.
     */
    private fun initializeGame() {
        wasPaused = false
        game = findViewById(R.id.surfaceView)
        sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)

        val selectedSkin = intent.getIntExtra(MenuActivity.EXTRA_SELECTED_SKIN, Color.RED)
        game.setPlayerSkin(selectedSkin);
        game.setScoreListener(this)

        sensorManager = CustomSensorManager(this, game)
    }

    /**
     * Initialise le pool de sons et charge les ressources audio.
     */
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

    /**
     * Affiche la boîte de dialogue de pause.
     */
    private fun showPauseDialog() {
        if (supportFragmentManager.findFragmentByTag("PauseDialog") == null) {
            val pauseDialog = PauseDialogFragment {
                game.resume()
            }
            pauseDialog.show(supportFragmentManager, "PauseDialog")
        }
    }

    /**
     * Appelé lorsque le score est mis à jour dans le jeu.
     * Met à jour l'affichage et joue des sons à certains seuils de score.
     *
     * @param score Le nouveau score
     */
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

    /**
     * Met à jour l'affichage du score avec une animation.
     *
     * @param newScore Le nouveau score à afficher
     */
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

    /**
     * Appelé lorsque la partie est terminée.
     * Enregistre les statistiques, joue des effets et affiche la boîte de dialogue de fin de partie.
     *
     * @param finalScore Le score final du joueur
     */
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    /**
     * Appelé lorsque l'activité est mise en pause.
     * Met en pause le jeu et les capteurs.
     */
    override fun onPause() {
        sensorManager.onPause()
        game.pause()
        wasPaused = true
        super.onPause()
    }

    /**
     * Appelé lorsque l'activité reprend.
     * Réactive les capteurs et affiche la boîte de dialogue de pause si nécessaire.
     */
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

    /**
     * Gère la navigation vers le haut (bouton retour de l'ActionBar).
     * Met le jeu en pause et affiche le dialogue de pause.
     *
     * @return Toujours true pour indiquer que l'événement a été traité
     */
    override fun onSupportNavigateUp(): Boolean {
        game.pause()
        showPauseDialog()
        return true
    }

    /**
     * Gère l'appui sur le bouton retour.
     * Le comportement dépend de l'état actuel du jeu.
     */
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