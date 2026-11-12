package com.example.suzukiconnect.data.notifications

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** One push notification as saved on the device. */
data class StoredNotification(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("receivedAtMillis") val receivedAtMillis: Long
)

/**
 * Keeps received push notifications on the device (newest first) so they stack up
 * on the Notification screen and survive app restarts.
 * [items] is observed by the UI, so a push arriving while the screen is open shows up immediately.
 */
class NotificationStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val listType = object : TypeToken<List<StoredNotification>>() {}.type

    private val _items = MutableStateFlow(load())
    val items: StateFlow<List<StoredNotification>> = _items.asStateFlow()

    /** Adds a notification on top. Ignores duplicates (same id), e.g. the same push seen twice. */
    @Synchronized
    fun add(notification: StoredNotification) {
        val current = _items.value
        if (current.any { it.id == notification.id }) return
        val updated = (listOf(notification) + current)
            .sortedByDescending { it.receivedAtMillis }
            .take(MAX_ITEMS)
        prefs.edit().putString(KEY_ITEMS, gson.toJson(updated)).apply()
        _items.value = updated
    }

    private fun load(): List<StoredNotification> =
        runCatching {
            prefs.getString(KEY_ITEMS, null)
                ?.let { gson.fromJson<List<StoredNotification>>(it, listType) }
        }.getOrNull().orEmpty()

    private companion object {
        const val PREFS_NAME = "push_notifications"
        const val KEY_ITEMS = "items"
        const val MAX_ITEMS = 100
    }
}