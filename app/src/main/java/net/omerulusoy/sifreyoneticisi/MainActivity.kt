package net.omerulusoy.sifreyoneticisi


import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import es.dmoral.toasty.Toasty
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteConnector.SQLiteConnector
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.SQLiteCreator
import net.sqlcipher.database.SQLiteDatabase
import java.io.File
import java.lang.Exception


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
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                dbImportCode -> {
                    try {
                        val dbFile = File(data.data?.let { getPathFromUri(this, it) })
                        val newDBFile = getDatabasePath(dbFile.name)
                        if (newDBFile.exists()) newDBFile.delete()
                        newDBFile.mkdirs()
                        newDBFile.delete()
                        dbFile.copyTo(newDBFile)
                        Toasty.success(this, "Veritabanı Aktarıldı", Toast.LENGTH_SHORT, true)
                            .show()
                    } catch (e: Exception) {
                        Toasty.error(this, "Veritabanı Aktarılamadı", Toast.LENGTH_SHORT, true)
                            .show()
                    }finally {
                        updateDBList()
                    }
                }
            }
        }
    }


    fun showChooser(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(intent, dbImportCode)
    }

    fun getAllDbNames() {
        dbFiles = File(SQLiteConnector.dbPath).listFiles()
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

    fun updateDBList(){
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
                    if (!dbNames.contains(dbName.text.toString())) {
                        val db =
                            SQLiteCreator(this, dbName.text.toString(), password.text.toString())
                            updateDBList()
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

    fun getPathFromUri(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri: Uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.getScheme(), ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.getLastPathSegment() else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
            return uri.getPath()
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
            cursor = uri?.let {
                context.contentResolver.query(
                    it, projection, selection, selectionArgs,
                    null
                )
            }
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.getAuthority()
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.getAuthority()
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.getAuthority()
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.getAuthority()
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