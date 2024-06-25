package com.example.movieapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.util.TableInfo
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "saved_items_table",
    indices = [Index(value = ["itemId"], unique = true)]
)
data class SavedItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @SerializedName("item_id")
    val itemId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("media_type")
    val mediaType: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("rating")
    val rating: Float
)