package com.learn.paging

/**
 * Common UI model between the [Hreo] data class and separators.
 */
sealed class HeroListItem(val name: String) {
    data class Item(val hero: Hero) : HeroListItem(hero.name!!)
    data class Separator(private val letter: Char) : HeroListItem(letter.toUpperCase().toString())
}