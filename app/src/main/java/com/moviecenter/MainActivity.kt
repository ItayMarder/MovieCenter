package com.app.moviecenter

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var headerView: View
    private lateinit var toolbar: Toolbar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        headerView = navigationView.getHeaderView(0)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        setupNavController()
        setupDrawer()
        setupNavigationView()

        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser //
        if (user != null) {
            navController.navigate(R.id.action_loginFragment_to_reviewsFragment)
        }
    }

    private fun setupNavController() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.findNavController()
        setupActionBarWithNavController(navController)
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupNavigationView() {
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.reviewsFragment,
                R.id.profileFragment,
                R.id.LogoutFragment,
                R.id.myReviewsFragment,
            ), drawerLayout
        )
        findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)

        // hide toolbar in login page
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment) {
                toolbar.visibility = View.GONE
            } else {
                toolbar.visibility = View.VISIBLE
            }
        }

        navigationView.setupWithNavController(navController)
        handleLogout()

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun handleLogout() {
        navigationView.menu
            .findItem(R.id.LogoutFragment)
            .setOnMenuItemClickListener { _ ->
                logoutDialog()
                true
            }
    }

    private fun logoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                auth = FirebaseAuth.getInstance()
                auth.signOut()
                navController.navigate(R.id.loginFragment)
                drawerLayout.closeDrawer(GravityCompat.START)

            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

