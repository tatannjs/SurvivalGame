package fr.core.projet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Activité servant uniquement de conteneur pour afficher le fragment de dialogue des crédits.
 *
 * Cette activité agit comme un pont entre le flux de navigation de l'application et l'affichage
 * des crédits dans un DialogFragment. Elle crée et affiche immédiatement le DialogFragment
 * puis se termine automatiquement lorsque le dialogue est fermé.
 */
class CreditsActivity : AppCompatActivity() {

    /**
     * Initialise l'activité en créant et affichant le fragment de dialogue des crédits.
     *
     * Ne définit pas de layout pour cette activité car elle sert uniquement
     * à afficher le DialogFragment.
     *
     * @param savedInstanceState L'état précédemment sauvegardé, s'il existe
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Afficher le dialogue des crédits stylisé
        val creditsDialog = CreditsDialogFragment()

        // Ajouter un écouteur de licenciement pour terminer l'activité
        creditsDialog.setDismissListener { finish() }

        creditsDialog.show(supportFragmentManager, "CreditsDialog")
    }
}