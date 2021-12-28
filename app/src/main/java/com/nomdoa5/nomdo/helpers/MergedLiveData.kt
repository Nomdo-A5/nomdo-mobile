package com.nomdoa5.nomdo.helpers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class MergedLiveData<T, K, S>(sourceA: LiveData<T>, sourceB: LiveData<K>, private val mergedCallback: (resultA: T?, resultB: K?) -> S) : MediatorLiveData<S>() {

    private var resultA: T? = null
    private var resultB: K? = null

    init {
        addSource(sourceA) {
            resultA = it
            value = mergedCallback(resultA, resultB)
        }
        addSource(sourceB) {
            resultB = it
            value = mergedCallback(resultA, resultB)
        }
    }
}