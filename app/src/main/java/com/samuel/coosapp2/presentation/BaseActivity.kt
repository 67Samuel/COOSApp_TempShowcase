package com.samuel.coosapp2.presentation

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.samuel.coosapp2.R
import com.samuel.coosapp2.presentation.session.SessionManager
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), UICommunicationListener {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_COOSApp2)
    }

    override fun displayProgressBar(isLoading: Boolean) {
    }

    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

}