package com.learn.paging

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Hero(@PrimaryKey(autoGenerate = true) val id: Int, val name: String?, val sex: String? = null)
