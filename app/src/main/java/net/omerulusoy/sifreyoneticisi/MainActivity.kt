package net.omerulusoy.sifreyoneticisi


import android.Manifest
import android.annotation.SuppressLint
import android.app.Instrumentation
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import es.dmoral.toasty.Toasty
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteConnector.SQLiteConnector
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.SQLiteCreator
import net.sqlcipher.database.SQLiteDatabase
import java.io.File


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val dbNames = ArrayList<String>()
    private val dbImportCode: Int = 1
    private var dbFiles: Array<File>? = null

    private lateinit var adapterAllDbNameList: HomeListAdapter
    private lateinit var dbList: ListView
    private lateinit var title: TextView
    private lateinit var btnNewDb: Button
    private lateinit var btnOpenDb: Button


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ŞifreYöneticisi)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)

        dbList = findViewById(R.id.lvAllDb)
        btnOpenDb = findViewById(R.id.btnOpenDb)
        btnNewDb = findViewById(R.id.btnNewDb)
        title = findViewById(R.id.tv_TitleList)

        getAllDbNames()
        printAllDbNames()
        title.text = "Mevcut Veritabanları" + " (" + dbNames.size + ")"
        SQLiteDatabase.loadLibs(this)
        dbList.setOnItemClickListener { _, _, i, _ ->
            val dbName = dbList.getItemAtPosition(i).toString()
            val openDBLayout = LayoutInflater.from(this).inflate(R.layout.home_open_db, null)
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setView(openDBLayout)
            val title = openDBLayout.findViewById<TextView>(R.id.tvLoginTitle)
            title.text = "$dbName Veritabanı Giriş Ekranı"
            val etDBPassword =
                openDBLayout.findViewById<EditText>(R.id.etDBPassword)
            val btnDBLogin = openDBLayout.findViewById<Button>(R.id.btnDBLogin)
            btnDBLogin.setOnClickListener {
                try {
                    SQLiteConnector.openDB(getDatabasePath(dbName), etDBPassword.text.toString())
                    Toasty.success(
                        this,
                        "Giriş Başarılı",
                        Toast.LENGTH_SHORT,
                        true
                    )
                        .show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    Animatoo.animateSlideLeft(this)
                } catch (e: Exception) {
                    Toasty.error(
                        this,
                        "Şifre Hatalı",
                        Toast.LENGTH_SHORT,
                        true
                    )
                        .show()
                }
            }

            alertDialog.show()
        }

    }

    override fun onResume() {
        super.onResume()
        SQLiteConnector.closeDatabase()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                dbImportCode -> {
                    try {
                        val dbFile = File(data.data?.let { SQLiteConnector.getRealPathFromURI(this, it) })
                        val dbName = dbFile.name
                        if (dbName.split('.').last() == "db") {
                            if (!dbNames.contains(dbName)) {
                                val newDBFile = getDatabasePath(dbName)
                                if (newDBFile.exists()) newDBFile.delete()
                                newDBFile.mkdirs()
                                newDBFile.delete()
                                dbFile.copyTo(newDBFile)
                                Toasty.success(
                                    this,
                                    "Veritabanı Aktarıldı",
                                    Toast.LENGTH_SHORT,
                                    true
                                )
                                    .show()
                            } else {
                                Toasty.warning(
                                    this,
                                    "Aynı İsimli Bir Veritabanı Var",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()
                            }
                        } else {
                            Toasty.warning(
                                this,
                                "Dosya Türü db Uzantılı Olmalı",
                                Toast.LENGTH_LONG,
                                true
                            )
                                .show()
                        }

                    } catch (e: Exception) {
                        Toasty.error(
                            this,
                            "Veritabanı Aktarılamadı. Veritabanını Cihaza Aktarıp Yeniden Deneyin.",
                            Toast.LENGTH_LONG,
                            true
                        )
                            .show()
                    } finally {
                        updateDBList()
                    }
                }
            }
        }
    }


    fun showChooser(view: View) {
        view as Button
        SQLiteConnector.writeStoragePermission(this@MainActivity)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            101->{
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "*/*"
                    startActivityForResult(intent, dbImportCode)
                }
            }
        }
    }

    private fun getAllDbNames() {
        dbFiles = File(SQLiteConnector.dbPath).listFiles()
        dbNames.clear()
        if (dbFiles != null) {
            for (dbFile in dbFiles as Array<out File>) {
                dbNames.add(dbFile.name)
            }
        }

    }

    private fun printAllDbNames() {
        adapterAllDbNameList = HomeListAdapter(this, dbNames)
        dbList.adapter = adapterAllDbNameList
    }

    fun clearDBFiles() {
        if (dbFiles != null) {
            for (dbFile in dbFiles as Array<out File>) {
                dbFile.delete()
            }
        }
    }

    private fun updateDBList() {
        getAllDbNames()
        adapterAllDbNameList.notifyDataSetChanged()
        printAllDbNames()
    }


    fun openNewDbScreen(view: View) {
        view as Button
        val newDBLayout = LayoutInflater.from(this).inflate(R.layout.home_open_new_db, null)
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setView(newDBLayout)
        val dbName = newDBLayout.findViewById<EditText>(R.id.etDBName)
        val password = newDBLayout.findViewById<EditText>(R.id.etPassword)
        val passwordControl = newDBLayout.findViewById<EditText>(R.id.etPasswordControl)
        val btnCreate = newDBLayout.findViewById<Button>(R.id.btnCreateNewDB)
        btnCreate.setOnClickListener {
            if (dbName.text.isNotBlank() and password.text.isNotBlank() and
                passwordControl.text.isNotBlank()
            ) {
                if (password.text.toString() == passwordControl.text.toString()) {
                    if (!dbNames.contains(dbName.text.toString() + ".db")) {
                        SQLiteCreator(this, dbName.text.toString(), password.text.toString())
                        updateDBList()
                        Toasty.success(
                            this,
                            "Veritabanı Başarıyla Oluşturuldu !",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    } else {
                        Toasty.warning(
                            this,
                            "Aynı İsimde Bir Veritaban Var",
                            Toast.LENGTH_SHORT,
                            true
                        )
                            .show()
                    }
                } else
                    Toasty.warning(this, "Şifreler Birbirinden Farklı", Toast.LENGTH_SHORT, true)
                        .show()
            } else {
                Toasty.warning(this, "Tüm Alanları Doldurunuz", Toast.LENGTH_SHORT, true).show()
            }
        }
        alertDialog.show()
    }

    class HomeListAdapter(private val cntx: Context, private val names: ArrayList<String>) :
        ArrayAdapter<String>(cntx, R.layout.home_list_row, names) {

        @SuppressLint("ViewHolder", "InflateParams")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(cntx)
            val view = inflater.inflate(R.layout.home_list_row, null)
            val text = view.findViewById<TextView>(R.id.tv_DBName)
            text.text = names[position]
            return view
        }
    }

}