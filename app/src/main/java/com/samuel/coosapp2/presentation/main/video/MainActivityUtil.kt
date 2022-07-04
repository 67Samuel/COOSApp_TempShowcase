package com.samuel.coosapp2.presentation.main.video

import android.app.Activity
import com.samuel.coosapp2.presentation.main.MainActivity
import javax.inject.Inject

class MainActivityUtil
@Inject
constructor(
    val activity: MainActivity
) {

    fun getMainActivity(): Activity {
        return activity
    }
}