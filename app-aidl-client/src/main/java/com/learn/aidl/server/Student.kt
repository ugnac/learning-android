package com.learn.aidl.server

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Student(var name: String?, var sex: String?, var age: Int, var score: Int) : Parcelable
