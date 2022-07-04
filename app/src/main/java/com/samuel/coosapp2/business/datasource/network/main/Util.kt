package com.samuel.coosapp2.business.datasource.network.main

import androidx.lifecycle.MutableLiveData

class Util {

    fun <T> setDataState(element: T, liveData: MutableLiveData<T>) {
        liveData.value = liveData.value
    }
}