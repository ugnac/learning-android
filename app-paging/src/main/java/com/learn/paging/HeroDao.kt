package com.learn.paging

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * Database Access Object for the Student database.
 */
@Dao
interface HeroDao {
    /**
     * Room knows how to return a LivePagedListProvider, from which we can get a LiveData and serve
     * it back to UI via ViewModel.
     */
    @Query("SELECT * FROM Hero ORDER BY name COLLATE NOCASE ASC")
    fun allHeroes(): PagingSource<Int, Hero>

    @Insert
    fun insert(student: Hero)

    @Insert
    fun insertAll(students: List<Hero>)

    @Delete
    fun delete(student: Hero)
}