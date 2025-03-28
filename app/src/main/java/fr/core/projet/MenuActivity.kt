package fr.core.projet

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.core.projet.game.Bille

class MenuActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SELECTED_SKIN = "selected_skin"
    }

    private var score: Int = 0
    private var bestScore: Int = 0
    private lateinit var scoreTextView: TextView
    private lateinit var startGameButton: Button
    private lateinit var creditButton: Button
    private lateinit var rulesButton: Button
    private lateinit var changeSkinButton: Button
    private lateinit var billeSurfaceView: SurfaceView
    private lateinit var bille: Bille
    private lateinit var surfaceHolder: SurfaceHolder
    private val skins = listOf(Color.RED, Color.GREEN, Color.BLUE)
    private var currentSkinIndex = 0
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Initialiser SharedPreferences
        sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)
        // Récupérer le meilleur score
        bestScore = sharedPreferences.getInt("bestScore", 0)

        scoreTextView = findViewById(R.id.scoreTextView)
        startGameButton = findViewById(R.id.startGameButton)
        creditButton = findViewById(R.id.creditButton)
        rulesButton = findViewById(R.id.rulesButton)
        changeSkinButton = findViewById(R.id.changeSkinButton)
        billeSurfaceView = findViewById(R.id.billeSurfaceView)
        surfaceHolder = billeSurfaceView.holder
        bille = Bille(10)

        updateBestScoreDisplay()

        surfaceHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                drawBille()
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                drawBille()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })

        val buttonAnimation = AlphaAnimation(0.2f, 1.0f).apply {
            duration = 500
            repeatCount = 1
            repeatMode = AlphaAnimation.REVERSE
        }

        startGameButton.setOnClickListener {
            it.startAnimation(buttonAnimation)
            startGame()
        }

        creditButton.setOnClickListener {
            it.startAnimation(buttonAnimation)
            credit()
        }

        rulesButton.setOnClickListener {
            it.startAnimation(buttonAnimation)
            viewRules()
        }

        changeSkinButton.setOnClickListener {
            it.startAnimation(buttonAnimation)
            changeSkin()
        }
    }

    private fun updateBestScoreDisplay() {
        scoreTextView.text = "Meilleur score: $bestScore"
    }

    private fun drawBille() {
        val canvas: Canvas? = surfaceHolder.lockCanvas()
        if (canvas != null) {
            canvas.drawColor(Color.WHITE)
            bille.setX((canvas.width / 2).toFloat())
            bille.setY((canvas.height / 2).toFloat())
            bille.draw(canvas)
            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    private fun changeSkin() {
        currentSkinIndex = (currentSkinIndex + 1) % skins.size
        bille.setColor(Paint().apply { color = skins[currentSkinIndex] })
        drawBille()
    }

    private fun viewRules() {
        RulesDialogFragment().show(supportFragmentManager, "RulesDialog")
    }

    private fun credit() {
        CreditsDialogFragment().show(supportFragmentManager, "CreditsDialog")
    }

    private fun startGame() {
        score = 0
        scoreTextView.text = "Score: $score"
        Toast.makeText(this, "Starting game...", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(EXTRA_SELECTED_SKIN, skins[currentSkinIndex])
        startActivity(intent)
    }

    // Méthode mise à jour pour afficher le score actuel
    private fun viewScore() {
        scoreTextView.text = "Score: $score"
        Toast.makeText(this, "Current score: $score", Toast.LENGTH_SHORT).show()
    }

    // Méthode pour mettre à jour le meilleur score si nécessaire
    override fun onResume() {
        super.onResume()
        // Rafraîchir le meilleur score à chaque retour sur le menu
        bestScore = sharedPreferences.getInt("bestScore", 0)
        updateBestScoreDisplay()
    }

}