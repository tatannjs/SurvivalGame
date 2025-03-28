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

class Game(context: Context, attrs: AttributeSet?) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private var bille: Bille
    private var enemies = CopyOnWriteArrayList<Enemy>()
    private var isRunning: Boolean = false
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 16 // Approx 60 FPS
    private var screenWidth: Int
    private var screenHeight: Int

    private var gameStartTime: Long = 0
    private var currentWave: Int = 0
    private var waveStartTime: Long = 0
    private var waveDuration: Long = 10000 // 10 seconds
    private var score: Int = 0
    private var gameOver: Boolean = false
    private var scoreListener: ScoreListener? = null

    private val updateLock = Any()

    interface ScoreListener {
        fun onScoreUpdated(score: Int)
        fun onGameOver(finalScore: Int)
    }

    init {
        holder.addCallback(this)
        bille = Bille(10)

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

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

    fun isRunning(): Boolean {
        return isRunning
    }

    fun getBille(): Bille {
        return bille
    }

    fun setPlayerSkin(skinColor: Int) {
        val paint = Paint().apply {
            color = skinColor
        }
        bille.setColor(paint)
    }

    fun setScoreListener(listener: ScoreListener) {
        this.scoreListener = listener
    }

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

    fun pause() {
        isRunning = false
        handler.removeCallbacks(updateRunnable)
    }

    fun resume() {
        if (!gameOver) {
            isRunning = true
            handler.post(updateRunnable)
        }
    }

    private fun startNextWave() {
        currentWave++
        waveStartTime = System.currentTimeMillis()

        // CORRECTION 2: Ajuster la durée des vagues en fonction de la progression
        waveDuration = (10000 - (currentWave * 500).coerceAtMost(7000)).toLong()  // Minimum 3 secondes

        // Calculer le nombre d'ennemis pour cette vague
        val enemiesCount = currentWave + 1

        // CORRECTION 3: Notifier le joueur du changement de vague
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


    private val updateRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                update()
                draw()
                handler.postDelayed(this, updateInterval)
            }
        }
    }

    private fun update() {
        synchronized(updateLock) {
            val currentTime = System.currentTimeMillis()

            if (currentTime - waveStartTime > waveDuration) {
                score += currentWave * 100
                scoreListener?.onScoreUpdated(score)
                startNextWave()
            }

            if (currentTime - gameStartTime > 1000) {  // Chaque seconde
                score += 1
                gameStartTime = currentTime  // Réinitialiser le timer pour le score
                scoreListener?.onScoreUpdated(score)
            }

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

            if (collisonDetected) {
                gameOver = true
                isRunning = false
                scoreListener?.onGameOver(score)
            }
        }
    }

    private fun draw() {
        val canvas: Canvas? = holder.lockCanvas()
        canvas?.let {
            it.drawColor(Color.WHITE)

            bille.draw(it)

            for (enemy in enemies) {
                enemy.draw(it)
            }

            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 30f
                textAlign = Paint.Align.LEFT
            }
            it.drawText("Vague: $currentWave", 20f, screenHeight - 20f, textPaint)

            val timeLeft = (waveDuration - (System.currentTimeMillis() - waveStartTime)) / 1000
            if (timeLeft > 0) {
                it.drawText("Temps restant: ${timeLeft}s", 20f, screenHeight - 60f, textPaint)
            }

            holder.unlockCanvasAndPost(it)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Start the game loop when the surface is created
        if (isRunning) {
            handler.post(updateRunnable)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Handle surface changes if needed
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Stop the game loop when the surface is destroyed
        pause()
    }
}