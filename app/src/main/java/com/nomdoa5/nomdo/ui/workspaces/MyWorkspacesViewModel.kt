package com.nomdoa5.nomdo.ui.workspaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyWorkspacesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is My Workspaces Fragment"
    }

    val text: LiveData<String> = _text
}