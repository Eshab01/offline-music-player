package com.offlinemusicplayer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Genre(
    val id: Long = 0,
    val name: String,
    val color: String? = null,
    val description: String? = null,
    val songCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable