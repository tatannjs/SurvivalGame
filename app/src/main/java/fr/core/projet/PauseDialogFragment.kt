package fr.core.projet

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater

class PauseDialogFragment(private val onResumeGame: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.pause_menu, null)

        builder.setView(view)
            .setPositiveButton("Resume") { _, _ -> onResumeGame() }
            .setNegativeButton("Quit") { _, _ -> returnToMenu() }

        return builder.create()
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