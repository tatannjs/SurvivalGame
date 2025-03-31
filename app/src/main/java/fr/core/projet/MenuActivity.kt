package fr.core.projet

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fr.core.projet.game.Bille

/**
 * Activité principale du menu du jeu.
 *
 * Gère l'interface utilisateur du menu principal, incluant l'affichage du score,
 * les boutons de navigation, la prévisualisation et le changement de skin du joueur.
 */
class MenuActivity : AppCompatActivity() {

    /**
     * Contient les constantes utilisées dans cette activité.
     */
    companion object {
        /** Clé pour transmettre le skin sélectionné à l'activité du jeu */
        const val EXTRA_SELECTED_SKIN = "selected_skin"
    }

    /** Score actuel du joueur */
    private var score: Int = 0

    /** Meilleur score enregistré */
    private var bestScore: Int = 0

    /** TextView affichant le score */
    private lateinit var scoreTextView: TextView

    /** Bouton pour lancer le jeu */
    private lateinit var startGameButton: Button

    /** Bouton pour afficher les crédits */
    private lateinit var creditButton: Button

    /** Bouton pour afficher les règles */
    private lateinit var rulesButton: Button

    /** Bouton pour changer le skin de la bille */
    private lateinit var changeSkinButton: Button

    /** Surface pour afficher l'aperçu de la bille */
    private lateinit var billeSurfaceView: SurfaceView

    /** Instance de la bille pour prévisualisation */
    private lateinit var bille: Bille

    /** Holder pour dessiner sur la surface */
    private lateinit var surfaceHolder: SurfaceHolder

    /** Liste des couleurs disponibles pour la bille */
    private val skins = listOf(Color.RED, Color.GREEN, Color.BLUE)

    /** Index de la couleur actuellement sélectionnée */
    private var currentSkinIndex = 0

    /** Préférences partagées pour stocker les données persistantes */
    private lateinit var sharedPreferences: SharedPreferences

    /**
     * Initialise l'activité du menu.
     * Configure l'interface utilisateur, les écouteurs d'événements et récupère le meilleur score.
     *
     * @param savedInstanceState État précédemment sauvegardé, s'il existe
     */
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
            /**
             * Appelé lorsque la surface est créée.
             * Dessine la prévisualisation de la bille.
             *
             * @param holder Le SurfaceHolder dont la surface a été créée
             */
            override fun surfaceCreated(holder: SurfaceHolder) {
                drawBille()
            }

            /**
             * Appelé lorsque la surface change.
             * Met à jour la prévisualisation de la bille.
             *
             * @param holder Le SurfaceHolder dont la surface a changé
             * @param format Le nouveau format PixelFormat de la surface
             * @param width La nouvelle largeur de la surface
             * @param height La nouvelle hauteur de la surface
             */
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                drawBille()
            }

            /**
             * Appelé lorsque la surface est détruite.
             *
             * @param holder Le SurfaceHolder dont la surface a été détruite
             */
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

    /**
     * Met à jour l'affichage du meilleur score.
     */
    private fun updateBestScoreDisplay() {
        scoreTextView.text = "Meilleur score: $bestScore"
    }

    /**
     * Dessine la bille sur la SurfaceView pour prévisualisation.
     * Positionne la bille au centre de la surface.
     */
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

    /**
     * Change le skin de la bille à la couleur suivante dans la liste.
     */
    private fun changeSkin() {
        currentSkinIndex = (currentSkinIndex + 1) % skins.size
        bille.setColor(Paint().apply { color = skins[currentSkinIndex] })
        drawBille()
    }

    /**
     * Affiche la boîte de dialogue des règles du jeu.
     */
    private fun viewRules() {
        RulesDialogFragment().show(supportFragmentManager, "RulesDialog")
    }

    /**
     * Affiche la boîte de dialogue des crédits.
     */
    private fun credit() {
        CreditsDialogFragment().show(supportFragmentManager, "CreditsDialog")
    }

    /**
     * Démarre l'activité du jeu avec le skin sélectionné.
     */
    private fun startGame() {
        score = 0
        scoreTextView.text = "Score: $score"

        try {
            // Utiliser un Toast simple sans personnalisation
            val message = "Starting game..."
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Logger l'erreur mais continuer l'exécution
            Log.e("MenuActivity", "Erreur lors de l'affichage du Toast: ${e.message}")
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(EXTRA_SELECTED_SKIN, skins[currentSkinIndex])
        startActivity(intent)
    }

    /**
     * Appelé lorsque l'activité devient visible à l'utilisateur.
     * Rafraîchit le meilleur score à chaque retour sur le menu.
     */
    override fun onResume() {
        super.onResume()
        // Rafraîchir le meilleur score à chaque retour sur le menu
        bestScore = sharedPreferences.getInt("bestScore", 0)
        updateBestScoreDisplay()
    }
}