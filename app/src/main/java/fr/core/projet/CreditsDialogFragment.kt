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
 * DialogFragment qui affiche les crédits de l'application dans une boîte de dialogue stylisée.
 *
 * Cette classe s'occupe d'afficher le contenu des crédits (contributeurs, licences, etc.)
 * et permet de revenir à l'écran précédent via un bouton de fermeture.
 */
class CreditsDialogFragment : DialogFragment() {

    /**
     * Listener appelé lorsque la boîte de dialogue est fermée.
     * Permet à l'activité parente de réagir à la fermeture des crédits.
     */
    private var dismissListener: (() -> Unit)? = null

    /**
     * Définit le listener à appeler lorsque la boîte de dialogue est fermée.
     *
     * @param listener La fonction à exécuter lors de la fermeture de la boîte de dialogue
     */
    fun setDismissListener(listener: () -> Unit) {
        dismissListener = listener
    }

    /**
     * Crée et configure la vue de la boîte de dialogue des crédits.
     *
     * @param inflater Le LayoutInflater utilisé pour gonfler la vue
     * @param container Le ViewGroup parent dans lequel la vue pourrait être attachée
     * @param savedInstanceState L'état précédemment sauvegardé, s'il existe
     * @return La vue racine de la boîte de dialogue des crédits
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_credits_styled, null)

        val closeButton: Button = view.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            dismiss()
        }

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return view
    }

    /**
     * Appelé lorsque la boîte de dialogue est fermée.
     * Exécute le dismissListener s'il a été défini.
     *
     * @param dialog L'interface DialogInterface associée à cette boîte de dialogue
     */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.invoke()
    }

    /**
     * Appelé au démarrage du dialogue pour ajuster sa taille en fonction
     * de la taille de l'écran (téléphone ou tablette).
     * Utilise la classe utilitaire DialogUtils pour calculer la largeur optimale.
     */
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            DialogUtils.getDialogWidth(requireContext()),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}