package net.omerulusoy.sifreyoneticisi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

class HomeActivity : AppCompatActivity() {
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
        setTheme(R.style.Theme_ŞifreYöneticisi)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_home)
    }

    fun createPassword(length: Int, selectedMethods: Array<String>): String {
        var tmpString = ""
        for (i in 0..length) {
            tmpString += this.methods[selectedMethods.random()].random()
        }
        return tmpString
    }
}