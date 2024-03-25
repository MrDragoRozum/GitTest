package ru.rozum.gitTest.presentation.fragment.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ru.rozum.gitTest.R

class ErrorDialogFragment : DialogFragment() {

    private lateinit var error: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        error = requireArguments().getString(KEY_ERROR) ?: getString(R.string.error)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = requireActivity().let {
        AlertDialog.Builder(it).apply {
            setTitle(getString(R.string.error))
            setMessage(error)
            setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.cancel()
            }
        }.create()
    }

    companion object {
        fun newInstance(error: String?): ErrorDialogFragment = ErrorDialogFragment().apply {
            arguments = Bundle().apply {
                putString(KEY_ERROR, error)
            }
        }
        private const val KEY_ERROR = "key_error"
    }
}