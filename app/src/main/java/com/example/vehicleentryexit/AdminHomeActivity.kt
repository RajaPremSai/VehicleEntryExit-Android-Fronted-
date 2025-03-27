package com.example.vehicleentryexit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.vehicleentryexit.manager.ManagerAnnouncementsFragment
import com.example.vehicleentryexit.manager.ManagerGates
import com.example.vehicleentryexit.manager.ManagerHomeFragment
import com.example.vehicleentryexit.manager.ManagerLogs
import com.example.vehicleentryexit.manager.ManagerProfile
import com.example.vehicleentryexit.manager.ManagerSecurityGuards
import com.example.vehicleentryexit.manager.ManagerUniversityVehicles
import com.example.vehicleentryexit.manager.ManagerUsers
import com.example.vehicleentryexit.manager.ManagerVehicles
import com.google.android.material.navigation.NavigationView

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_home)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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
                .replace(R.id.fragmentContainerView, ManagerHomeFragment())
                .commit()
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()

            when (menuItem.itemId) {
                R.id.nav_home -> loadFragment(ManagerHomeFragment())
                R.id.nav_profile -> loadFragment(ManagerProfile())
                R.id.nav_logs -> loadFragment(ManagerLogs())
                R.id.nav_announcements -> loadFragment(ManagerAnnouncementsFragment())
//                R.id.nav_users -> loadFragment(ManagerUsers())
                R.id.nav_sg -> loadFragment(ManagerSecurityGuards())
//                R.id.nav_vehicles -> loadFragment(ManagerVehicles())
                R.id.nav_uni_vehicles -> loadFragment(ManagerUniversityVehicles())
                R.id.nav_gates-> loadFragment(ManagerGates())
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