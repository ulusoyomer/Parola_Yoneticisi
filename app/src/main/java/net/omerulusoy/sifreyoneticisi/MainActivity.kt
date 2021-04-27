package net.omerulusoy.sifreyoneticisi


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.File

class MainActivity : AppCompatActivity() {
    private val dbNames  = ArrayList<String>()
    private var dbFiles : Array<File>? = File("data/data/net.omerulusoy.sifreyoneticisi/databases").listFiles()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ŞifreYöneticisi)
        setContentView(R.layout.activity_main)



    }

    fun getAllDbNames(){
        if (dbFiles != null) {
           if (dbNames.isNotEmpty())
               dbNames.clear()
            for (dbFile in dbFiles as Array<out File>){
                dbNames.add(dbFile.name)
            }
        }
    }

    fun printAllDbNames(){
        if (dbNames.isNotEmpty()){
            for (each in dbNames){
                Log.d("Db Name", each)
            }
        }
    }

    fun clearDBFiles(){
        if(dbFiles != null){
            for (dbFile in dbFiles as Array<out File>){
                dbFile.delete()
            }
        }
    }
}