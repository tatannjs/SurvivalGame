package fr.core.projet.game

import android.graphics.Canvas
import android.graphics.Paint

open class Bille(size : Int) {

    private var x: Float = 50f
    private var y: Float = 50f
    private var vx: Float = 0f
    private var vy: Float = 0f
    private var color: Paint = Paint()
    private var delta: Int = 3
    private var radius: Int = 0

    init {
        color.setARGB(255, 255, 0, 0)
        radius = size
    }

    fun getX(): Float {
        return x
    }

    fun getY(): Float {
        return y
    }

    fun setX(x: Float) {
        this.x = x
    }

    fun setY(y: Float) {
        this.y = y
    }

    fun setColor(color: Paint) {
        this.color = color
    }

    fun getColor(): Paint {
        return color
    }

    fun getVx(): Float {
        return vx
    }

    fun getVy(): Float {
        return vy
    }

    fun setVx(vx: Float) {
        this.vx = vx
    }

    fun setVy(vy: Float) {
        this.vy = vy
    }

    fun setRadius(radius: Int) {
        this.radius = radius
    }
    fun getRadius(): Int {
        return radius
    }

    open fun update(screenWidth: Int, screenHeight: Int) {
        x += vx * delta
        y += vy * delta

        //vérifie qu'il ne sort pas de l'écran
        if (x - radius < 0) {
            x = radius.toFloat()
        } else if (x + radius > screenWidth) {
            x = (screenWidth - radius).toFloat()
        }

        if (y - radius < 0) {
            y = radius.toFloat()
        } else if (y + radius > screenHeight) {
            y = (screenHeight - radius).toFloat()
        }
    }

    open fun draw(canvas: Canvas) {
        canvas.drawCircle(getX(),getY(), radius.toFloat(), color) // Draw the bille
    }
}