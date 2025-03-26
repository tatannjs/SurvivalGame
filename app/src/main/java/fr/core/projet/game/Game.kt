package fr.core.projet.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.os.Handler
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager

class Game(context: Context, attrs: AttributeSet?) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private var bille: Bille
    private var enemy: Enemy
    private var isRunning: Boolean = false
    private val handler: Handler = Handler()
    private val updateInterval: Long = 16 // Approx 60 FPS
    private var screenWidth: Int
    private var screenHeight: Int

    init {
        holder.addCallback(this)
        bille = Bille(10)
        enemy = Enemy(bille)

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
        bille.update(screenWidth,screenHeight)
        enemy.update(screenWidth,screenHeight)
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