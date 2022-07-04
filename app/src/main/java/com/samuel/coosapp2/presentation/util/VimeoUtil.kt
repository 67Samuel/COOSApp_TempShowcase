package com.samuel.coosapp2.presentation.util

import android.app.Application
import android.util.Log
import com.vimeo.networking2.*
//import com.vimeo.networking.AccountStore
//import com.vimeo.networking.Configuration
//import com.vimeo.networking.VimeoClient
import com.vimeo.networking2.account.AccountStore
import com.vimeo.networking2.config.VimeoApiConfiguration

class VimeoUtil {

    private val TAG: String = "VimeoDebug"

    fun getVimeoClientInstance(): VimeoApiConfiguration.Builder {
        return VimeoApiConfiguration.Builder(ACCESS_TOKEN)
    }

    fun initVimeoAuthenticatorWithAccessToken(application: Application): VimeoApiClient {

        val constantAccountStore = ConstantTokenAccountStore(ACCESS_TOKEN)

        val configuration = application.cacheDir?.let {
            VimeoApiConfiguration.Builder(CLIENT_ID,
                CLIENT_SECRET,
                listOf(ScopeType.PUBLIC, ScopeType.PRIVATE, ScopeType.VIDEO_FILES))
                .withAccountStore(constantAccountStore)
                .withCertPinning(false)
                .withCacheDirectory(it)
                .build()
        } ?: VimeoApiConfiguration.Builder(CLIENT_ID,
            CLIENT_SECRET,
            listOf(ScopeType.PUBLIC, ScopeType.PRIVATE, ScopeType.VIDEO_FILES))
            .withAccountStore(constantAccountStore)
            .withCertPinning(false)
            .build()

        configuration.cacheDirectory?.let {
            Log.d(TAG, "initVimeoAuthenticatorWithAccessToken: cacheDirectory found and configured")
        } ?: Log.d(TAG, "initVimeoAuthenticatorWithAccessToken: cacheDirectory not found")

        //Initialize the authenticator instance with the configuration and the api client.
        val authenticator = Authenticator(configuration)

        return VimeoApiClient(configuration, authenticator)
    }

    class ConstantTokenAccountStore(accessToken: String): AccountStore {
        private val account = VimeoAccount(accessToken = accessToken)

        override fun loadAccount(): VimeoAccount = account

        // We won't be supporting logging in
        override fun storeAccount(vimeoAccount: VimeoAccount) = Unit

        // We won't be supporting logging out
        override fun removeAccount() = Unit
    }

    companion object {
        const val CLIENT_ID = ""
        const val CLIENT_SECRET = ""
        const val ACCESS_TOKEN = ""

        const val CHANNEL_VIDEOS_ENDPOINT_URI = "/channels/1722446/videos"
        const val VIDEO_LIST_ENDPOINT_URI = "/me/videos?page=1"
    }

}