package net.omerulusoy.sifreyoneticisi

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText


class SettingsFragment : Fragment() {

    lateinit var cntx:Context
    private lateinit var btnDBExport:Button
    lateinit var btnDBChangePassword:Button
    lateinit var btnDBDelete:Button
    lateinit var etSetOldPassword:EditText
    lateinit var etSetNewPassword:EditText
    lateinit var etSetNewPasswordAg:EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnDBExport = view.findViewById(R.id.btnDBExport)
        btnDBChangePassword = view.findViewById(R.id.btnDBChangePassword)
        btnDBDelete = view.findViewById(R.id.btnDBDelete)
        etSetOldPassword = view.findViewById(R.id.etSetOldPassword)
        etSetNewPassword = view.findViewById(R.id.etSetNewPassword)
        etSetNewPasswordAg = view.findViewById(R.id.etSetNewPasswordAg)

        btnDBExport.setOnClickListener {

        }

    }

    companion object {

        @JvmStatic
        fun newInstance(context:Context) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    cntx = context
                }
            }
    }
}