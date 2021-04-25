package net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteConnector

import android.content.ContentValues
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.SQLiteCreator
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.Tables.Account
import net.sqlcipher.database.SQLiteDatabase

object SQLiteConnector {
    lateinit var db: SQLiteDatabase
    fun openOrCreateDatabase(dbCreator:SQLiteCreator){
        db = dbCreator.database
    }
    fun closeDatabase(){
        db.close()
    }
    fun addNewAccount(account:Account){
        val contentValues = ContentValues()
        contentValues.put("name",account.name)
        contentValues.put("password",account.password)
        contentValues.put("explanation",account.explanation)
        db.insert(SQLiteCreator.tableName,null,contentValues)
    }
}