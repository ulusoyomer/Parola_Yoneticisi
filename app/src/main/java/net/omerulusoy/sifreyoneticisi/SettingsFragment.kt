package net.omerulusoy.sifreyoneticisi

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteConnector.SQLiteConnector


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
            SQLiteConnector.writeStoragePermission(cntx)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            102->{
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "file://"
                    startActivityForResult(intent, 55)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 55 && resultCode == AppCompatActivity.RESULT_OK && data != null){
            Log.i("PATH",data.data?.path.toString())
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