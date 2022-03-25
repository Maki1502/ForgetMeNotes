package edu.ib.forget_me_notes

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import edu.ib.forget_me_notes.Fragments.*
import edu.ib.forget_me_notes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_search -> {
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_add -> {
                moveToFragment(AddFragment())
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_notifications -> {
                moveToFragment(NotificationsFragment())
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_settings -> {
                moveToFragment(SettingsFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        moveToFragment(HomeFragment())
    }

    private fun moveToFragment(fragment: Fragment) {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }
}