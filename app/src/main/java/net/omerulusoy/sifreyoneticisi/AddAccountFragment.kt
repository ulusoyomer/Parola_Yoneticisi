package net.omerulusoy.sifreyoneticisi

import android.accounts.Account
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import es.dmoral.toasty.Toasty


class AddAccountFragment : Fragment() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_account, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            AddAccountFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    fun createPassword(length: Int, selectedMethods: Array<String>): String {
        var tmpString = ""
        for (i in 0..length) {
            tmpString += this.methods[selectedMethods.random()].random()
        }
        return tmpString
    }
    class UserAccountList(private val cntx: Context, private val accounts: ArrayList<net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.Tables.Account>) :
        ArrayAdapter<net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.Tables.Account>(cntx, R.layout.account_list_row, accounts) {

        @SuppressLint("ViewHolder", "InflateParams")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(cntx)
            val view = inflater.inflate(R.layout.account_list_row, null)
            val tvAccountID = view.findViewById<TextView>(R.id.tvAccountID)
            val etAccountName = view.findViewById<EditText>(R.id.etAccountName)
            val etAccountPassword = view.findViewById<EditText>(R.id.etAccountPassword)
            val btnUpdate = view.findViewById<Button>(R.id.btnUpdate)
            val btnDelete = view.findViewById<Button>(R.id.btnDelete)
            val btnCopy = view.findViewById<Button>(R.id.btnCopy)

            tvAccountID.text = accounts[position].id.toString()
            etAccountName.setText(accounts[position].name)
            etAccountPassword.setText(accounts[position].password)

            btnUpdate.setOnClickListener {  }
            btnDelete.setOnClickListener {  }
            btnCopy.setOnClickListener {
                val clipboard = cntx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("${etAccountName.text} Password",etAccountPassword.text)
                clipboard.setPrimaryClip(clip)
                Toasty.success(cntx,"\"${etAccountName.text} Parolası Kopyalandı\"",Toast.LENGTH_SHORT).show()
            }


            return view
        }
    }
}