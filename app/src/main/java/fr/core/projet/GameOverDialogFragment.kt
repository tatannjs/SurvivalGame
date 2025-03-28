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

class GameOverDialogFragment(
    private val finalScore: Int,
    private val bestScore: Int,
    private val gamesPlayed: Int,
    private val isNewBestScore: Boolean,
    private val onRestart: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.AppTheme)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.game_over_menu, null)

        // Configuration des éléments du dialogue
        val titleTextView: TextView = view.findViewById(R.id.gameOverTitle)
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

        menuButton.setOnClickListener {
            val intent = Intent(requireContext(), MenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            requireActivity().finish()
        }

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

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            DialogUtils.getDialogWidth(requireContext()),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}