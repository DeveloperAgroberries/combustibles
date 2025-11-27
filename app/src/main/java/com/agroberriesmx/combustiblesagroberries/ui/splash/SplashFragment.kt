package com.agroberriesmx.combustiblesagroberries.ui.splash
//
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import com.agroberriesmx.combustiblesagroberries.R
//import com.agroberriesmx.combustiblesagroberries.ui.home.MainActivity
//import com.agroberriesmx.combustiblesagroberries.ui.login.LoginActivity
//import com.agroberriesmx.combustiblesagroberries.ui.privacypolicy.PrivacyPolicyActivity
//
//class SplashFragment: Fragment() {
//    companion object {
//        private const val PERSISTENT_PREFERENCES_KEY = "persistent_prefs"
//        private const val SESSION_PREFERENCES_KEY = "session_prefs"
//        private const val POLICIES_SHOWN_KEY = "policies_shown"
//        private const val LOGGED_IN_KEY = "logged_in"
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val persistentPrefs = requireActivity().getSharedPreferences(PERSISTENT_PREFERENCES_KEY, Context.MODE_PRIVATE)
//        val sessionPrefs = requireActivity().getSharedPreferences(SESSION_PREFERENCES_KEY, Context.MODE_PRIVATE)
//
//        val policiesShown = persistentPrefs.getBoolean(POLICIES_SHOWN_KEY, false)
//        val loggedIn = sessionPrefs.getBoolean(LOGGED_IN_KEY, false)
//
//        val navController = findNavController()
//
//        when {
//            !policiesShown -> navController.navigate(R.id.action_splash_to_privacyPolicy)
//            !loggedIn -> navController.navigate(R.id.action_splash_to_login)
//            else -> navController.navigate(R.id.action_splash_to_fuel)
//        }
//    }
//}