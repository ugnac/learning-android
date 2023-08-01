package com.learn.paging

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HeroViewModelFactory(private val app: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HeroViewModel::class.java)){
            val heroDao = HeroDb.getInstance(app).heroDao()

            @Suppress("UNCHECKED_CAST") // Guaranteed to succeed at this point.
            return HeroViewModel(heroDao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}