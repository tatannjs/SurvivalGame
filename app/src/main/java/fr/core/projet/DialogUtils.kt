package fr.core.projet.utils

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * Classe utilitaire qui fournit des méthodes pour gérer et adapter les dimensions
 * des boîtes de dialogue en fonction de la taille de l'écran de l'appareil.
 */
object DialogUtils {
    /**
     * Calcule la largeur optimale d'une boîte de dialogue selon le type d'appareil.
     *
     * @param context Le contexte de l'application pour accéder aux services système
     * @return La largeur recommandée pour une boîte de dialogue en pixels
     *         - Sur tablette : 60% de la largeur d'écran
     *         - Sur téléphone : 85% de la largeur d'écran
     */
    fun getDialogWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        return if (isTablet(context)) {
            (screenWidth * 0.6).toInt()
        } else {
            (screenWidth * 0.85).toInt()
        }
    }

    /**
     * Détermine si l'appareil est une tablette en se basant sur la taille d'écran.
     *
     * @param context Le contexte de l'application pour accéder aux ressources
     * @return true si l'appareil est une tablette, false sinon
     */
    fun isTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE
    }
}