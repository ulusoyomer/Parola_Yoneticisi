package net.omerulusoy.sifreyoneticisi


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import es.dmoral.toasty.Toasty
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteConnector.SQLiteConnector
import net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.Tables.Account


class AddAccountFragment : Fragment() {

    lateinit var etAddAccountName: EditText
    lateinit var etAddAccountPassword: EditText
    lateinit var btnAddAddAccount: Button
    lateinit var btnAddAutoPass: Button
    lateinit var swAlphabet: Switch
    lateinit var swTRChar: Switch
    lateinit var swSymbols: Switch
    lateinit var swNumbers: Switch
    lateinit var npPassSize: NumberPicker
    lateinit var cntx: Context

    private lateinit var sharedPreferences: SharedPreferences

    lateinit var mAdView : AdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etAddAccountName = view.findViewById(R.id.etAddAccountName)
        etAddAccountPassword = view.findViewById(R.id.etAddAccountPassword)
        btnAddAddAccount = view.findViewById(R.id.btnAddAddAccount)
        btnAddAutoPass = view.findViewById(R.id.btnAddAutoPass)
        swAlphabet = view.findViewById(R.id.swAlphabet)
        swTRChar = view.findViewById(R.id.swTRChar)
        swSymbols = view.findViewById(R.id.swSymbols)
        swNumbers = view.findViewById(R.id.swNumbers)
        npPassSize = view.findViewById(R.id.npPassSize)
        sharedPreferences =
            cntx.getSharedPreferences("net.omerulusoy.sifreyoneticisi", Context.MODE_PRIVATE)
        npPassSize.maxValue = 30
        npPassSize.minValue = 5
        npPassSize.value = SQLiteConnector.returnPassLength(sharedPreferences)

        MobileAds.initialize(cntx) {}
        mAdView = view.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        btnAddAddAccount.setOnClickListener {
            val username = etAddAccountName.text.toString()
            val password = etAddAccountPassword.text.toString()
            if (username.isNotEmpty() || password.isNotEmpty()) {
                try {
                    SQLiteConnector.addNewAccount(Account(username, password))
                    Toasty.success(cntx, "Hesap Başarıyla Eklendi", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toasty.error(cntx, "Hesap Eklendirken Hata Oldu", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnAddAutoPass.setOnClickListener {
            val tmpPass = SQLiteConnector.createPassword(
                SQLiteConnector.returnPassLength(sharedPreferences),
                SQLiteConnector.returnSelectedMethods(sharedPreferences)
            )

            etAddAccountPassword.setText(tmpPass)
        }
        val selectedMethods = SQLiteConnector.returnSelectedMethods(sharedPreferences)

        for (each in selectedMethods){
            when(each){
                "alphabet" -> swAlphabet.isChecked = true
                "trChars" -> swTRChar.isChecked = true
                "symbols" -> swSymbols.isChecked = true
                "numbers" -> swNumbers.isChecked = true
            }
        }

        swAlphabet.setOnCheckedChangeListener { _, _ ->
            if (switchController())
                SQLiteConnector.changeSharedPreferences(sharedPreferences,swAlphabet.isChecked,"alphabet")
            else
                swAlphabet.isChecked = true
        }
        swTRChar.setOnCheckedChangeListener { _, _ ->
            if (switchController())
                SQLiteConnector.changeSharedPreferences(sharedPreferences,swTRChar.isChecked,"trChars")
            else
                swTRChar.isChecked = true
        }
        swSymbols.setOnCheckedChangeListener { _, _ ->
            if (switchController())
                SQLiteConnector.changeSharedPreferences(sharedPreferences,swSymbols.isChecked,"symbols")
            else
                swSymbols.isChecked = true
        }
        swNumbers.setOnCheckedChangeListener { _, _ ->
            if (switchController())
                SQLiteConnector.changeSharedPreferences(sharedPreferences,swNumbers.isChecked,"numbers")
            else
                swNumbers.isChecked = true
        }

        npPassSize.setOnValueChangedListener { _, _, _ ->
            SQLiteConnector.changeSharedPreferences(sharedPreferences,npPassSize.value,"passLength")
        }

    }

    fun switchController():Boolean{
        if (!swAlphabet.isChecked && !swNumbers.isChecked && !swSymbols.isChecked && !swTRChar.isChecked)
            return false
        return true
    }

    companion object {

        @JvmStatic
        fun newInstance(context: Context) =
            AddAccountFragment().apply {
                arguments = Bundle().apply {
                    cntx = context
                }
            }
    }


}