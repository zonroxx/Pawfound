package com.example.androidpetapp.data.remote

fun normalizeEmulatorImageUrl(raw: String?): String? {
    if (raw.isNullOrBlank()) return raw
    return raw
        .replace("http://localhost:", "http://10.0.2.2:")
        .replace("http://127.0.0.1:", "http://10.0.2.2:")
        .replace(Regex("(?<!:)//photos"), "/photos")
}
