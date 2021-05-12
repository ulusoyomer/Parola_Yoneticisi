package net.omerulusoy.sifreyoneticisi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import es.dmoral.toasty.Toasty

class HomeActivity : AppCompatActivity() {
    var backTap = false
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
    private var navAnim = 0
    lateinit var bottomNavigation: MeowBottomNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ŞifreYöneticisi)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_home)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.add(MeowBottomNavigation.Model(0, R.drawable.ic_home))
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_add))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_settings))
        addFragment(HomeFragment.newInstance())
        bottomNavigation.show(0)

        bottomNavigation.setOnClickMenuListener {
            when (it.id) {
                0 -> {
                    replaceFragment(HomeFragment.newInstance())
                }
                1 -> {
                    replaceFragment(AddAccountFragment.newInstance())
                }
                2 -> {
                    replaceFragment(SettingsFragment.newInstance())
                }
                else -> {
                    Toasty.success(this, "Nothing", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onBackPressed() {
        if (navAnim <= 0){
            if (backTap)
                super.onBackPressed()
            else {
                Toasty.info(
                    this,
                    "Çıkmak İçin Birdaha Basın",
                    Toast.LENGTH_SHORT,
                    true
                )
                    .show()
                backTap = true
                Handler().postDelayed({
                    run {
                        backTap = false
                    }
                }, 2000)

            }
        }
        else{
            navAnim--
            super.onBackPressed()
        }

    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.setCustomAnimations(
            R.anim.animate_slide_up_enter,
            R.anim.animate_slide_down_exit
        )
        fragmentTransition.replace(R.id.frame_container, fragment)
            .addToBackStack(Fragment::class.java.simpleName).commit()
        navAnim++
    }

    fun addFragment(fragment: Fragment) {
        val fragmentTransition = supportFragmentManager.beginTransaction()

        fragmentTransition.add(R.id.frame_container, fragment)
            .addToBackStack(Fragment::class.java.simpleName).commit()

    }

    fun createPassword(length: Int, selectedMethods: Array<String>): String {
        var tmpString = ""
        for (i in 0..length) {
            tmpString += this.methods[selectedMethods.random()].random()
        }
        return tmpString
    }
}