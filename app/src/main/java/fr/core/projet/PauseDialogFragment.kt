package fr.core.projet

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import fr.core.projet.utils.DialogUtils

/**
 * Fragment de dialogue affichant le menu de pause pendant le jeu.
 *
 * Ce dialogue permet au joueur de reprendre la partie ou de quitter vers le menu principal.
 * Il bloque les interactions avec le jeu lorsqu'il est affiché et gère la pression du bouton retour.
 *
 * @property onResumeGame Fonction de rappel à exécuter lorsque le joueur reprend la partie
 */
class PauseDialogFragment(private val onResumeGame: () -> Unit) : DialogFragment() {

    /**
     * Crée et configure le dialogue de pause.
     *
     * @param savedInstanceState L'état précédemment sauvegardé, s'il existe
     * @return Le dialogue configuré avec les éléments du menu de pause
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.AppTheme)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.pause_menu, null)
        val resumeButton: Button = view.findViewById(R.id.resumeButton)
        val quitButton: Button = view.findViewById(R.id.quitButton)
        resumeButton.setOnClickListener {
            onResumeGame()
            dismiss()
        }
        quitButton.setOnClickListener {
            returnToMenu()
        }
        val dialog = builder.setView(view).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    /**
     * Appelé après que le dialogue soit créé.
     * Configure la largeur du dialogue en utilisant une méthode d'utilitaire.
     */
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            DialogUtils.getDialogWidth(requireContext()),
            LayoutParams.WRAP_CONTENT
        )
    }

    /**
     * Appelé lorsque le dialogue devient visible pour l'utilisateur.
     * Configure la gestion du bouton retour pour reprendre le jeu.
     */
    override fun onResume() {
        super.onResume()
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                onResumeGame()
                dismiss()
                true
            } else false

        }
    }

    /**
     * Quitte la partie et retourne au menu principal.
     * Efface la pile d'activités pour éviter de revenir au jeu avec le bouton retour.
     */
    private fun returnToMenu() {
        val intent = Intent(requireContext(), MenuActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity?.finish()
    }
}