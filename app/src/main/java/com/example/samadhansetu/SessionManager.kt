package com.example.samadhansetu

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages user session data locally using SharedPreferences.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("SamadhanSetuAppPrefs", Context.MODE_PRIVATE)

    companion object {
        // Keys for storing user data
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_REGISTRATION_NUMBER = "user_reg_no"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_PHONE_NUMBER = "user_phone"
        private const val KEY_ROOM_NUMBER = "user_room"
    }

    /**
     * Saves all user profile data at once, typically after login or registration.
     */
    fun saveUser(name: String, regNo: String, email: String, phone: String, room: String) {
        val editor = prefs.edit()
        editor.putString(KEY_USER_NAME, name)
        editor.putString(KEY_REGISTRATION_NUMBER, regNo)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PHONE_NUMBER, phone)
        editor.putString(KEY_ROOM_NUMBER, room)
        editor.apply() // Use apply() to save changes asynchronously
    }

    /**
     * Retrieves the stored user name.
     * @return The user's name, or null if not found.
     */
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    /**
     * Retrieves the stored registration number.
     */
    fun getRegistrationNumber(): String? {
        return prefs.getString(KEY_REGISTRATION_NUMBER, null)
    }

    // Add similar getter functions for other fields...
    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)
    fun getPhoneNumber(): String? = prefs.getString(KEY_PHONE_NUMBER, null)
    fun getRoomNumber(): String? = prefs.getString(KEY_ROOM_NUMBER, null)


    /**
     * Clears all stored session data, typically on logout.
     */
    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}