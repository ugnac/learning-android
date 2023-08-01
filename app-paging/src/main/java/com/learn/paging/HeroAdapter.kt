package com.learn.paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

class HeroAdapter : PagingDataAdapter<HeroListItem, HeroViewHolder>(diffCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<HeroListItem>() {
            override fun areItemsTheSame(oldItem: HeroListItem, newItem: HeroListItem): Boolean {
                return if (oldItem is HeroListItem.Item && newItem is HeroListItem.Item) {
                    oldItem.hero.id == newItem.hero.id
                } else if (oldItem is HeroListItem.Separator && newItem is HeroListItem.Separator) {
                    oldItem.name == newItem.name
                } else {
                    oldItem == newItem
                }
            }

            /**
             * Note that in kotlin, == checking on data classes compares all contents, but in Java,
             * typically you'll implement Object#equals, and use it to compare object contents.
             */
            override fun areContentsTheSame(oldItem: HeroListItem, newItem: HeroListItem): Boolean {
                return oldItem == newItem
            }
        }
    }


    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        return HeroViewHolder(parent)
    }
}