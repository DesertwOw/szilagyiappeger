package hu.radosdev.szilagyiapp.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import hu.radosdev.szilagyiapp.R

class ErrorFragment : Fragment() {

    private lateinit var errorMessage: String

    companion object {
        private const val ARG_ERROR_MESSAGE = "error_message"

        fun newInstance(errorMessage: String): ErrorFragment {
            val fragment = ErrorFragment()
            val args = Bundle()
            args.putString(ARG_ERROR_MESSAGE, errorMessage)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            errorMessage = it.getString(ARG_ERROR_MESSAGE).orEmpty()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_error, container, false)
        val errorTextView = view.findViewById<TextView>(R.id.error_text)
        errorTextView.text = errorMessage
        return view
    }
}