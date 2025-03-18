package com.example.vehicleentryexit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle

//class UserHomeActivity : AppCompatActivity() {
//
//    private lateinit var drawerLayout: DrawerLayout
//    private lateinit var navigationView: NavigationView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_user_home)
//
//        // Set up the DrawerLayout
//        drawerLayout = findViewById(R.id.drawer_layout)
//
//        // Set up the NavigationView
//        navigationView = findViewById(R.id.nav_view)
//
//        // Set up ActionBar and the Toggle for the Drawer
//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//
//        val toggle = ActionBarDrawerToggle(
//            this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer
//        )
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//
//        // Set up Navigation Item Click Listener
//        navigationView.setNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.nav_home -> {
//                    // Handle Home item
//                }
//                R.id.nav_profile -> {
//                    // Handle Profile item
//                }
//                R.id.nav_logout -> {
//                    // Handle Logout item
//                }
//            }
//
//            // Close the drawer after selection
//            drawerLayout.closeDrawer(GravityCompat.START)
//            true
//        }
//
//        // Handle Menu Icon Click
//        val menuIcon = findViewById<ImageView>(R.id.menuIcon)
//        menuIcon.setOnClickListener {
//            drawerLayout.openDrawer(GravityCompat.START)
//        }
//    }
//}

//import android.os.Bundle
//import android.view.Gravity
//import android.view.MenuItem
//import android.widget.ImageView
//import androidx.appcompat.app.ActionBarDrawerToggle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.GravityCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import com.google.android.material.navigation.NavigationView

class UserHomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var profileIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val menuIcon: ImageView = findViewById(R.id.menuIcon)
        val profileIcon: ImageView = findViewById(R.id.imageView2)

        // Set click listener on the menu icon to open the drawer
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        profileIcon.setOnClickListener{
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    val intent = Intent(this, UserProfileActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_vehicles -> {
                    val intent = Intent(this, UserVehiclesActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_logs -> {
                    val intent = Intent(this, UserLogsActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_announcements -> {
                    val intent = Intent(this, UserAnnouncementsActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_logout -> {
                    logout()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // ActionBarDrawerToggle for syncing the drawer state
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.open, R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply() // Clear all stored data

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}

