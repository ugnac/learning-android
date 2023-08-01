package com.learn.paging

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * A simple [AndroidViewModel] that provides a [Flow]<[PagingData]> of heroes.
 */
class HeroViewModel(private val dao: HeroDao) : ViewModel() {
    /**
     * We use the Kotlin [Flow] property available on [Pager]. Java developers should use the
     * RxJava or LiveData extension properties available in `PagingRx` and `PagingLiveData`.
     */
    val heroes: Flow<PagingData<HeroListItem>> = Pager(
        config = PagingConfig(
            pageSize = 60,  // 一次性加载的数量
            enablePlaceholders = true,
            maxSize = 200 // 内存中可以保存的最大数，多余的会被回收掉
        )
    ) {
        dao.allHeroes()
    }.flow
        .map { pagingData ->
            pagingData
                // Map hero to common UI model.
                .map { hero ->
                    HeroListItem.Item(hero)
                }
                .insertSeparators { before, after ->
                    if (before == null || after == null) {
                        null// List is empty after fully loaded; return null to skip adding separator.
                    } else if (after == null) {
                        null// Footer; return null here to skip adding a footer.
                    } else if (before == null) {
                        HeroListItem.Separator(after.name.first())// Header
                    } else if (!before.name.first().equals(after.name.first(), ignoreCase = true)) {
                        HeroListItem.Separator(after.name.first())// Between two items that start with different letters.
                    } else {
                        null// Between two items that start with the same letter.
                    }
                }
        }
        .cachedIn(viewModelScope)

    fun insert(text: CharSequence) = ioThread {
        dao.insert(Hero(id = 0, name = text.toString()))
    }

    fun remove(hero: Hero) = ioThread {
        dao.delete(hero)
    }
}