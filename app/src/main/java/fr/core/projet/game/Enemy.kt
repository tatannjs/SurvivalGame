package fr.core.projet.game

import android.graphics.Canvas
import android.graphics.Paint

class Enemy(Joueur: Bille) : Bille(50) {

    private var Perso :Bille;
    private val delta = 6f;

    init {
        this.setColor(Paint().apply {
            setARGB(255, 0, 0, 255)
        })
        Perso = Joueur;
        this.setX(100f)
        this.setY(100f)
        this.setVx(delta)
        this.setVy(delta)
    }

    override fun update(screenWidth: Int, screenHeight: Int) {
        val targetX = Perso.getX()
        val targetY = Perso.getY()
        val distanceX = targetX - this.getX()
        val distanceY = targetY - this.getY()
        val distance = Math.sqrt((distanceX * distanceX + distanceY * distanceY).toDouble()).toFloat()

        val minDistance = this.getRadius() + Perso.getRadius()

        if (distance > minDistance) {
            if (Math.abs(distanceX) > this.getVx()) {
                if (this.getX() < targetX) {
                    this.setX(this.getX() + this.getVx())
                } else {
                    this.setX(this.getX() - this.getVx())
                }
            }

            if (Math.abs(distanceY) > this.getVy()) {
                if (this.getY() < targetY) {
                    this.setY(this.getY() + this.getVy())
                } else {
                    this.setY(this.getY() - this.getVy())
                }
            }
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.drawCircle(getX(),getY(), 50f, this.getColor()) // Draw the bille
    }
}