package com.samuel.coosapp2.presentation.session

sealed class SessionEvents {

    data class SetAppTheme(val isDarkTheme: Boolean): SessionEvents()

    object OnRemoveHeadFromQueue: SessionEvents()

}