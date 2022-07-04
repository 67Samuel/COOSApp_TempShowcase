package com.samuel.coosapp2.presentation.main

import android.content.Intent
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.samuel.coosapp2.R
import com.samuel.coosapp2.business.datasource.datastore.AppDataStore
import com.samuel.coosapp2.business.domain.util.StateMessageCallback
import com.samuel.coosapp2.databinding.ActivityMainBinding
import com.samuel.coosapp2.presentation.BaseActivity
import com.samuel.coosapp2.presentation.session.SessionEvents
import com.samuel.coosapp2.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity
@Inject
constructor() : BaseActivity() {

    @Inject
    lateinit var appDataStore: AppDataStore

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private var fullScreenMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        theme.applyStyle(R.style.Theme_COOSApp2, true)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setSystemUIVisibilityListener()
        setContentView(binding.root)

        // to handle navigation between fragments
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        subscribeObservers()
    }

    /**
     * Set app theme to dark or light
     */
    private fun triggerSetThemeEvent(isDarkTheme: Boolean) {
        sessionManager.onTriggerEvent(SessionEvents.SetAppTheme(isDarkTheme))
    }

    /**
     * If phone is in landscape mode, hide system bars for the fullscreen experience
     */
    private fun setSystemUIVisibilityListener() {
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) { // system bars are visible
                if (fullScreenMode and (resources.configuration.orientation == ORIENTATION_LANDSCAPE)) {
                    hideSystemUI()
                }
            }
        }
    }

    /**
     * Hides system and navigation bars.
     * Any interaction with the app does not bring the bars back.
     * Only system gestures (like swiping down from the top of the phone) will bring the bars back.
     */
    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                // NOTE: or is bitwise OR. We use it here as a logical AND.
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
        expandAppBar(false)
    }

    /**
     * Subscribe observers to handle changes to the SessionState which keeps track of:
     * - The loading status
     * - The theme
     * - A queue of state messages
     */
    private fun subscribeObservers() {
        sessionManager.state.observe(this) { state ->
            displayProgressBar(state.isLoading)

            processQueue(
                context = this,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        sessionManager.onTriggerEvent(SessionEvents.OnRemoveHeadFromQueue)
                    }
                })

            state.isDarkTheme.let { isDarkTheme ->
                if (isDarkTheme) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }

    /**
     * Adds the theme toggle icon to the app bar menu
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        if (sessionManager.state.value?.isDarkTheme == true) {
            menu?.findItem(R.id.toggle_theme_menu_item)?.setIcon(R.drawable.ic_light_mode)
        } else {
            menu?.findItem(R.id.toggle_theme_menu_item)?.setIcon(R.drawable.ic_dark_mode)
        }
        return true
    }

    /**
     * Handles selection of the toggle theme button.
     * Theme is handled in MainActivity because it should be applied across the entire app (and all fragments)
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.toggle_theme_menu_item -> {
                if (sessionManager.state.value?.isDarkTheme == true) {
                    triggerSetThemeEvent(false)
                } else {
                    triggerSetThemeEvent(true)
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * Performs necessary operations on the UI for an immersive fullscreen view
     * - It seems that once the activity is destroyed, systemUI goes back to normal, so we don't need to explicitly show it
     */
    override fun toggleFullScreenMode(isFullScreen: Boolean) {
        fullScreenMode = isFullScreen
        if (isFullScreen) {
            hideSystemUI()
        }
    }

    /**
     * Returns a boolean indicating whether the phone is in fullscreen mode
     */
    override fun isFullScreenMode(): Boolean { return fullScreenMode }

    /**
     * Toggles the display of the progressbar
     */
    override fun displayProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = VISIBLE
        } else {
            binding.progressBar.visibility = GONE
        }
    }

    /**
     * Toggles the visibility of the app bar
     */
    override fun expandAppBar(expand: Boolean) {
        if (findViewById<AppBarLayout>(R.id.app_bar) != null) {
            findViewById<AppBarLayout>(R.id.app_bar).setExpanded(expand)
            if (expand) {
                findViewById<AppBarLayout>(R.id.app_bar).visibility = VISIBLE
            } else {
                findViewById<AppBarLayout>(R.id.app_bar).visibility = GONE
            }
        }
    }

}