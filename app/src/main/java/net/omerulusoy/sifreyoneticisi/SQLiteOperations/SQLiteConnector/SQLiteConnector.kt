package net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteConnector

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    var dbPass: String = ""
    var dbName: String = ""

    fun writeStoragePermission(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                101
            )
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    fun openDB(dbFile: File, dbPassword: String) {
        dbPass = dbPassword
        dbName = dbFile.name
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
    fun changeSharedPreferences(sharedPreferences: SharedPreferences, value: Any, who: String) {
        val editor = sharedPreferences.edit()
        editor.apply {
            when (who) {
                "alphabet" -> {
                    putBoolean("alphabet", value.toString().toBoolean())
                }
                "trChars" -> {
                    putBoolean("trChars", value.toString().toBoolean())
                }
                "symbols" -> {
                    putBoolean("symbols", value.toString().toBoolean())
                }
                "numbers" -> {
                    putBoolean("numbers", value.toString().toBoolean())
                }
                "passLength" -> {
                    putInt("passLength", value.toString().toInt())
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

    fun getRealPathFromURI(context: Context, uri: Uri): String? {
        when {
            // DocumentProvider
            DocumentsContract.isDocumentUri(context, uri) -> {
                when {
                    // ExternalStorageProvider
                    isExternalStorageDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val type = split[0]
                        // This is for checking Main Memory
                        return if ("primary".equals(type, ignoreCase = true)) {
                            if (split.size > 1) {
                                Environment.getExternalStorageDirectory()
                                    .toString() + "/" + split[1]
                            } else {
                                Environment.getExternalStorageDirectory().toString() + "/"
                            }
                            // This is for checking SD Card
                        } else {
                            "storage" + "/" + docId.replace(":", "/")
                        }
                    }
                    isDownloadsDocument(uri) -> {
                        val fileName = getFilePath(context, uri)
                        if (fileName != null) {
                            return Environment.getExternalStorageDirectory()
                                .toString() + "/Download/" + fileName
                        }
                        var id = DocumentsContract.getDocumentId(uri)
                        if (id.startsWith("raw:")) {
                            id = id.replaceFirst("raw:".toRegex(), "")
                            val file = File(id)
                            if (file.exists()) return id
                        }
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(id)
                        )
                        return getDataColumn(context, contentUri, null, null)
                    }
                    isMediaDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val type = split[0]
                        var contentUri: Uri? = null
                        when (type) {
                            "image" -> {
                                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            }
                            "video" -> {
                                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            }
                            "audio" -> {
                                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                            }
                        }
                        val selection = "_id=?"
                        val selectionArgs = arrayOf(split[1])
                        return getDataColumn(context, contentUri, selection, selectionArgs)
                    }
                }
            }
            "content".equals(uri.scheme, ignoreCase = true) -> {
                // Return the remote address
                return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                    context,
                    uri,
                    null,
                    null
                )
            }
            "file".equals(uri.scheme, ignoreCase = true) -> {
                return uri.path
            }
        }
        return null
    }

    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            if (uri == null) return null
            cursor = context.contentResolver.query(
                uri, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }


    fun getFilePath(context: Context, uri: Uri?): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME
        )
        try {
            if (uri == null) return null
            cursor = context.contentResolver.query(
                uri, projection, null, null,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

}