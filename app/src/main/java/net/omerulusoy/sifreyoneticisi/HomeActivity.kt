package net.omerulusoy.sifreyoneticisi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import es.dmoral.toasty.Toasty

class HomeActivity : AppCompatActivity() {
    private var backTap = false

    private lateinit var bottomNavigation: MeowBottomNavigation

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
        addFragment(HomeFragment.newInstance(this))
        bottomNavigation.show(0)

        bottomNavigation.setOnClickMenuListener {
            when (it.id) {
                0 -> {
                    replaceFragment(HomeFragment.newInstance(this))
                }
                1 -> {
                    replaceFragment(AddAccountFragment.newInstance(this))
                }
                2 -> {
                    replaceFragment(SettingsFragment.newInstance(this))
                }
                else -> {
                    Toasty.success(this, "Nothing", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onBackPressed() {

        if (backTap) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            super.onBackPressed()
        } else {
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

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.setCustomAnimations(
            R.anim.animate_slide_up_enter,
            R.anim.animate_slide_down_exit
        )
        fragmentTransition.replace(R.id.frame_container, fragment)
            .addToBackStack(Fragment::class.java.simpleName).commit()
    }

    private fun addFragment(fragment: Fragment) {
        val fragmentTransition = supportFragmentManager.beginTransaction()

        fragmentTransition.add(R.id.frame_container, fragment)
            .addToBackStack(Fragment::class.java.simpleName).commit()

    }


}