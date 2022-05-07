package com.example.photogallery.data

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

private const val KEY_QUERY = "query"
private const val KEY_LAST_RESULT_ID = "last_result_id"

object QueryStorage {
    fun saveQuery(context: Context, query: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(KEY_QUERY, query)
            }
    }

    fun getQuery(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(KEY_QUERY, "")!!
    }

    fun saveLastResultId(context: Context, lastResultId: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(KEY_LAST_RESULT_ID, lastResultId)
            }
    }

    fun getLastResultId(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(KEY_LAST_RESULT_ID, "")!!
    }
}