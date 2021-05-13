package net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteConnector

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.SharedPreferences
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.SQLiteCreator
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.Tables.Account
import net.sqlcipher.database.SQLiteDatabase
import java.io.File
import java.util.*

object SQLiteConnector {
    private val alphabet = arrayOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
        "P", "R", "S", "T", "U", "V", "Y", "Z", "X", "W"
    )
    private val trChars = arrayOf("Ç", "Ğ", "İ", "Ö", "Ş", "Ü", "ı")

    private val symbols = arrayOf(
        "!",
        "\\\"",
        "#",
        "$",
        "%",
        "&",
        "'",
        "(",
        ")",
        "*",
        "+",
        ",",
        "-",
        ".",
        "/",
        ":",
        ";",
        "<",
        "=",
        ">",
        "?",
        "@",
        "[",
        "\\",
        "]",
        "^",
        "_",
        "`",
        "/",
        "{",
        "|",
        "}",
        "~"
    )
    private val numbers = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    private val methods = mapOf(
        "alphabet" to alphabet, "trChars" to trChars,
        "symbols" to symbols, "numbers" to numbers
    )


    private var db: SQLiteDatabase? = null
    const val dbPath = "data/data/net.omerulusoy.sifreyoneticisi/databases"


    fun openDB(dbFile: File, dbPassword: String) {
        if (db == null)
            db = SQLiteDatabase.openOrCreateDatabase(dbFile, dbPassword, null)
    }

    fun closeDatabase() {
        db?.close()
        db = null
    }

    private fun setAccount(account: Account): ContentValues {
        val contentValues = ContentValues()
        contentValues.put("name", account.name)
        contentValues.put("password", account.password)
        return contentValues
    }

    fun addNewAccount(account: Account) {
        val contentValues = setAccount(account)
        db?.insert(SQLiteCreator.tableName, null, contentValues)
    }

    fun updateAccount(account: Account) {
        val contentValues = setAccount(account)
        db?.update(SQLiteCreator.tableName, contentValues, "id = ${account.id}", null)
    }

    fun deleteAccount(id: Int) {
        db?.delete(SQLiteCreator.tableName, "id = $id", null)
    }

    fun getAllAccounts(): ArrayList<Account> {
        val accountList = ArrayList<Account>()
        val cursor =
            db?.rawQuery(String.format("SELECT * FROM '%s';", SQLiteCreator.tableName), null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {

                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val password = cursor.getString(cursor.getColumnIndex("password"))

                    val tmpAccount = Account(id, name, password)
                    accountList.add(tmpAccount)
                    cursor.moveToNext()
                }
            }
        }
        cursor?.close()
        return accountList
    }

    @SuppressLint("CommitPrefEdits")
    fun changeSharedPreferences(sharedPreferences: SharedPreferences,value:Any,who:String){
        val editor = sharedPreferences.edit()
        editor.apply{
            when(who){
                "alphabet"->{
                    putBoolean("alphabet",value.toString().toBoolean())
                }
                "trChars"->{
                    putBoolean("trChars",value.toString().toBoolean())
                }
                "symbols"->{
                    putBoolean("symbols",value.toString().toBoolean())
                }
                "numbers"->{
                    putBoolean("numbers",value.toString().toBoolean())
                }
                "passLength"->{
                    putInt("passLength",value.toString().toInt())
                }
            }
        }.apply()

    }


    fun returnPassLength(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt("passLength", 10)
    }

    fun returnSelectedMethods(sharedPreferences: SharedPreferences): ArrayList<String> {
        val alphabet = sharedPreferences.getBoolean("alphabet", true)
        val trChars = sharedPreferences.getBoolean("trChars", true)
        val symbols = sharedPreferences.getBoolean("symbols", true)
        val numbers = sharedPreferences.getBoolean("numbers", true)
        val selectedMethods = ArrayList<String>()
        if (alphabet)
            selectedMethods.add("alphabet")
        if (trChars)
            selectedMethods.add("trChars")
        if (symbols)
            selectedMethods.add("symbols")
        if (numbers)
            selectedMethods.add("numbers")
        return selectedMethods
    }

    fun createPassword(length: Int, selectedMethods: ArrayList<String>): String {
        var tmpString = ""
        for (i in 0 until length) {
            val method = selectedMethods.random()
            if (method == "alphabet" || method == "trChars") {
                tmpString += if ((0..1).random() == 0)
                    this.methods[method]?.random().toString().toUpperCase(Locale.ROOT)
                else
                    this.methods[method]?.random().toString().toLowerCase(Locale.ROOT)
            } else
                tmpString += this.methods[method].random()
        }
        return tmpString
    }

}