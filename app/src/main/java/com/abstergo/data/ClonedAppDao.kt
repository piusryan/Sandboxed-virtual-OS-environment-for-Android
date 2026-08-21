package com.abstergo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClonedAppDao {

    @Query("SELECT * FROM cloned_apps ORDER BY addedAt DESC")
    fun getAllClonedApps(): Flow<List<ClonedAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClonedApp(app: ClonedAppEntity)

    @Delete
    suspend fun deleteClonedApp(app: ClonedAppEntity)

    @Query("DELETE FROM cloned_apps WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)

    @Query("SELECT COUNT(*) FROM cloned_apps WHERE packageName = :packageName")
    suspend fun isCloned(packageName: String): Int
}
