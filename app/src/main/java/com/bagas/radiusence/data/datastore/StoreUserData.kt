package com.bagas.radiusence.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bagas.radiusence.data.model.Users
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreUserData(private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore("UserData")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_NIM_KEY = stringPreferencesKey("user_nim")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val USER_FACULTY_KEY = stringPreferencesKey("user_faculty")
        val USER_MAJOR_KEY = stringPreferencesKey("user_major")
        val USER_TYPE_KEY = stringPreferencesKey("user_type")
        val USER_AVATAR_KEY = stringPreferencesKey("user_avatar")
    }

    val getData: Flow<Users> = context.dataStore.data
        .map { preferences ->
            val name = preferences[USER_NAME_KEY] ?: ""
            val nim = preferences[USER_NIM_KEY] ?: ""
            val email = preferences[USER_EMAIL_KEY] ?: ""
            val faculty = preferences[USER_FACULTY_KEY] ?: ""
            val major = preferences[USER_MAJOR_KEY] ?: ""
            val type = preferences[USER_TYPE_KEY] ?: ""
            val avatar = preferences[USER_AVATAR_KEY] ?: ""

            Users(name, nim, email, faculty, major, type, avatar)
        }

    suspend fun saveData(name: String, nim: String, email: String, faculty: String, major: String, type: String, avatar: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
            preferences[USER_NIM_KEY] = nim
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_FACULTY_KEY] = faculty
            preferences[USER_MAJOR_KEY] = major
            preferences[USER_TYPE_KEY] = type
            preferences[USER_AVATAR_KEY] = avatar
        }

        Log.d("Data", "Name : $name")
    }

    suspend fun clearDataStore() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

}