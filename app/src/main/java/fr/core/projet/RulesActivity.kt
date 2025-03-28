package fr.core.projet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class RulesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Afficher le dialogue des règles stylisé
        val rulesDialog = RulesDialogFragment()

        // Ajouter un écouteur de licenciement pour terminer l'activité
        rulesDialog.setDismissListener { finish() }

        rulesDialog.show(supportFragmentManager, "RulesDialog")
    }
}