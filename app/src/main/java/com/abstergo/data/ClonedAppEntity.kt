package com.abstergo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cloned_apps")
data class ClonedAppEntity(
    @PrimaryKey
    val packageName: String,
    val displayName: String,
    val webUrl: String,
    val addedAt: Long = System.currentTimeMillis()
)
