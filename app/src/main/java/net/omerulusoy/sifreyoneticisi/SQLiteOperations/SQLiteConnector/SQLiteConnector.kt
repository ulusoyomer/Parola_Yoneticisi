package net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteConnector

import android.content.ContentValues
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.SQLiteCreator
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.Tables.Account
import net.sqlcipher.database.SQLiteDatabase
import java.io.File
import java.util.ArrayList

object SQLiteConnector {
    private val alphabet = arrayOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
        "P", "R", "S", "T", "U", "V", "Y", "Z", "X", "W"
    )
    private val trChars = arrayOf("Ç", "Ğ", "İ", "Ö", "Ş", "Ü", "ı")
    private val symbols = arrayOf(
        "~",
        "`",
        "!",
        "@",
        "#",
        "£",
        "€",
        "$",
        "¢",
        "¥",
        "§",
        "%",
        "°",
        "^",
        "&",
        "*",
        "(",
        ")",
        "-",
        "_",
        "+",
        "=",
        "{",
        "}",
        "[",
        "]",
        "|",
        "\\",
        "/",
        ":",
        ";",
        "\"",
        "'",
        "<",
        ">",
        ",",
        ".",
        "?"
    )
    private val numbers = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    private val methods = mapOf(
        "alphabet" to alphabet, "trChars" to trChars,
        "symbols" to symbols, "numbers" to numbers
    )



    private var db: SQLiteDatabase? = null
    const val dbPath = "data/data/net.omerulusoy.sifreyoneticisi/databases"


    fun openDB(dbFile:File,dbPassword:String) {
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
        val cursor = db?.rawQuery(String.format("SELECT * FROM '%s';", SQLiteCreator.tableName),null)
        if (cursor != null) {
            if (cursor.moveToFirst()){
                while (!cursor.isAfterLast){

                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val password = cursor.getString(cursor.getColumnIndex("password"))

                    val tmpAccount = Account(id,name,password)
                    accountList.add(tmpAccount)
                    cursor.moveToNext()
                }
            }
        }
        cursor?.close()
        return accountList
    }

    fun createPassword(length: Int, selectedMethods: Array<String>): String {
        var tmpString = ""
        for (i in 0..length) {
            tmpString += this.methods[selectedMethods.random()].random()
        }
        return tmpString
    }

}