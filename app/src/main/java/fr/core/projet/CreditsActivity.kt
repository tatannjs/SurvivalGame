package fr.core.projet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CreditsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Afficher le dialogue des crédits stylisé
        val creditsDialog = CreditsDialogFragment()

        // Ajouter un écouteur de licenciement pour terminer l'activité
        creditsDialog.setDismissListener { finish() }

        creditsDialog.show(supportFragmentManager, "CreditsDialog")
    }
}