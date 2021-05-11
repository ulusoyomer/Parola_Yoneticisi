package net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteConnector

import android.content.ContentValues
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.SQLiteCreator
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.Tables.Account
import net.sqlcipher.database.SQLiteDatabase
import java.io.File
import java.util.ArrayList

object SQLiteConnector {
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
        contentValues.put("explanation", account.explanation)
        return contentValues
    }

    fun addNewAccount(account: Account) {
        val contentValues = setAccount(account)
        db?.insert(SQLiteCreator.tableName, null, contentValues)
    }

    fun updateAccount(id: Int, account: Account) {
        val contentValues = setAccount(account)
        db?.update(SQLiteCreator.tableName, contentValues, "id = $id", null)
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
                    val explanation = cursor.getString(cursor.getColumnIndex("explanation"))

                    val tmpAccount = Account(id,name,password,explanation)
                    accountList.add(tmpAccount)
                    cursor.moveToNext()
                }
            }
        }
        cursor?.close()
        return accountList
    }
}