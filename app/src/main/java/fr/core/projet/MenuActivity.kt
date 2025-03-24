package fr.core.projet

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    private var score: Int = 0
    private lateinit var scoreTextView: TextView
    private lateinit var startGameButton: Button
    private lateinit var viewScoreButton: Button
    private lateinit var creditButton: Button
    private lateinit var rulesButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        scoreTextView = findViewById(R.id.scoreTextView)
        startGameButton = findViewById(R.id.startGameButton)
        viewScoreButton = findViewById(R.id.viewScoreButton)
        creditButton = findViewById(R.id.creditButton)
        rulesButton = findViewById(R.id.rulesButton)

        val buttonAnimation = AlphaAnimation(0.2f, 1.0f).apply {
            duration = 500
            repeatCount = 1
            repeatMode = AlphaAnimation.REVERSE
        }

        startGameButton.setOnClickListener {
            it.startAnimation(buttonAnimation)
            startGame()
        }

        viewScoreButton.setOnClickListener {
            it.startAnimation(buttonAnimation)
            viewScore()
        }

        creditButton.setOnClickListener {
            it.startAnimation(buttonAnimation)
            credit()
        }

        rulesButton.setOnClickListener {
            it.startAnimation(buttonAnimation)
            viewRules()
        }
    }

    private fun viewRules() {
        Toast.makeText(this, "Viewing rules", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, RulesActivity::class.java))
    }

    private fun credit() {
        Toast.makeText(this, "Credits", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, CreditsActivity::class.java))
    }

    private fun startGame() {
        score = 0
        scoreTextView.text = "Score: $score"
        Toast.makeText(this, "Starting game...", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun viewScore() {
        scoreTextView.text = "Score: $score"
        Toast.makeText(this, "Current score: $score", Toast.LENGTH_SHORT).show()
    }

}