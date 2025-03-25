package fr.core.projet

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog

class PauseDialogFragment(private val onResumeGame: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.pause_menu, null)
        builder.setView(view)

        // Set up button click listeners
        val resumeButton: Button = view.findViewById(R.id.resumeButton)
        val quitButton: Button = view.findViewById(R.id.quitButton)

        resumeButton.setOnClickListener {
            onResumeGame()
            dismiss()
        }

        quitButton.setOnClickListener {
            returnToMenu()
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onResume() {
        super.onResume()
        dialog?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                returnToMenu()
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