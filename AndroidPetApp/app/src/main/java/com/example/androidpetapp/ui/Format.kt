package com.example.androidpetapp.ui

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale


fun formatIntakeDate(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        parser.isLenient = false
        val date = parser.parse(raw.trim()) ?: return null
        SimpleDateFormat("MMMM d, yyyy", Locale.US).format(date)
    } catch (e: Exception) {
        null
    }
}

/** Formats an adoption fee as Philippine peso currency (e.g. "₱1,500.00"). */
fun formatFee(fee: Double?): String? {
    if (fee == null) return null
    if (fee == 0.0) return "Free"
    return try {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-PH")).format(fee)
    } catch (e: Exception) {
        "₱$fee"
    }
}
