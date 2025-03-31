package fr.core.projet

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import fr.core.projet.utils.DialogUtils

/**
 * Fragment de dialogue affichant les règles du jeu.
 *
 * Ce dialogue présente les règles du jeu dans une interface stylisée et permet
 * à l'utilisateur de les fermer avec un bouton. Il peut également notifier
 * un écouteur lors de sa fermeture.
 */
class RulesDialogFragment : DialogFragment() {

    /** Fonction de rappel à exécuter lorsque le dialogue est fermé */
    private var dismissListener: (() -> Unit)? = null

    /**
     * Définit l'écouteur de fermeture du dialogue.
     *
     * @param listener La fonction à appeler lorsque le dialogue est fermé
     */
    fun setDismissListener(listener: () -> Unit) {
        dismissListener = listener
    }

    /**
     * Crée et configure la vue du dialogue des règles.
     *
     * @param inflater L'objet LayoutInflater pour gonfler la mise en page
     * @param container Le conteneur parent où la vue sera attachée
     * @param savedInstanceState L'état précédemment sauvegardé, s'il existe
     * @return La vue racine du fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_rules_styled, null)

        val closeButton: Button = view.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            dismiss()
        }

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return view
    }

    /**
     * Appelé lorsque le dialogue est fermé.
     * Exécute l'écouteur de fermeture s'il a été défini.
     *
     * @param dialog L'interface de dialogue étant fermée
     */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.invoke()
    }

    /**
     * Appelé après que le dialogue soit créé.
     * Configure la largeur du dialogue en utilisant une méthode d'utilitaire.
     */
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            DialogUtils.getDialogWidth(requireContext()),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}