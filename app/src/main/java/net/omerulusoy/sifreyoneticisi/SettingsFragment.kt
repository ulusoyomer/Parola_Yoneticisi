package net.omerulusoy.sifreyoneticisi

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import es.dmoral.toasty.Toasty
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteConnector.SQLiteConnector
import java.io.File
import java.io.FileOutputStream
import java.net.URI


class SettingsFragment : Fragment() {

    lateinit var cntx: Context
    private lateinit var btnDBExport: Button
    lateinit var btnDBChangePassword: Button
    lateinit var btnDBDelete: Button
    lateinit var etSetOldPassword: EditText
    lateinit var etSetNewPassword: EditText
    lateinit var etSetNewPasswordAg: EditText

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
            if (SQLiteConnector.writeStoragePermission(cntx)) {
                createChooser()

            }
        }

    }

    private fun createChooser() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        startActivityForResult(intent, 55)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && data != null && requestCode == 55){
            val newUri = Uri.withAppendedPath(data.data,"${SQLiteConnector.dbName}")
            val folder = cntx.getContentResolver().openInputStream(newUri);

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            102 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    createChooser()
                }
            }
        }
    }



    companion object {

        @JvmStatic
        fun newInstance(context: Context) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    cntx = context
                }
            }
    }
}