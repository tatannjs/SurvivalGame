package fr.core.projet

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class PauseDialogFragment(private val onResumeGame: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.AppTheme)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.pause_menu, null)

        // Configuration des boutons
        val resumeButton: Button = view.findViewById(R.id.resumeButton)
        val quitButton: Button = view.findViewById(R.id.quitButton)

        resumeButton.setOnClickListener {
            onResumeGame()
            dismiss()
        }

        quitButton.setOnClickListener {
            returnToMenu()
        }

        // Créer le dialogue
        val dialog = builder.setView(view).create()

        // Fond transparent pour respecter le style défini dans pause_menu.xml
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Empêcher la fermeture en cliquant à l'extérieur
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    override fun onStart() {
        super.onStart()

        // S'assurer que le dialogue prend une taille correcte
        dialog?.window?.setLayout(
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onResume() {
        super.onResume()
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                onResumeGame()
                dismiss()
                true
            } else {
                false
            }
        }
    }

    private fun returnToMenu() {
        val intent = Intent(requireContext(), MenuActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity?.finish()
    }
}