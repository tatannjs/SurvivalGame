package fr.core.projet.game

import android.graphics.Canvas
import android.graphics.Paint

/**
 * Classe représentant un ennemi dans le jeu.
 * Étend la classe Bille et implémente un comportement de poursuite vers le joueur.
 *
 * @property Perso La bille du joueur que cet ennemi doit poursuivre
 * @property delta La vitesse de déplacement de base de l'ennemi
 * @constructor Crée un ennemi qui suit la bille du joueur spécifiée
 * @param Joueur La bille contrôlée par le joueur à cibler
 */
class Enemy(Joueur: Bille) : Bille(50) {

    /** La bille du joueur que cet ennemi poursuit */
    private var Perso :Bille;

    /** Vitesse de déplacement de base de l'ennemi */
    private val delta = 6f;

    /**
     * Initialise l'ennemi avec une couleur bleue, une position de départ
     * et définit la bille du joueur comme cible
     */
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

    /**
     * Met à jour la position de l'ennemi en fonction de la position du joueur.
     * L'ennemi se déplace vers le joueur tout en maintenant une distance minimale.
     *
     * @param screenWidth La largeur de l'écran en pixels
     * @param screenHeight La hauteur de l'écran en pixels
     */
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

    /**
     * Dessine l'ennemi sur le canvas comme un cercle bleu
     *
     * @param canvas Le canvas sur lequel dessiner l'ennemi
     */
    override fun draw(canvas: Canvas) {
        canvas.drawCircle(getX(),getY(), 50f, this.getColor()) // Draw the bille
    }
}