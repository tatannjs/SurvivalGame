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

    override fun update() {
        /* TODO : inclure la taille du perso pour éviter le tremblement*/
        if (this.getX() < Perso.getX()) {
            this.setX(this.getX() + this.getVx())
        } else {
            this.setX(this.getX() - this.getVx())
        }
        if (this.getY() < Perso.getY()) {
            this.setY(this.getY() + this.getVy())
        } else {
            this.setY(this.getY() - this.getVy())
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.drawCircle(getX(),getY(), 50f, this.getColor()) // Draw the bille
    }
}