package com.dicoding.asclepius.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class History(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "image")
    var image: ByteArray,

    @ColumnInfo(name = "label")
    var label: String,

    @ColumnInfo(name = "score")
    var score: Float,

    @ColumnInfo(name = "inference_time")
    var inferenceTime: Long,

    @ColumnInfo(name = "date")
    var date: String
) : Parcelable
