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

class RulesDialogFragment : DialogFragment() {

    private var dismissListener: (() -> Unit)? = null

    fun setDismissListener(listener: () -> Unit) {
        dismissListener = listener
    }

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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.invoke()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            DialogUtils.getDialogWidth(requireContext()),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}