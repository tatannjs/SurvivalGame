package fr.core.projet.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.os.Handler

class Game(context: Context, attrs: AttributeSet?) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private var bille: Bille
    private var enemy: Enemy
    private var isRunning: Boolean = false
    private val handler: Handler = Handler()
    private val updateInterval: Long = 16 // Approx 60 FPS

    init {
        holder.addCallback(this)
        bille = Bille(10)
        enemy = Enemy(bille)
    }

    fun getBille(): Bille {
        return bille
    }

    fun start() {
        isRunning = true
        handler.post(updateRunnable)
    }

    fun pause() {
        isRunning = false
        handler.removeCallbacks(updateRunnable)
    }

    fun resume() {
        isRunning = true
        handler.post(updateRunnable)
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
        bille.update()
        enemy.update()
    }

    private fun draw() {
        val canvas: Canvas? = holder.lockCanvas()
        if (canvas != null) {
            canvas.drawColor(Color.WHITE) // Clear the canvas
            bille.draw(canvas)
            enemy.draw(canvas)
            holder.unlockCanvasAndPost(canvas)
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