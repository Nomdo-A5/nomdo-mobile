package com.nomdoa5.nomdo.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TasksViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Task Fragment"
    }
    val text: LiveData<String> = _text
}