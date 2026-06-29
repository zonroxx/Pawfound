package com.example.androidpetapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


object AdminMode {

    /** Hardcoded demo PIN - intentionally not a real credential. */
    private const val PIN = "1234"

    private val _isAdmin = MutableLiveData(false)
    val isAdmin: LiveData<Boolean> = _isAdmin

    /** Current value for one-off checks (e.g. when an Activity opens). */
    val isAdminNow: Boolean
        get() = _isAdmin.value == true

    /** Returns true if the PIN was correct and admin mode is now enabled. */
    fun enableWithPin(pin: String): Boolean {
        val ok = pin == PIN
        if (ok) _isAdmin.value = true
        return ok
    }

    fun disable() {
        _isAdmin.value = false
    }
}
