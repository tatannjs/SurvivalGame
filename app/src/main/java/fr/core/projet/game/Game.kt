package fr.core.projet.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowInsets
import android.view.WindowManager
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Classe principale du jeu qui gère la logique, le rendu et les interactions.
 *
 * Cette vue personnalisée étend SurfaceView et implémente SurfaceHolder.Callback
 * pour gérer efficacement le rendu graphique. Elle contient la boucle de jeu,
 * la gestion des ennemis, le calcul des scores et la détection des collisions.
 *
 * @param context Le contexte de l'application
 * @param attrs Ensemble d'attributs XML de la vue
 */
class Game(context: Context, attrs: AttributeSet?) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    /** La bille contrôlée par le joueur */
    private var bille: Bille

    /** Liste thread-safe des ennemis actuellement dans le jeu */
    private var enemies = CopyOnWriteArrayList<Enemy>()

    /** État d'exécution du jeu */
    private var isRunning: Boolean = false

    /** Handler pour exécuter la boucle de jeu sur le thread principal */
    private val handler: Handler = Handler(Looper.getMainLooper())

    /** Intervalle entre les mises à jour en millisecondes (environ 60 FPS) */
    private val updateInterval: Long = 16

    /** Largeur de l'écran en pixels */
    private var screenWidth: Int

    /** Hauteur de l'écran en pixels */
    private var screenHeight: Int

    /** Temps de début de la partie en millisecondes */
    private var gameStartTime: Long = 0

    /** Numéro de la vague actuelle */
    private var currentWave: Int = 0

    /** Temps de début de la vague actuelle en millisecondes */
    private var waveStartTime: Long = 0

    /** Durée d'une vague en millisecondes */
    private var waveDuration: Long = 10000 // 10 secondes

    /** Score actuel du joueur */
    private var score: Int = 0

    /** Indicateur de fin de partie */
    private var gameOver: Boolean = false

    /** Écouteur pour notifier des changements de score et de fin de partie */
    private var scoreListener: ScoreListener? = null

    /** Verrou pour synchroniser les mises à jour du jeu */
    private val updateLock = Any()

    /**
     * Interface pour recevoir les notifications de changement de score et de fin de partie
     */
    interface ScoreListener {
        /**
         * Appelé lorsque le score est mis à jour
         *
         * @param score Le score actuel
         */
        fun onScoreUpdated(score: Int)

        /**
         * Appelé lorsque la partie est terminée
         *
         * @param finalScore Le score final du joueur
         */
        fun onGameOver(finalScore: Int)
    }

    init {
        holder.addCallback(this)
        bille = Bille(10)

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // Récupération des dimensions de l'écran selon la version d'Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            screenWidth = windowMetrics.bounds.width() - insets.left - insets.right
            screenHeight = windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            screenWidth = displayMetrics.widthPixels
            screenHeight = displayMetrics.heightPixels
        }
    }

    /**
     * Vérifie si le jeu est actuellement en cours d'exécution
     *
     * @return true si le jeu est en cours d'exécution, false sinon
     */
    fun isRunning(): Boolean {
        return isRunning
    }

    /**
     * Récupère l'objet bille du joueur
     *
     * @return L'objet bille contrôlé par le joueur
     */
    fun getBille(): Bille {
        return bille
    }

    /**
     * Définit la couleur de la bille du joueur
     *
     * @param skinColor La couleur à appliquer à la bille du joueur
     */
    fun setPlayerSkin(skinColor: Int) {
        val paint = Paint().apply {
            color = skinColor
        }
        bille.setColor(paint)
    }

    /**
     * Définit l'écouteur pour les événements de score et de fin de partie
     *
     * @param listener L'objet implémentant l'interface ScoreListener
     */
    fun setScoreListener(listener: ScoreListener) {
        this.scoreListener = listener
    }

    /**
     * Démarre une nouvelle partie
     * Réinitialise le score, les ennemis et lance la boucle de jeu
     */
    fun start() {
        isRunning = true
        gameOver = false
        gameStartTime = System.currentTimeMillis()
        waveStartTime = gameStartTime
        currentWave = 0
        score = 0
        enemies.clear()
        startNextWave()
        handler.post(updateRunnable)
    }

    /**
     * Met le jeu en pause en arrêtant la boucle de jeu
     */
    fun pause() {
        isRunning = false
        handler.removeCallbacks(updateRunnable)
    }

    /**
     * Reprend le jeu si celui-ci n'est pas terminé
     */
    fun resume() {
        if (!gameOver) {
            isRunning = true
            handler.post(updateRunnable)
        }
    }

    /**
     * Lance la prochaine vague d'ennemis
     * Incrémente le compteur de vagues, ajuste la difficulté et génère de nouveaux ennemis
     */
    private fun startNextWave() {
        currentWave++
        waveStartTime = System.currentTimeMillis()

        // Ajuster la durée des vagues en fonction de la progression
        waveDuration = (10000 - (currentWave * 500).coerceAtMost(7000)).toLong()  // Minimum 3 secondes

        // Calculer le nombre d'ennemis pour cette vague
        val enemiesCount = currentWave + 1

        // Notifier le joueur du changement de vague
        scoreListener?.onScoreUpdated(score)  // Mettre à jour l'affichage du score

        // Générer de nouveaux ennemis
        enemies.clear()
        for (i in 0 until enemiesCount) {
            val enemy = Enemy(bille)

            // Positionner aléatoirement autour de l'écran
            val randomSide = (0..3).random()
            when (randomSide) {
                0 -> { // Haut
                    enemy.setX((0..screenWidth).random().toFloat())
                    enemy.setY(-enemy.getRadius().toFloat())
                }
                1 -> { // Droite
                    enemy.setX(screenWidth + enemy.getRadius().toFloat())
                    enemy.setY((0..screenHeight).random().toFloat())
                }
                2 -> { // Bas
                    enemy.setX((0..screenWidth).random().toFloat())
                    enemy.setY(screenHeight + enemy.getRadius().toFloat())
                }
                3 -> { // Gauche
                    enemy.setX(-enemy.getRadius().toFloat())
                    enemy.setY((0..screenHeight).random().toFloat())
                }
            }

            // Augmenter la vitesse et la taille avec les vagues
            val speedFactor = 1.0f + currentWave * 0.2f
            enemy.setVx(enemy.getVx() * speedFactor)
            enemy.setVy(enemy.getVy() * speedFactor)

            val sizeFactor = 1.0f + currentWave * 0.1f
            enemy.setRadius((enemy.getRadius() * sizeFactor).toInt())

            enemies.add(enemy)
        }
    }

    /**
     * Runnable qui exécute la boucle principale du jeu
     * Appelle les méthodes update() et draw() à chaque frame
     */
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                update()
                draw()
                handler.postDelayed(this, updateInterval)
            }
        }
    }

    /**
     * Met à jour l'état du jeu
     * Gère la logique des vagues, le calcul du score,
     * les déplacements des entités et la détection des collisions
     */
    private fun update() {
        synchronized(updateLock) {
            val currentTime = System.currentTimeMillis()

            // Vérifier si la vague actuelle est terminée
            if (currentTime - waveStartTime > waveDuration) {
                score += currentWave * 100
                scoreListener?.onScoreUpdated(score)
                startNextWave()
            }

            // Mettre à jour le score toutes les secondes
            if (currentTime - gameStartTime > 1000) {  // Chaque seconde
                score += 1
                gameStartTime = currentTime  // Réinitialiser le timer pour le score
                scoreListener?.onScoreUpdated(score)
            }

            // Mettre à jour la position de la bille
            bille.update(screenWidth, screenHeight)

            // Optimisation: vérifier les collisions uniquement si nécessaire
            var collisonDetected = false

            // Mise à jour des ennemis et vérification des collisions
            for (enemy in enemies) {
                enemy.update(screenWidth, screenHeight)

                // Optimisation: calcul de distance au carré (évite la racine carrée coûteuse)
                val distanceX = enemy.getX() - bille.getX()
                val distanceY = enemy.getY() - bille.getY()
                val distanceSquared = distanceX * distanceX + distanceY * distanceY
                val radiusSum = enemy.getRadius() + bille.getRadius()

                if (distanceSquared < radiusSum * radiusSum) {
                    collisonDetected = true
                    break
                }
            }

            // Traiter la collision si détectée
            if (collisonDetected) {
                gameOver = true
                isRunning = false
                scoreListener?.onGameOver(score)
            }
        }
    }

    /**
     * Dessine l'état actuel du jeu sur le canvas
     * Affiche la bille, les ennemis et les informations de jeu
     */
    private fun draw() {
        val canvas: Canvas? = holder.lockCanvas()
        canvas?.let {
            it.drawColor(Color.WHITE)

            // Dessiner la bille
            bille.draw(it)

            // Dessiner les ennemis
            for (enemy in enemies) {
                enemy.draw(it)
            }

            // Configurer le pinceau pour le texte
            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 30f
                textAlign = Paint.Align.LEFT
            }

            // Afficher le numéro de vague
            it.drawText("Vague: $currentWave", 20f, screenHeight - 20f, textPaint)

            // Afficher le temps restant pour la vague actuelle
            val timeLeft = (waveDuration - (System.currentTimeMillis() - waveStartTime)) / 1000
            if (timeLeft > 0) {
                it.drawText("Temps restant: ${timeLeft}s", 20f, screenHeight - 60f, textPaint)
            }

            holder.unlockCanvasAndPost(it)
        }
    }

    /**
     * Appelé lorsque la surface est créée
     * Démarre la boucle de jeu si le jeu est en cours d'exécution
     *
     * @param holder Le SurfaceHolder dont la surface a été créée
     */
    override fun surfaceCreated(holder: SurfaceHolder) {
        // Start the game loop when the surface is created
        if (isRunning) {
            handler.post(updateRunnable)
        }
    }

    /**
     * Appelé lorsque la surface change de taille ou de format
     *
     * @param holder Le SurfaceHolder dont la surface a changé
     * @param format Le nouveau format PixelFormat de la surface
     * @param width La nouvelle largeur de la surface
     * @param height La nouvelle hauteur de la surface
     */
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    /**
     * Appelé juste avant que la surface soit détruite
     * Met le jeu en pause pour arrêter la boucle de jeu
     *
     * @param holder Le SurfaceHolder dont la surface va être détruite
     */
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Stop the game loop when the surface is destroyed
        pause()
    }
}