package fr.core.projet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Activité servant uniquement de conteneur pour afficher le fragment de dialogue des règles.
 *
 * Cette activité agit comme un pont entre le flux de navigation de l'application et l'affichage
 * des règles dans un DialogFragment. Elle crée et affiche immédiatement le DialogFragment
 * puis se termine automatiquement lorsque le dialogue est fermé.
 */
class RulesActivity : AppCompatActivity() {

    /**
     * Initialise l'activité en créant et affichant le fragment de dialogue des règles.
     *
     * Ne définit pas de layout pour cette activité car elle sert uniquement
     * à afficher le DialogFragment.
     *
     * @param savedInstanceState L'état précédemment sauvegardé, s'il existe
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Afficher le dialogue des règles stylisé
        val rulesDialog = RulesDialogFragment()

        // Ajouter un écouteur de licenciement pour terminer l'activité
        rulesDialog.setDismissListener { finish() }

        rulesDialog.show(supportFragmentManager, "RulesDialog")
    }
}