package com.samuel.coosapp2.presentation

interface UICommunicationListener {

    fun displayProgressBar(isLoading: Boolean)

    fun toggleFullScreenMode(isFullScreen: Boolean)

    fun isFullScreenMode(): Boolean

    fun expandAppBar(expand: Boolean)

//    fun expandTabLayout(expand: Boolean)

//    fun getWindowDimensions(): List<Int>

//    fun toggleTheme()
//
    fun hideSoftKeyboard()
//
//    fun isStoragePermissionGranted(): Boolean
}