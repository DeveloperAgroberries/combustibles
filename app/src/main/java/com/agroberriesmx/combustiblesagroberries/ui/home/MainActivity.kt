package com.agroberriesmx.combustiblesagroberries.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.agroberriesmx.combustiblesagroberries.R
import com.agroberriesmx.combustiblesagroberries.databinding.ActivityMainBinding
import com.agroberriesmx.combustiblesagroberries.ui.base.BaseActivity
import com.agroberriesmx.combustiblesagroberries.ui.login.LoginActivity
import com.agroberriesmx.combustiblesagroberries.ui.privacypolicy.PrivacyPolicyActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var persistentPrefs: SharedPreferences
    private lateinit var sessionPrefs: SharedPreferences

    companion object {
        private const val PERSISTENT_PREFERENCES_KEY = "persistent_prefs"
        private const val SESSION_PREFERENCES_KEY = "session_prefs"
        private const val POLICIES_SHOWN_KEY = "policies_shown"
        private const val LOGGED_IN_KEY = "logged_in"
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Cambia el color de la barra de estado (status bar)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }

    private fun initUI() {
        appRun()
        initListener()
        initNavigation()
    }

    private fun initNavigation() {
        setSupportActionBar(binding.toolbar)

        val navHost =
            supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment
        navController = navHost.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navFuel,
                R.id.navListOfRecords,
                R.id.navSync,
                R.id.navAbout
            ), binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navLogout -> {
                    showExitConfirmationData()
                    true
                }

                else -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
            }
        }
    }

    private fun initListener() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackFunction()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun onBackFunction() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            showExitConfirmationData()
        }
    }

    private fun showExitConfirmationData() {
        AlertDialog.Builder(this)
            .setMessage("Quieres salir de la aplicacion?")
            .setCancelable(false)
            .setPositiveButton("Si") { dialog, _ ->
                dialog.dismiss()
                handleLogout()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun handleLogout() {
        sessionPrefs = getSharedPreferences(SESSION_PREFERENCES_KEY, MODE_PRIVATE)
        val editor = sessionPrefs.edit()
        editor.clear()
        editor.apply()

        navigateToLogin()
    }

    private fun appRun() {
        persistentPrefs = getSharedPreferences(PERSISTENT_PREFERENCES_KEY, MODE_PRIVATE)
        sessionPrefs = getSharedPreferences(SESSION_PREFERENCES_KEY, MODE_PRIVATE)
        //val policiesShown = persistentPrefs.getBoolean(POLICIES_SHOWN_KEY, false)
        val policiesShown = persistentPrefs.getBoolean(POLICIES_SHOWN_KEY, false)
        val loggedIn = sessionPrefs.getBoolean(LOGGED_IN_KEY, false)

        when {
            !policiesShown -> {
                val intent = Intent(this, PrivacyPolicyActivity::class.java)
                startActivity(intent)
                finish()
            }

            !loggedIn -> {
                navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}