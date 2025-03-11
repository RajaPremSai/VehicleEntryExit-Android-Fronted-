package com.example.vehicleentryexit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.vehicleentryexit.sg.SGAnnouncementsFragment
import com.example.vehicleentryexit.sg.SGHomeFragment
import com.example.vehicleentryexit.sg.SGLogsFragment
import com.example.vehicleentryexit.sg.SGProfileFragment
import com.example.vehicleentryexit.sg.SGScanBarcodeFragment
import com.google.android.material.navigation.NavigationView

class SGHomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sghome)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // Set Toolbar
        setSupportActionBar(toolbar)

        // Enable Drawer Toggle
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, SGHomeFragment())
                .commit()
        }

        // Handle Navigation Item Clicks
//        navigationView.setNavigationItemSelectedListener { menuItem ->
//            menuItem.isChecked = true
//            drawerLayout.closeDrawers()
//
//
//            val selectedFragment: Fragment = when (menuItem.itemId) {
//                R.id.nav_home -> SGHomeFragment()
//                R.id.nav_profile -> SGProfileFragment()
//                R.id.nav_logs -> SGLogsFragment()
//                R.id.nav_announcements -> SGAnnouncementsFragment()
//                R.id.nav_scan -> SGScanBarcodeFragment()
//                else -> SGHomeFragment()
//            }
//
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainerView, selectedFragment)
//                .commit()
//
//            true
//    }
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()

            when (menuItem.itemId) {
                R.id.nav_home -> loadFragment(SGHomeFragment())
                R.id.nav_profile -> loadFragment(SGProfileFragment())
                R.id.nav_logs -> loadFragment(SGLogsFragment())
                R.id.nav_announcements -> loadFragment(SGAnnouncementsFragment())
                R.id.nav_scan -> loadFragment(SGScanBarcodeFragment())
                R.id.action_logout -> {
                    logout() // Call your logout function
                    return@setNavigationItemSelectedListener true
                }
            }

            true
        }

    }
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    private fun logout() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply() // Clear all stored data

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
        startActivity(intent)
        finish()
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }
}