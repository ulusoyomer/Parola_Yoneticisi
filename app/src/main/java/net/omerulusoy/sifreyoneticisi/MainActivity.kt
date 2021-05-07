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
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import java.io.File

class MainActivity : AppCompatActivity() {
    private val dbNames = ArrayList<String>()
    private var dbFiles: Array<File>? =
        File("data/data/net.omerulusoy.sifreyoneticisi/databases").listFiles()
    lateinit var dbList: ListView
    lateinit var title: TextView
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
        getAllDbNames()
        dbList = findViewById(R.id.lvAllDb)
        title = findViewById(R.id.tv_TitleList)
        if (dbNames.isEmpty()) {

        }
        title.text = "Mevcut Veritabanları" + " (" + dbNames.size + ")"
    }

    fun getAllDbNames() {
        if (dbFiles != null) {
            if (dbNames.isNotEmpty())
                dbNames.clear()
            for (dbFile in dbFiles as Array<out File>) {
                dbNames.add(dbFile.name)
            }
        }
    }

    fun printAllDbNames() {
        if (dbNames.isNotEmpty()) {
            for (each in dbNames) {
                Log.d("Db Name", each)
            }
        }
    }

    fun clearDBFiles() {
        if (dbFiles != null) {
            for (dbFile in dbFiles as Array<out File>) {
                dbFile.delete()
            }
        }
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