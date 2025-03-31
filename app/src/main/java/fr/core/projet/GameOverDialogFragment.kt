package fr.core.projet

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import fr.core.projet.utils.DialogUtils

/**
 * DialogFragment qui affiche l'écran de fin de partie avec le score du joueur,
 * le meilleur score, et des options pour recommencer ou revenir au menu principal.
 *
 * @property finalScore Le score obtenu par le joueur lors de cette partie
 * @property bestScore Le meilleur score enregistré précédemment
 * @property gamesPlayed Le nombre total de parties jouées
 * @property isNewBestScore Indique si le joueur a établi un nouveau record
 * @property onRestart Callback exécuté lorsque le joueur choisit de recommencer une partie
 */
class GameOverDialogFragment(
    private val finalScore: Int,
    private val bestScore: Int,
    private val gamesPlayed: Int,
    private val isNewBestScore: Boolean,
    private val onRestart: () -> Unit
) : DialogFragment() {

    /**
     * Crée et configure la boîte de dialogue de fin de partie.
     *
     * @param savedInstanceState État de l'instance sauvegardé, si disponible
     * @return Dialog configuré avec le layout de fin de partie
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.AppTheme)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.game_over_menu, null)

        // Configuration des éléments du dialogue
        val messageTextView: TextView = view.findViewById(R.id.gameOverMessage)
        val menuButton: Button = view.findViewById(R.id.menuButton)
        val restartButton: Button = view.findViewById(R.id.restartButton)

        // Message personnalisé
        val message = StringBuilder()
            .append("Score final: $finalScore\n")
            .append("Meilleur score: ${Math.max(bestScore, finalScore)}\n")
            .append("Parties jouées: $gamesPlayed")

        messageTextView.text = message.toString()

        // Afficher félicitations si nouveau record
        if (isNewBestScore) {
            val congratsTextView: TextView = view.findViewById(R.id.congratsTextView)
            congratsTextView.visibility = View.VISIBLE
        }

        // Configurer le bouton pour revenir au menu principal
        menuButton.setOnClickListener {
            val intent = Intent(requireContext(), MenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            requireActivity().finish()
        }

        // Configurer le bouton pour recommencer une partie
        restartButton.setOnClickListener {
            onRestart()
            dismiss()
        }

        // Créer le dialogue
        val dialog = builder.setView(view).create()

        // Fond transparent pour respecter le style défini
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Empêcher la fermeture en cliquant à l'extérieur
        dialog.setCanceledOnTouchOutside(false)

        return dialog
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