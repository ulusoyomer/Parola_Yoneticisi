package net.omerulusoy.sifreyoneticisi


import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import es.dmoral.toasty.Toasty
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteConnector.SQLiteConnector
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.SQLiteCreator
import java.io.File
import net.sqlcipher.database.SQLiteDatabase


class MainActivity : AppCompatActivity() {
    private val dbNames = ArrayList<String>()

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
    }

    override fun onStart() {
        super.onStart()
        Log.d("START", "START")
    }

    override fun onResume() {
        super.onResume()
        Log.d("onResume", "onResume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("onRestart", "onRestart")
    }

    override fun onPause() {
        super.onPause()
        Log.d("onPause", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("onStop", "onStop")
    }

    fun getAllDbNames() {
        dbFiles = File(SQLiteConnector.dbPath).listFiles()
        if (dbFiles != null) {
            if (dbNames.isNotEmpty())
                dbNames.clear()
            for (dbFile in dbFiles as Array<out File>) {
                dbNames.add(dbFile.name)
                Log.d("DBNAME", dbFile.name)
            }
        }
    }

    fun printAllDbNames() {
        if (dbNames.isNotEmpty()) {
            adapterAllDbNameList = HomeListAdapter(this, dbNames)
            dbList.adapter = adapterAllDbNameList
        }
    }

    fun clearDBFiles() {
        if (dbFiles != null) {
            for (dbFile in dbFiles as Array<out File>) {
                dbFile.delete()
            }
        }
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
                    if (!dbNames.contains(dbName.text.toString())) {
                        val db =
                            SQLiteCreator(this, dbName.text.toString(), password.text.toString())
                        getAllDbNames()
                        adapterAllDbNameList.notifyDataSetChanged()
                        File("data/data/net.omerulusoy.sifreyoneticisi/databases").listFiles()
                        printAllDbNames()
                        Toasty.success(
                            this,
                            "Veritabanı Başarıyla Oluşturuldu !",
                            Toast.LENGTH_SHORT,
                            true
                        ).show();
                    }
                } else
                    Toasty.warning(this, "Şifreler Birbirinden Farklı", Toast.LENGTH_SHORT, true)
                        .show();
            } else {
                Toasty.warning(this, "Tüm Alanları Doldurunuz", Toast.LENGTH_SHORT, true).show();
            }
        }
        alertDialog.show()
    }


    class HomeListAdapter(private val cntx: Context, private val names: ArrayList<String>) :
        ArrayAdapter<String>(cntx, R.layout.home_list_row, names) {

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(cntx)
            val view = inflater.inflate(R.layout.home_list_row, null)
            val text = view.findViewById<TextView>(R.id.tv_DBName)
            text.text = names[position]
            return view
        }
    }

}