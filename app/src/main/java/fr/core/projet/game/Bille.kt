package fr.core.projet.game

import android.graphics.Canvas
import android.graphics.Paint

/**
 * Classe de base représentant une bille dans le jeu.
 * Gère la position, le déplacement, la couleur et le rendu d'une bille.
 * Peut être étendue pour créer différents types de billes (joueur, ennemis).
 *
 * @property x Position horizontale de la bille
 * @property y Position verticale de la bille
 * @property vx Vitesse horizontale de la bille
 * @property vy Vitesse verticale de la bille
 * @property color Couleur de la bille (objet Paint)
 * @property delta Multiplicateur de vitesse pour le mouvement
 * @property radius Rayon de la bille en pixels
 * @constructor Crée une bille avec une taille spécifiée
 * @param size Rayon initial de la bille en pixels
 */
open class Bille(size : Int) {

    /** Position horizontale de la bille */
    private var x: Float = 50f

    /** Position verticale de la bille */
    private var y: Float = 50f

    /** Vitesse horizontale de la bille */
    private var vx: Float = 0f

    /** Vitesse verticale de la bille */
    private var vy: Float = 0f

    /** Couleur de la bille */
    private var color: Paint = Paint()

    /** Multiplicateur de vitesse pour le mouvement */
    private var delta: Int = 3

    /** Rayon de la bille en pixels */
    private var radius: Int = 0

    /**
     * Initialise la bille avec une couleur rouge par défaut
     * et définit son rayon à la valeur fournie
     */
    init {
        color.setARGB(255, 255, 0, 0)
        radius = size
    }

    /**
     * Récupère la position horizontale de la bille
     *
     * @return Position horizontale en pixels
     */
    fun getX(): Float {
        return x
    }

    /**
     * Récupère la position verticale de la bille
     *
     * @return Position verticale en pixels
     */
    fun getY(): Float {
        return y
    }

    /**
     * Définit la position horizontale de la bille
     *
     * @param x Nouvelle position horizontale en pixels
     */
    fun setX(x: Float) {
        this.x = x
    }

    /**
     * Définit la position verticale de la bille
     *
     * @param y Nouvelle position verticale en pixels
     */
    fun setY(y: Float) {
        this.y = y
    }

    /**
     * Définit la couleur de la bille
     *
     * @param color Objet Paint définissant la couleur et le style
     */
    fun setColor(color: Paint) {
        this.color = color
    }

    /**
     * Récupère la couleur de la bille
     *
     * @return Objet Paint contenant la couleur et le style
     */
    fun getColor(): Paint {
        return color
    }

    /**
     * Récupère la vitesse horizontale de la bille
     *
     * @return Vitesse horizontale
     */
    fun getVx(): Float {
        return vx
    }

    /**
     * Récupère la vitesse verticale de la bille
     *
     * @return Vitesse verticale
     */
    fun getVy(): Float {
        return vy
    }

    /**
     * Définit la vitesse horizontale de la bille
     *
     * @param vx Nouvelle vitesse horizontale
     */
    fun setVx(vx: Float) {
        this.vx = vx
    }

    /**
     * Définit la vitesse verticale de la bille
     *
     * @param vy Nouvelle vitesse verticale
     */
    fun setVy(vy: Float) {
        this.vy = vy
    }

    /**
     * Définit le rayon de la bille
     *
     * @param radius Nouveau rayon en pixels
     */
    fun setRadius(radius: Int) {
        this.radius = radius
    }

    /**
     * Récupère le rayon de la bille
     *
     * @return Rayon en pixels
     */
    fun getRadius(): Int {
        return radius
    }

    /**
     * Met à jour la position de la bille en fonction de sa vitesse
     * et vérifie qu'elle reste dans les limites de l'écran
     *
     * @param screenWidth Largeur de l'écran en pixels
     * @param screenHeight Hauteur de l'écran en pixels
     */
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

    /**
     * Dessine la bille sur le canvas fourni
     *
     * @param canvas Le canvas sur lequel dessiner la bille
     */
    open fun draw(canvas: Canvas) {
        canvas.drawCircle(getX(),getY(), radius.toFloat(), color) // Draw the bille
    }
}