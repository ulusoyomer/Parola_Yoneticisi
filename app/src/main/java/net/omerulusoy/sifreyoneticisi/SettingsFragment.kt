package net.omerulusoy.sifreyoneticisi

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.obsez.android.lib.filechooser.ChooserDialog
import es.dmoral.toasty.Toasty
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteConnector.SQLiteConnector
import java.io.File
import kotlin.system.exitProcess


@Suppress("DEPRECATION")
class SettingsFragment : Fragment() {

    lateinit var cntx: Context
    private lateinit var btnDBExport: Button
    private lateinit var btnDBChangePassword: Button
    private lateinit var btnDBDelete: Button
    private lateinit var etSetOldPassword: EditText
    private lateinit var etSetNewPassword: EditText
    private lateinit var etSetNewPasswordAg: EditText

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

        btnDBChangePassword.setOnClickListener {
            val oldPass = etSetOldPassword.text.toString()
            val newPass = etSetNewPassword.text.toString()
            val newPassAg = etSetNewPasswordAg.text.toString()
            if (oldPass.isNotEmpty() and newPass.isNotEmpty() and newPassAg.isNotEmpty()){
                if (oldPass == SQLiteConnector.dbPass){
                    if (newPass == newPassAg){
                        SQLiteConnector.db?.changePassword(newPass)
                        Toasty.success(
                            cntx,
                            "Parola Başarıyla Değiştirildi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        Toasty.warning(
                            cntx,
                            "Parolar Birbiyle Uyuşmuyor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else{
                    Toasty.warning(
                        cntx,
                        "Eski Parola Yanlış",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                Toasty.warning(
                    cntx,
                    "Tüm Alanları Doldurun",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnDBDelete.setOnClickListener {
            AlertDialog.Builder(cntx)
                .setTitle("Veritabanı Silme İşlemi")
                .setMessage("${SQLiteConnector.dbName} veritabanını silmek istiyormusunuz ?")
                .setPositiveButton("Evet") { _, _ ->
                    SQLiteConnector.closeDatabase()
                    cntx.getDatabasePath(SQLiteConnector.dbName).delete()
                    exitProcess(0)
                }
                .setNegativeButton("Hayır", null).show()
        }

    }

    private fun createChooser() {
        ChooserDialog(cntx).withFilter(true, false)
            .withStartFile(Environment.getExternalStorageDirectory().absolutePath)
            .withChosenListener { path, _ ->
                SQLiteConnector.closeDatabase()
                try {
                    val realPath = path + "/${SQLiteConnector.dbName}"
                    val exportFile = File(realPath)
                    val thisDB = cntx.getDatabasePath(SQLiteConnector.dbName)
                    if (exportFile.exists()) exportFile.delete()
                    thisDB.copyTo(exportFile)
                    SQLiteConnector.openDB(
                        cntx.getDatabasePath(SQLiteConnector.dbName),
                        SQLiteConnector.dbPass
                    )
                    Toasty.success(
                        cntx,
                        "Veritabanı Başarıyla Aktarıldı",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    Toasty.error(
                        cntx,
                        "Aktarma Yapılırken Hata Oldu Farklı Bir Klasör Seçip Yeniden Deneyin",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .build()
            .show()
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