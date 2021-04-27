package net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator

import android.content.Context
import net.sqlcipher.database.SQLiteDatabase

class SQLiteCreator(context: Context, dbName: String, dbPassword: String) {
    val database : SQLiteDatabase
    companion object {
        const val tableName: String = "accounts"
    }
    init {
        val databaseFile = context.getDatabasePath("$dbName.db")
        database = SQLiteDatabase.openOrCreateDatabase(databaseFile, dbPassword, null)
        database.execSQL("create table $tableName (id integer primary key autoincrement, name text not null, password text not null, explanation text)")
    }

}