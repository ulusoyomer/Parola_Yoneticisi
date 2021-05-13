package net.omerulusoy.sifreyoneticisi

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import es.dmoral.toasty.Toasty
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteConnector.SQLiteConnector
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.Tables.Account
import java.util.ArrayList
import kotlin.Exception


class HomeFragment : Fragment() {

    private var lvAccountsList: ListView? = null
    private var searchView: SearchView? = null
    private lateinit var accountsList: ArrayList<Account>
    private lateinit var accountsListAdapter: UserAccountListAdapter
    lateinit var cntx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountsList = SQLiteConnector.getAllAccounts()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lvAccountsList = view.findViewById(R.id.lv_AccountsList)
        searchView = view.findViewById(R.id.svAccountSeatch)
        setAdapter(UserAccountListAdapter(cntx, accountsList))
        searchViewCreateListener()
    }

    fun setAdapter(adapter: UserAccountListAdapter){
        accountsListAdapter = adapter
        accountsListAdapter.notifyDataSetChanged()
        lvAccountsList?.adapter = accountsListAdapter
    }
    fun searchViewCreateListener(){
        searchView?.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                val filterAccounts = ArrayList<Account>()
                for (account in accountsList){
                    if (p0?.let { account.name.contains(it) } == true){
                        filterAccounts.add(account)
                        setAdapter(UserAccountListAdapter(cntx,filterAccounts))
                    }
                }
                return false
            }

        })
    }

    companion object {

        @JvmStatic
        fun newInstance(context: Context) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    cntx = context
                }
            }
    }

    inner class UserAccountListAdapter(
        private val cntx: Context,
        private var accounts: ArrayList<Account>
    ) :
        ArrayAdapter<Account>(cntx, R.layout.account_list_row, accounts) {

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

            btnUpdate.setOnClickListener { }
            btnDelete.setOnClickListener {
                AlertDialog.Builder(cntx)
                    .setTitle("Hesap Silme İşlemi")
                    .setMessage("${etAccountName.text} hesabı silmek istiyormusunuz ?")
                    .setPositiveButton("Evet") { _, _ ->
                        try {
                            SQLiteConnector.deleteAccount(tvAccountID.text.toString().toInt())
                            accountsList = SQLiteConnector.getAllAccounts()
                            setAdapter(UserAccountListAdapter(cntx, accountsList))
                            Toasty.success(cntx, "Hesap Başarıyla Silindi", Toast.LENGTH_SHORT)
                                .show()
                        } catch (e: Exception) {
                            Toasty.error(cntx, "Hesap Silinirken Hata Oluştu", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    .setNegativeButton("Hayır", null).show()
            }
            btnCopy.setOnClickListener {
                val clipboard = cntx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip =
                    ClipData.newPlainText("${etAccountName.text} Password", etAccountPassword.text)
                clipboard.setPrimaryClip(clip)
                Toasty.success(
                    cntx,
                    "\"${etAccountName.text} Parolası Kopyalandı\"",
                    Toast.LENGTH_SHORT
                ).show()
            }


            return view
        }
    }
}