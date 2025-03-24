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

    open fun update() {
        x += vx * delta
        y += vy * delta
    }

    open fun draw(canvas: Canvas) {
        canvas.drawCircle(getX(),getY(), radius.toFloat(), color) // Draw the bille
    }
}