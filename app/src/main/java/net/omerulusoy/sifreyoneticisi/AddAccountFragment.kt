package net.omerulusoy.sifreyoneticisi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup





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
}