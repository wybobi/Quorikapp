package com.example.data

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class QuorikRepository(private val context: Context) {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val dbAdapter = moshi.adapter(QuorikDatabase::class.java).indent("  ")
    private val prefsAdapter = moshi.adapter(UserPrefs::class.java)

    private val dbFile: File get() = File(context.filesDir, "quorik_cms_v3.json")
    private val prefsFile: File get() = File(context.filesDir, "quorik_user_prefs_v3.json")

    private fun normalizeDb(db: QuorikDatabase): QuorikDatabase {
        val defaults = DefaultQuorikData.createDefaultDatabase()
        val mergedPageBanners = defaults.pageBanners.toMutableMap().apply {
            putAll(db.pageBanners)
        }
        return db.copy(
            staticBanners = db.staticBanners.ifEmpty { DefaultQuorikData.defaultStaticBanners },
            pageBanners = mergedPageBanners,
            syllabusSemesters = db.syllabusSemesters.ifEmpty { DefaultQuorikData.allSemesters },
            subjects = db.subjects.ifEmpty { DefaultQuorikData.allSubjects },
            levels = db.levels.ifEmpty { DefaultQuorikData.defaultLevels }
        )
    }

    suspend fun loadDatabase(): QuorikDatabase = withContext(Dispatchers.IO) {
        try {
            if (dbFile.exists()) {
                val json = dbFile.readText()
                val parsed = dbAdapter.fromJson(json)
                if (parsed != null) {
                    return@withContext normalizeDb(parsed)
                }
            }
        } catch (_: Exception) {
            // Fall back to default data
        }
        val defaults = DefaultQuorikData.createDefaultDatabase()
        saveDatabase(defaults)
        defaults
    }

    suspend fun saveDatabase(database: QuorikDatabase) = withContext(Dispatchers.IO) {
        try {
            dbFile.writeText(dbAdapter.toJson(database))
        } catch (_: Exception) {
        }
    }

    suspend fun importDatabaseJson(json: String): QuorikDatabase? = withContext(Dispatchers.IO) {
        try {
            val parsed = dbAdapter.fromJson(json) ?: return@withContext null
            val normalized = normalizeDb(parsed)
            saveDatabase(normalized)
            normalized
        } catch (_: Exception) {
            null
        }
    }

    suspend fun resetDatabase(): QuorikDatabase = withContext(Dispatchers.IO) {
        val defaults = DefaultQuorikData.createDefaultDatabase()
        saveDatabase(defaults)
        defaults
    }

    fun exportDatabaseJson(database: QuorikDatabase): String {
        return dbAdapter.toJson(database)
    }

    suspend fun loadUserPrefs(): UserPrefs = withContext(Dispatchers.IO) {
        try {
            if (prefsFile.exists()) {
                val parsed = prefsAdapter.fromJson(prefsFile.readText())
                if (parsed != null) return@withContext parsed
            }
        } catch (_: Exception) {
        }
        UserPrefs()
    }

    suspend fun saveUserPrefs(prefs: UserPrefs) = withContext(Dispatchers.IO) {
        try {
            prefsFile.writeText(prefsAdapter.toJson(prefs))
        } catch (_: Exception) {
        }
    }
}
